package com.wxw.model.all;

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

import com.wxw.actions.HeadTreeToActions;
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
import com.wxw.tree.HeadTreeNode;

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
 * 一步训练树模型
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisME {

	public static final int DEFAULT_BEAM_SIZE = 8;
	private SyntacticAnalysisContextGenerator<HeadTreeNode> contextGenerator;
	private int size;
	private Sequence bestSequence;
	private SyntacticAnalysisSequenceClassificationModel<HeadTreeNode> model;
	private SyntacticAnalysisModel modelPackage;

    private SyntacticAnalysisSequenceValidator<HeadTreeNode> sequenceValidator;
	
	/**
	 * 构造函数，初始化工作
	 * @param model 模型
	 * @param contextGen 特征
	 */
	public SyntacticAnalysisME(SyntacticAnalysisModel model, SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen) {
		init(model , contextGen);
	}
    /**
     * 初始化工作
     * @param model 模型
     * @param contextGen 特征
     */
	private void init(SyntacticAnalysisModel model, SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen) {
		int beamSize = SyntacticAnalysisME.DEFAULT_BEAM_SIZE;

        String beamSizeString = model.getManifestProperty(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);

        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

        modelPackage = model;

        contextGenerator = contextGen;
        size = beamSize;
        sequenceValidator = new DefaultSyntacticAnalysisSequenceValidator();
        if (model.getTreeSequenceModel() != null) {
            this.model = model.getTreeSequenceModel();
        } else {
            this.model = new SyntacticAnalysisBeamSearch(beamSize,
                    model.getTreeModel(), 0,"onstep");
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
	public static SyntacticAnalysisModel train(File file, TrainingParameters params, SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen,
			String encoding){
		SyntacticAnalysisModel model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(lineStream);
			model = SyntacticAnalysisME.train("zh", sampleStream, params, contextGen);
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
	public static SyntacticAnalysisModel train(String languageCode, ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream, TrainingParameters params,
			SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen) throws IOException {
		String beamSizeString = params.getSettings().get(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = SyntacticAnalysisME.DEFAULT_BEAM_SIZE;
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
            ObjectStream<Event> es = new SyntacticAnalysisSampleEvent(sampleStream, contextGen);
            EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(),
                    manifestInfoEntries);
            posModel = trainer.train(es);                       
        }

        if (posModel != null) {
            return new SyntacticAnalysisModel(languageCode, posModel, beamSize, manifestInfoEntries);
        } else {
            return new SyntacticAnalysisModel(languageCode, seqPosModel, manifestInfoEntries);
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
	public static SyntacticAnalysisModel train(File file, File modelbinaryFile, File modeltxtFile, TrainingParameters params,
			SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen, String encoding) {
		OutputStream modelOut = null;
		PlainTextGISModelWriter modelWriter = null;
		SyntacticAnalysisModel model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(lineStream);
			model = SyntacticAnalysisME.train("zh", sampleStream, params, contextGen);
			 //模型的持久化，写出的为二进制文件
            modelOut = new BufferedOutputStream(new FileOutputStream(modelbinaryFile));           
            model.serialize(modelOut);
            //模型的写出，文本文件
            modelWriter = new PlainTextGISModelWriter((AbstractModel) model.getTreeModel(), modeltxtFile);
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
	/**
	 * 根据训练得到的模型文件得到
	 * @param modelFile 模型文件
	 * @param params 参数
	 * @param contextGen 上下文生成器
	 * @param encoding 编码方式
	 * @return
	 */
	public static SyntacticAnalysisModel readModel(File modelFile, TrainingParameters params, SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen,
			String encoding) {
		PlainTextGISModelReader modelReader = null;
		AbstractModel abModel = null;
		SyntacticAnalysisModel model = null;
		String beamSizeString = params.getSettings().get(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);
	      
        int beamSize = SyntacticAnalysisME.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

		try {
			Map<String, String> manifestInfoEntries = new HashMap<String, String>();
			modelReader = new PlainTextGISModelReader(modelFile);			
			abModel = modelReader.getModel();
			model =  new SyntacticAnalysisModel(encoding, abModel, beamSize,manifestInfoEntries);
	
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
	 * 得到最好的K个chunk树
	 * @param k 结果数目
	 * @param posTree 词性标注树
	 * @param ac
	 * @return
	 */
	public List<List<HeadTreeNode>> tagKChunk(int k, List<List<HeadTreeNode>> posTree, Object[] ac){
		List<List<HeadTreeNode>> chunkTree = new ArrayList<>();
		HeadTreeToActions tta = new HeadTreeToActions();
		List<List<HeadTreeNode>> combineChunkTree = new ArrayList<>();
		SyntacticAnalysisSequenceForChunk[] sequences = this.model.bestSequencesForChunk(k, posTree, ac, contextGenerator, sequenceValidator);
		for (int i = 0; i < sequences.length; i++) {
			int label = sequences[i].getLabel();
			List<HeadTreeNode> tree = new ArrayList<>();
			List<HeadTreeNode> tempTree = posTree.get(label);
			List<String> outcomes = sequences[i].getOutcomes();
			for (int j = 0; j < outcomes.size(); j++) {
				HeadTreeNode outNode = new HeadTreeNode(outcomes.get(j));
				outNode.setFlag(true);
				outNode.addChild(tempTree.get(j));
				tempTree.get(j).setParent(outNode);
				outNode.setHeadWords(tempTree.get(j).getHeadWords());
				tree.add(outNode);
			}
			chunkTree.add(tree);
		}
		for (int i = 0; i < chunkTree.size(); i++) {
			combineChunkTree.add(tta.combine(chunkTree.get(i)));
		}
		return combineChunkTree;
	}
	
	/**
	 * 得到最好的K个chunk树
	 * @param k 结果数目
	 * @param posTree 词性标注树
	 * @param ac
	 * @return
	 */
	public List<HeadTreeNode> tagChunk(List<List<HeadTreeNode>> posTree, Object[] ac){
		List<List<HeadTreeNode>> chunkTree = tagKChunk(1,posTree,null);
		return chunkTree.get(0);
	}
	
	/**
	 * 得到最好的K个最好结果的树,List中每一个值都是一颗完整的树
	 * @param k 结果数目
	 * @param chunkTree chunk标记树
	 * @param ac
	 * @return
	 */
	public List<HeadTreeNode> tagBuildAndCheck(int k, List<List<HeadTreeNode>> chunkTree, Object[] ac){
		List<HeadTreeNode> buildAndCheckTree = new ArrayList<>();
		SyntacticAnalysisSequenceForBuildAndCheck<HeadTreeNode>[] sequences = this.model.bestSequencesForBuildAndCheck(k, chunkTree, ac, contextGenerator, sequenceValidator);
		for (int i = 0; i < sequences.length; i++) {
			buildAndCheckTree.add(sequences[i].getTree().get(0));
		}
		return buildAndCheckTree;
	}
	/**
	 * 得到最好的K个完整的动作序列
	 * @param k 结果数
	 * @param chunkTree k个chunk子树序列
	 * @param ac
	 * @return
	 * @throws CloneNotSupportedException 
	 */
	public List<List<String>> tagKactions(int k, List<List<HeadTreeNode>> chunkTree, Object[] ac) throws CloneNotSupportedException{
		List<List<String>> kActions = new ArrayList<>();
		List<HeadTreeNode> alltree= tagBuildAndCheck(k,chunkTree,null);
		for (int i = 0; i < alltree.size(); i++) {
			HeadTreeToActions tta = new HeadTreeToActions();
			SyntacticAnalysisSample<HeadTreeNode> sample = tta.treeToAction(alltree.get(i));
			kActions.add(sample.getActions());	
		}
		return kActions;
	}
	
	/**
	 * 得到最好的K个完整的动作序列
	 * @param k 结果数
	 * @param chunkTree k个chunk子树序列
	 * @param ac
	 * @return
	 * @throws CloneNotSupportedException 
	 */
	public List<String> tagActions(int k, List<List<HeadTreeNode>> chunkTree, Object[] ac) throws CloneNotSupportedException{
		List<List<String>> kActions = tagKactions(1,chunkTree,null);
		return kActions.get(0);
	}
	
	/**
	 * 得到最好的树
	 * @param chunkTree chunk标记树
	 * @param ac
	 * @return
	 */
	public HeadTreeNode tagBuildAndCheck(List<List<HeadTreeNode>> chunkTree, Object[] ac){
		List<HeadTreeNode> buildAndCheckTree = tagBuildAndCheck(1,chunkTree, ac);
		return buildAndCheckTree.get(0);
	}
}
