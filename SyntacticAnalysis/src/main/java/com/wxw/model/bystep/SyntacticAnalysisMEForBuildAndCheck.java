package com.wxw.model.bystep;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.sequence.DefaultSyntacticAnalysisSequenceValidator;
import com.wxw.sequence.SyntacticAnalysisBeamSearch;
import com.wxw.sequence.SyntacticAnalysisSequenceClassificationModel;
import com.wxw.sequence.SyntacticAnalysisSequenceForBuildAndCheck;
import com.wxw.sequence.SyntacticAnalysisSequenceForChunk;
import com.wxw.sequence.SyntacticAnalysisSequenceValidator;
import com.wxw.stream.FileInputStreamFactory;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.stream.SyntacticAnalysisSampleStream;
import com.wxw.tree.PhraseGenerateTree;
import com.wxw.tree.TreeNode;
import com.wxw.tree.TreeToActions;

import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.TrainerFactory.TrainerType;
import opennlp.tools.ml.maxent.io.PlainTextGISModelReader;
import opennlp.tools.ml.maxent.io.PlainTextGISModelWriter;
import opennlp.tools.ml.model.AbstractModel;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.TrainingParameters;
/**
 * 分步骤训练build check模型
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisMEForBuildAndCheck {
	public static final int DEFAULT_BEAM_SIZE = 10;
	private SyntacticAnalysisContextGenerator contextGenerator;
	private int size;
	private Sequence bestSequence;
	private SyntacticAnalysisSequenceClassificationModel model;
	private SyntacticAnalysisModelForBuildAndCheck modelPackage;

    private SyntacticAnalysisSequenceValidator sequenceValidator;
	
	/**
	 * 构造函数，初始化工作
	 * @param model 模型
	 * @param contextGen 特征
	 */
	public SyntacticAnalysisMEForBuildAndCheck(SyntacticAnalysisModelForBuildAndCheck model, SyntacticAnalysisContextGenerator contextGen) {
		init(model , contextGen);
	}
    /**
     * 初始化工作
     * @param model 模型
     * @param contextGen 特征
     */
	private void init(SyntacticAnalysisModelForBuildAndCheck model, SyntacticAnalysisContextGenerator contextGen) {
		int beamSize = SyntacticAnalysisMEForBuildAndCheck.DEFAULT_BEAM_SIZE;

        String beamSizeString = model.getManifestProperty(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);

        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

        modelPackage = model;

        contextGenerator = contextGen;
        size = beamSize;
        sequenceValidator = new DefaultSyntacticAnalysisSequenceValidator();
     
        this.model = new SyntacticAnalysisBeamSearch(beamSize,model.getBuildTreeModel(),
                    model.getCheckTreeModel(), 0);
        
		
	}
	
	/**
	 * 训练模型
	 * @param file 训练文件
	 * @param params 训练
	 * @param contextGen 特征
	 * @param encoding 编码
	 * @return 模型和模型信息的包裹结果
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static SyntacticAnalysisModelForBuildAndCheck train(File file, TrainingParameters params, SyntacticAnalysisContextGenerator contextGen,
			String encoding){
		SyntacticAnalysisModelForBuildAndCheck model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<SyntacticAnalysisSample> sampleStream = new SyntacticAnalysisSampleStream(lineStream);
			model = SyntacticAnalysisMEForBuildAndCheck.train("zh", sampleStream, params, contextGen);
			return model;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return null;
	}

	/**
	 * 训练模型
	 * @param languageCode 编码
	 * @param sampleStream 文件流
	 * @param contextGen 特征
	 * @param encoding 编码
	 * @return 模型和模型信息的包裹结果
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static SyntacticAnalysisModelForBuildAndCheck train(String languageCode, ObjectStream<SyntacticAnalysisSample> sampleStream, TrainingParameters params,
			SyntacticAnalysisContextGenerator contextGen) throws IOException {
		String beamSizeString = params.getSettings().get(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = SyntacticAnalysisMEForBuildAndCheck.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }
        MaxentModel buildModel = null;
        MaxentModel checkModel = null;
        Map<String, String> manifestInfoEntries = new HashMap<String, String>();
        //event_model_trainer
        TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
        SequenceClassificationModel<String> buildseqModel = null;
        SequenceClassificationModel<String> checkseqModel = null;
        if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType)) {
        	//sampleStream为PhraseAnalysisSampleStream对象
            ObjectStream<Event> buildes = new SyntacticAnalysisSampleEventForBuild(sampleStream, contextGen);
            EventTrainer buildtrainer = TrainerFactory.getEventTrainer(params.getSettings(),
                    manifestInfoEntries);
            buildModel = buildtrainer.train(buildes);   
            sampleStream.reset();
            ObjectStream<Event> checkes = new SyntacticAnalysisSampleEventForCheck(sampleStream, contextGen);
            EventTrainer checktrainer = TrainerFactory.getEventTrainer(params.getSettings(),
                    manifestInfoEntries);
            checkModel = checktrainer.train(checkes); 
        }

        if (buildModel != null && checkModel != null) {
            return new SyntacticAnalysisModelForBuildAndCheck(languageCode, buildModel, checkModel, beamSize, manifestInfoEntries);
        } else {
            return new SyntacticAnalysisModelForBuildAndCheck(languageCode, buildseqModel, checkseqModel, manifestInfoEntries);
        }
	}

	/**
	 * 训练模型，并将模型写出
	 * @param file 训练的文本
	 * @param buildmodeltxtFile 文本类型的模型文件
	 * @param checkmodeltxtFile 文本类型的模型文件
	 * @param params 训练的参数配置
	 * @param contextGen 上下文 产生器
	 * @param encoding 编码方式
	 * @return
	 */
	public static SyntacticAnalysisModelForBuildAndCheck train(File file, File buildmodeltxtFile, 
			File checkmodeltxtFile,TrainingParameters params,
			SyntacticAnalysisContextGenerator contextGen, String encoding) {
		PlainTextGISModelWriter modelWriter = null;
		SyntacticAnalysisModelForBuildAndCheck model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<SyntacticAnalysisSample> sampleStream = new SyntacticAnalysisSampleStream(lineStream);
			model = SyntacticAnalysisMEForBuildAndCheck.train("zh", sampleStream, params, contextGen);
			
            modelWriter = new PlainTextGISModelWriter((AbstractModel) model.getBuildTreeModel(), buildmodeltxtFile);
            modelWriter.persist();
            modelWriter = new PlainTextGISModelWriter((AbstractModel) model.getCheckTreeModel(), checkmodeltxtFile);
            modelWriter.persist();
            return model;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据训练得到的模型文件得到
	 * @param modelFile 模型文件
	 * @param params 参数
	 * @param contextGen 上下文生成器
	 * @param encoding 编码方式
	 * @return
	 */
	public static SyntacticAnalysisModelForBuildAndCheck readModel(File buildmodelFile, File checkmodelFile, TrainingParameters params, SyntacticAnalysisContextGenerator contextGen,
			String encoding) {
		PlainTextGISModelReader modelReader = null;
		AbstractModel buildModel = null;
		AbstractModel checkModel = null;
		SyntacticAnalysisModelForBuildAndCheck model = null;
		String beamSizeString = params.getSettings().get(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);
	      
        int beamSize = SyntacticAnalysisMEForBuildAndCheck.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

		try {
			Map<String, String> manifestInfoEntries = new HashMap<String, String>();
			modelReader = new PlainTextGISModelReader(buildmodelFile);			
			buildModel = modelReader.getModel();
			modelReader = new PlainTextGISModelReader(checkmodelFile);			
			checkModel = modelReader.getModel();
			model =  new SyntacticAnalysisModelForBuildAndCheck(encoding, buildModel, checkModel, beamSize,manifestInfoEntries);
	
			System.out.println("读取模型成功");
            return model;
        } catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	
	
	/**
	 * 统计词语出现的个数
	 * @param file 训练语料
	 * @param encoding 编码
	 * @return
	 * @throws IOException
	 * @throws CloneNotSupportedException 
	 */
	public static HashMap<String,Integer> buildDictionary(File file, String encoding) throws IOException, CloneNotSupportedException{
		HashMap<String,Integer> dict = new HashMap<String,Integer>();
		PlainTextByLineStream lineStream = new PlainTextByLineStream(new FileInputStreamFactory(file), "utf8");
		PhraseGenerateTree pgt = new PhraseGenerateTree();
		TreeToActions tta = new TreeToActions();
		String txt = "";
		while((txt = lineStream.read())!= null){
			TreeNode tree = pgt.generateTree(txt);
			SyntacticAnalysisSample sample = tta.treeToAction(tree);
			List<String> words = sample.getWords();
			for (int i = 0; i < words.size(); i++) {
				if(dict.containsKey(words.get(i))){
					Integer count = dict.get(words.get(i));
					count++;
					dict.put(words.get(i), count);
				}else{
					dict.put(words.get(i), 1);
				}
			}
		}
		lineStream.close();
		return dict;
	}
	
	/**
	 * 得到最好的K个chunk树
	 * @param k 结果数目
	 * @param posTree 词性标注树
	 * @param ac
	 * @return
	 */
	public List<List<TreeNode>> tagChunk(int k, List<List<TreeNode>> posTree, Object[] ac){
		List<List<TreeNode>> chunkTree = new ArrayList<>();
		SyntacticAnalysisSequenceForChunk[] sequences = this.model.bestSequencesForChunk(k, posTree, ac, contextGenerator, sequenceValidator);
		for (int i = 0; i < sequences.length; i++) {
			int label = sequences[i].getLabel();
			List<TreeNode> tree = new ArrayList<>();
			List<TreeNode> tempTree = posTree.get(label);
			List<String> outcomes = sequences[i].getOutcomes();
			for (int j = 0; j < outcomes.size(); j++) {
				TreeNode outNode = new TreeNode(outcomes.get(j));
				outNode.setFlag(true);
				outNode.addChild(tempTree.get(j));
				tempTree.get(j).setParent(outNode);
				outNode.setHeadWords(tempTree.get(j).getHeadWords());
				tree.add(outNode);
			}
			chunkTree.add(tree);
		}
		return chunkTree;
	}
	
	/**
	 * 得到最好的K个BuildAndCheck标记
	 * @param k 结果数目
	 * @param chunkTree chunk标记树
	 * @param ac
	 * @return
	 */
	public List<List<TreeNode>> tagBuildAndCheck(int k, List<List<TreeNode>> chunkTree, Object[] ac){
		List<List<TreeNode>> buildAndCheckTree = new ArrayList<>();
		SyntacticAnalysisSequenceForBuildAndCheck[] sequences = this.model.bestSequencesForBuildAndCheck(k, chunkTree, ac, contextGenerator, sequenceValidator);
		for (int i = 0; i < sequences.length; i++) {
			int label = sequences[i].getLabel();
			List<TreeNode> tree = new ArrayList<>();
			List<TreeNode> tempTree = chunkTree.get(label);
			List<String> outcomes = sequences[i].getOutcomes();
			for (int j = 0; j < outcomes.size(); j++) {
				TreeNode outNode = new TreeNode(outcomes.get(j));
				outNode.setFlag(true);
				outNode.addChild(tempTree.get(j));
				tempTree.get(j).setParent(outNode);
				outNode.setHeadWords(tempTree.get(j).getHeadWords());
				tree.add(outNode);
			}
			buildAndCheckTree.add(tree);
		}
		return buildAndCheckTree;
	}
	
	/**
	 * 得到最好的BuildAndCheck标记
	 * @param chunkTree chunk标记树
	 * @param ac
	 * @return
	 */
	public List<TreeNode> tagBuildAndCheck(List<List<TreeNode>> chunkTree, Object[] ac){
		List<List<TreeNode>> buildAndCheckTree = tagBuildAndCheck(1,chunkTree, ac);
		return buildAndCheckTree.get(0);
	}
}

