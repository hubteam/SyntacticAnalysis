package com.wxw.modelBystep;

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
import com.wxw.syntacticanalysis.SyntacticAnalysisForChunk;
import com.wxw.tree.HeadTreeNode;
import com.wxw.tree.HeadTreeToActions;

import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.TrainerFactory.TrainerType;
import opennlp.tools.ml.maxent.io.PlainTextGISModelReader;
import opennlp.tools.ml.model.AbstractModel;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
/**
 * 分步骤训练chunk模型
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisMEForChunk implements SyntacticAnalysisForChunk<HeadTreeNode>{

	public static final int DEFAULT_BEAM_SIZE = 20;
	private SyntacticAnalysisContextGenerator<HeadTreeNode> contextGenerator;
	@SuppressWarnings("unused")
	private int size;
	private SyntacticAnalysisSequenceClassificationModel<HeadTreeNode> model;

    private SyntacticAnalysisSequenceValidator<HeadTreeNode> sequenceValidator;
	
	/**
	 * 构造函数，初始化工作
	 * @param model 模型
	 * @param contextGen 特征
	 */
	public SyntacticAnalysisMEForChunk(SyntacticAnalysisModelForChunk model, SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen) {
		init(model , contextGen);
	}
    /**
     * 初始化工作
     * @param model 模型
     * @param contextGen 特征
     */
	@SuppressWarnings("unchecked")
	private void init(SyntacticAnalysisModelForChunk model, SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen) {
		int beamSize = SyntacticAnalysisMEForChunk.DEFAULT_BEAM_SIZE;

        String beamSizeString = model.getManifestProperty(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);

        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

        contextGenerator = contextGen;
        size = beamSize;
        sequenceValidator = new DefaultSyntacticAnalysisSequenceValidator();
        if (model.getChunkTreeSequenceModel() != null) {
            this.model = (SyntacticAnalysisSequenceClassificationModel<HeadTreeNode>) model.getChunkTreeSequenceModel();
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
	public static SyntacticAnalysisModelForChunk train(File file, TrainingParameters params, SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen,
			String encoding){
		SyntacticAnalysisModelForChunk model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(lineStream);
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
	public static SyntacticAnalysisModelForChunk train(String languageCode, ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream, TrainingParameters params,
			SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen) throws IOException {
		String beamSizeString = params.getSettings().get(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = SyntacticAnalysisMEForChunk.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }
        MaxentModel chunkModel = null;
        Map<String, String> manifestInfoEntries = new HashMap<String, String>();
        TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
        SyntacticAnalysisSequenceClassificationModel<HeadTreeNode> seqChunkModel = null;
        if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType)) {
            ObjectStream<Event> es = new SyntacticAnalysisSampleEventForChunk(sampleStream, contextGen);
            EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(),
                    manifestInfoEntries);
            chunkModel = trainer.train(es);                       
        }

        if (chunkModel != null) {
            return new SyntacticAnalysisModelForChunk(languageCode, chunkModel, beamSize, manifestInfoEntries);
        } else {
            return new SyntacticAnalysisModelForChunk(languageCode, seqChunkModel, manifestInfoEntries);
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
	public static SyntacticAnalysisModelForChunk train(File file, File modelFile, TrainingParameters params,
			SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen, String encoding) {
		OutputStream modelOut = null;
		SyntacticAnalysisModelForChunk model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(lineStream);
			model = SyntacticAnalysisMEForChunk.train("zh", sampleStream, params, contextGen);
            //模型的写出，文本文件
            modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));           
            model.serialize(modelOut);
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
	public static SyntacticAnalysisModelForChunk readModel(File modelFile, TrainingParameters params, SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen,
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
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
		List<List<HeadTreeNode>> chunkTree = new ArrayList<List<HeadTreeNode>>();
		
		List<List<HeadTreeNode>> combineChunkTree = new ArrayList<List<HeadTreeNode>>();
		SyntacticAnalysisSequenceForChunk[] sequences = this.model.bestSequencesForChunk(k, posTree, ac, contextGenerator, sequenceValidator);
		for (int i = 0; i < sequences.length; i++) {
			int label = sequences[i].getLabel();
			List<HeadTreeNode> tree = new ArrayList<>();
			List<HeadTreeNode> tempTree = posTree.get(label);
			List<String> outcomes = sequences[i].getOutcomes();
			for (int j = 0; j < outcomes.size(); j++) {
				HeadTreeNode outNode = new HeadTreeNode(outcomes.get(j));
				outNode.addChild(tempTree.get(j));
				tree.add(outNode);
			}
			chunkTree.add(tree);
		}
		for (int i = 0; i < chunkTree.size(); i++) {
			HeadTreeToActions tta = new HeadTreeToActions();
			List<HeadTreeNode> node = tta.combine(chunkTree.get(i));
			combineChunkTree.add(node);
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
	 * 得到chunk子树
	 * @param words 词语
	 * @param poses 词性
	 * @return
	 */
	@Override
	public List<HeadTreeNode> chunkTree(String[] words, String[] poses) {
		
		List<HeadTreeNode> posTree = new ArrayList<>();
		for (int i = 0; i < poses.length && i < words.length; i++) {
			HeadTreeNode pos = new HeadTreeNode(poses[i]);
			pos.addChild(new HeadTreeNode(words[i]));
			posTree.add(pos);
		}
		
		return chunkTree(posTree);
	}
	/**
	 * 得到chunk子树
	 * @param wordsandposes 词语+词性组成的数组
	 * @return
	 */
	@Override
	public List<HeadTreeNode> chunkTree(String[] wordsandposes) {
		String[] words = new String[wordsandposes.length];
		String[] poses = new String[wordsandposes.length];
		for (int i = 0; i < wordsandposes.length; i++) {
			words[i] = wordsandposes[i].split("/")[0];
			poses[i] = wordsandposes[i].split("/")[1];
		}
		return chunkTree(words,poses);
	}
	/**
	 * 得到chunk子树
	 * @param wordsandposes 词语+词性组成的句子
	 * @return
	 */
	@Override
	public List<HeadTreeNode> chunkTree(String wordsandposes) {
		String[] wordandpos = WhitespaceTokenizer.INSTANCE.tokenize(wordsandposes);
		return chunkTree(wordandpos);
	}
	/**
	 * 得到chunk子树
	 * @param posTree pos子树
	 * @return
	 */
	@Override
	public List<HeadTreeNode> chunkTree(List<HeadTreeNode> posTree) {
		List<List<HeadTreeNode>> allposTree = new ArrayList<>();
		allposTree.add(posTree);
		HeadTreeToActions tta = new HeadTreeToActions();
		return tta.combine(tagChunk(allposTree,null));
	}
	/**
	 * 得到chunk结果
	 * @param words 词语
	 * @param poses 词性
	 * @return
	 */
	@Override
	public String[] chunk(String[] words, String[] poses) {
		List<HeadTreeNode> posTree = new ArrayList<>();
		for (int i = 0; i < poses.length && i < words.length; i++) {
			HeadTreeNode pos = new HeadTreeNode(poses[i]);
			pos.addChild(new HeadTreeNode(words[i]));
			posTree.add(pos);
		}
		
		return chunk(posTree);
	}
	/**
	 * 得到chunk结果
	 * @param wordsandposes 词语+词性组成数组
	 * @return
	 */
	@Override
	public String[] chunk(String[] wordsandposes) {
		String[] words = new String[wordsandposes.length];
		String[] poses = new String[wordsandposes.length];
		for (int i = 0; i < wordsandposes.length; i++) {
			words[i] = wordsandposes[i].split("/")[0];
			poses[i] = wordsandposes[i].split("/")[1];
		}
		return chunk(words,poses);
	}
	/**
	 * 得到chunk结果
	 * @param wordsandposes 词语+词性组成的句子
	 * @return
	 */
	@Override
	public String[] chunk(String wordsandposes) {
		String[] wordandpos = WhitespaceTokenizer.INSTANCE.tokenize(wordsandposes);
		return chunk(wordandpos);
	}
	/**
	 * 得到chunk结果
	 * @param posTree pos子树
	 * @return
	 */
	@Override
	public String[] chunk(List<HeadTreeNode> posTree) {
		List<List<HeadTreeNode>> allposTree = new ArrayList<>();
		allposTree.add(posTree);
		List<HeadTreeNode> chunkTree = tagChunk(allposTree,null);
		String[] wordandpos = new String[chunkTree.size()];
		String[] chunkTag = new String[chunkTree.size()];
		String[] output = new String[chunkTree.size()];
		int k = 0;
		int index = -1;
		for (int i = 0; i < chunkTree.size(); i++) {
			if(chunkTree.get(i).getNodeName().contains("start")){
				chunkTag[k] = chunkTree.get(i).getNodeName().split("_")[1];
				wordandpos[k] += getWordAndPos(chunkTree.get(i).getChildren().get(0));
				for (int j = i+1; j < chunkTag.length; j++) {
					if(chunkTree.get(j).getNodeName().contains("start")){
						break;
					}else if(chunkTree.get(j).getNodeName().contains("join")){
						wordandpos[k] += getWordAndPos(chunkTree.get(j).getChildren().get(0));
						index = j;
					}
				}
				i = index;
				output[k] = "["+wordandpos[k]+"]"+chunkTag[k]+" ";
				k++;
			}else if(chunkTree.get(i).getNodeName().contains("other")){
				chunkTag[k] = "o";
				wordandpos[k] += getWordAndPos(chunkTree.get(i).getChildren().get(0));
				for (int j = i+1; j < chunkTag.length; j++) {
					if(chunkTree.get(j).getNodeName().contains("start")){
						break;
					}else if(chunkTree.get(j).getNodeName().contains("join")){
						wordandpos[k] += getWordAndPos(chunkTree.get(j).getChildren().get(0));
						index = j;
					}
				}
				i = index;
				output[k] = wordandpos[k]+" ";
				k++;
			}
		}
		return output;
	}
	
	public String getWordAndPos(HeadTreeNode tree){
		String wordandpos = "";
		for (int i = 0; i < tree.getChildren().size(); i++) {
			if(i == tree.getChildren().size()-1){
				wordandpos += tree.getChildren().get(i).getChildren().get(0).getNodeName()+"/"+
						tree.getChildren().get(i).getNodeName();
			}else{
				wordandpos += tree.getChildren().get(i).getChildren().get(0).getNodeName()+"/"+
						tree.getChildren().get(i).getNodeName()+" ";
			}
		}
		return wordandpos;
	}
}

