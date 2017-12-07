package com.wxw.model.bystep;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.sequence.DefaultSyntacticAnalysisSequenceValidator;
import com.wxw.sequence.SyntacticAnalysisBeamSearch;
import com.wxw.sequence.SyntacticAnalysisSequenceClassificationModel;
import com.wxw.sequence.SyntacticAnalysisSequenceForChunk;
import com.wxw.sequence.SyntacticAnalysisSequenceValidator;
import com.wxw.stream.FileInputStreamFactory;
import com.wxw.stream.PlainTextByTreeStream;
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
import opennlp.tools.util.Sequence;
import opennlp.tools.util.TrainingParameters;
/**
 * 分步骤训练chunk模型
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisMEForChunk {

	public static final int DEFAULT_BEAM_SIZE = 10;
	private SyntacticAnalysisContextGenerator contextGenerator;
	private int size;
	private Sequence bestSequence;
	private SyntacticAnalysisSequenceClassificationModel model;
	private SyntacticAnalysisModelForChunk modelPackage;

    private SyntacticAnalysisSequenceValidator sequenceValidator;
	
	/**
	 * 构造函数，初始化工作
	 * @param model 模型
	 * @param contextGen 特征
	 */
	public SyntacticAnalysisMEForChunk(SyntacticAnalysisModelForChunk model, SyntacticAnalysisContextGenerator contextGen) {
		init(model , contextGen);
	}
    /**
     * 初始化工作
     * @param model 模型
     * @param contextGen 特征
     */
	private void init(SyntacticAnalysisModelForChunk model, SyntacticAnalysisContextGenerator contextGen) {
		int beamSize = SyntacticAnalysisMEForChunk.DEFAULT_BEAM_SIZE;

        String beamSizeString = model.getManifestProperty(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);

        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

        modelPackage = model;

        contextGenerator = contextGen;
        size = beamSize;
        sequenceValidator = new DefaultSyntacticAnalysisSequenceValidator();
        if (model.getChunkTreeSequenceModel() != null) {
            this.model = model.getChunkTreeSequenceModel();
        } else {
            this.model = new SyntacticAnalysisBeamSearch(beamSize,
                    model.getChunkTreeModel(), 0);
        }
		
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
	public static SyntacticAnalysisModelForChunk train(File file, TrainingParameters params, SyntacticAnalysisContextGenerator contextGen,
			String encoding){
		SyntacticAnalysisModelForChunk model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<SyntacticAnalysisSample> sampleStream = new SyntacticAnalysisSampleStream(lineStream);
			model = SyntacticAnalysisMEForChunk.train("zh", sampleStream, params, contextGen);
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
	public static SyntacticAnalysisModelForChunk train(String languageCode, ObjectStream<SyntacticAnalysisSample> sampleStream, TrainingParameters params,
			SyntacticAnalysisContextGenerator contextGen) throws IOException {
		String beamSizeString = params.getSettings().get(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = SyntacticAnalysisMEForChunk.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }
        MaxentModel posModel = null;
        Map<String, String> manifestInfoEntries = new HashMap<String, String>();
        //event_model_trainer
        TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
        SequenceClassificationModel<String> seqPosModel = null;
        if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType)) {
        	//sampleStream为PhraseAnalysisSampleStream对象
            ObjectStream<Event> es = new SyntacticAnalysisSampleEventForChunk(sampleStream, contextGen);
            EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(),
                    manifestInfoEntries);
            posModel = trainer.train(es);                       
        }

        if (posModel != null) {
            return new SyntacticAnalysisModelForChunk(languageCode, posModel, beamSize, manifestInfoEntries);
        } else {
            return new SyntacticAnalysisModelForChunk(languageCode, seqPosModel, manifestInfoEntries);
        }
	}

	/**
	 * 训练模型，并将模型写出
	 * @param file 训练的文本
	 * @param modelbinaryFile 二进制的模型文件
	 * @param modeltxtFile 文本类型的模型文件
	 * @param params 训练的参数配置
	 * @param contextGen 上下文 产生器
	 * @param encoding 编码方式
	 * @return
	 */
	public static SyntacticAnalysisModelForChunk train(File file, File modelbinaryFile, File modeltxtFile, TrainingParameters params,
			SyntacticAnalysisContextGenerator contextGen, String encoding) {
		OutputStream modelOut = null;
		PlainTextGISModelWriter modelWriter = null;
		SyntacticAnalysisModelForChunk model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<SyntacticAnalysisSample> sampleStream = new SyntacticAnalysisSampleStream(lineStream);
			model = SyntacticAnalysisMEForChunk.train("zh", sampleStream, params, contextGen);
			 //模型的持久化，写出的为二进制文件
            modelOut = new BufferedOutputStream(new FileOutputStream(modelbinaryFile));           
            model.serialize(modelOut);
            //模型的写出，文本文件
            modelWriter = new PlainTextGISModelWriter((AbstractModel) model.getChunkTreeModel(), modeltxtFile);
            modelWriter.persist();
            return model;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
            if (modelOut != null) {
                try {
                    modelOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }	
		return null;
	}
	
//	public String[] tag(String[] characters, String[] tags, String[] words, Object[] additionaContext){
//		bestSequence = model.bestSequence(characters, tags, words, additionaContext, contextGenerator,sequenceValidator);
//      //  System.out.println(bestSequence);
//		List<String> t = bestSequence.getOutcomes();
//        
//        return t.toArray(new String[t.size()]);
//	}
	/**
	 * 根据训练得到的模型文件得到
	 * @param modelFile 模型文件
	 * @param params 参数
	 * @param contextGen 上下文生成器
	 * @param encoding 编码方式
	 * @return
	 */
	public static SyntacticAnalysisModelForChunk readModel(File modelFile, TrainingParameters params, SyntacticAnalysisContextGenerator contextGen,
			String encoding) {
		PlainTextGISModelReader modelReader = null;
		AbstractModel abModel = null;
		SyntacticAnalysisModelForChunk model = null;
		String beamSizeString = params.getSettings().get(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);
	      
        int beamSize = SyntacticAnalysisMEForChunk.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

		try {
			Map<String, String> manifestInfoEntries = new HashMap<String, String>();
			modelReader = new PlainTextGISModelReader(modelFile);			
			abModel = modelReader.getModel();
			model =  new SyntacticAnalysisModelForChunk(encoding, abModel, beamSize,manifestInfoEntries);
	
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
		PlainTextByTreeStream lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(file), "utf8");
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
		SyntacticAnalysisSequenceForChunk[] sequences = this.model.bestSequencesForChunk(k, chunkTree, ac, contextGenerator, sequenceValidator);
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

