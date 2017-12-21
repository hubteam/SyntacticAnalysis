package com.wxw.model.pos.unused;

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
import com.wxw.stream.FileInputStreamFactory;
import com.wxw.stream.PlainTextByTreeStream;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.stream.SyntacticAnalysisSampleStream;
import com.wxw.tree.HeadTreeNode;
import com.wxw.tree.PhraseGenerateTree;
import com.wxw.tree.TreeNode;
import com.wxw.tree.TreeToHeadTree;
import com.wxw.wordsegandpos.samplestream.WordSegAndPosSample;

import opennlp.tools.ml.BeamSearch;
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
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.TrainingParameters;

/**
 * 训练英文词性标注模型
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisMEContainsPos {
	public static final int DEFAULT_BEAM_SIZE = 20;
	private SyntacticAnalysisContextGeneratorContainsPos contextGenerator;
	private int size;
	private Sequence bestSequence;
	private SequenceClassificationModel<String> model;
	private SyntacticAnalysisModelContainsPos modelPackage;

    private SequenceValidator<String> sequenceValidator;
	
	/**
	 * 构造函数，初始化工作
	 * @param model 模型
	 * @param contextGen 特征
	 */
	public SyntacticAnalysisMEContainsPos(SyntacticAnalysisModelContainsPos model, SyntacticAnalysisContextGeneratorContainsPos contextGen) {
		init(model , contextGen);
	}
    /**
     * 初始化工作
     * @param model 模型
     * @param contextGen 特征
     */
	private void init(SyntacticAnalysisModelContainsPos model, SyntacticAnalysisContextGeneratorContainsPos contextGen) {
		int beamSize = SyntacticAnalysisMEContainsPos.DEFAULT_BEAM_SIZE;

        String beamSizeString = model.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER);

        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

        modelPackage = model;

        contextGenerator = contextGen;
        size = beamSize;
        sequenceValidator = new DefaultSyntacticAnalysisSequenceValidatorContainsPos();
        if (model.getWordPosSequenceModel() != null) {
            this.model = model.getWordPosSequenceModel();
        } else {
            this.model = new BeamSearch<String>(beamSize,
                    model.getWordPosModel(), 0);
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
	public static SyntacticAnalysisModelContainsPos train(File file, TrainingParameters params, SyntacticAnalysisContextGeneratorContainsPos contextGen,
			String encoding){
		SyntacticAnalysisModelContainsPos model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(lineStream);
			model = SyntacticAnalysisMEContainsPos.train("zh", sampleStream, params, contextGen);
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
	public static SyntacticAnalysisModelContainsPos train(String languageCode, ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream, TrainingParameters params,
			SyntacticAnalysisContextGeneratorContainsPos contextGen) throws IOException {
		String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = SyntacticAnalysisMEContainsPos.DEFAULT_BEAM_SIZE;
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
            ObjectStream<Event> es = new SyntacticAnalysisSampleEventContainsPos(sampleStream, contextGen);
            EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(),
                    manifestInfoEntries);
            posModel = trainer.train(es);                       
        }

        if (posModel != null) {
            return new SyntacticAnalysisModelContainsPos(languageCode, posModel, beamSize, manifestInfoEntries);
        } else {
            return new SyntacticAnalysisModelContainsPos(languageCode, seqPosModel, manifestInfoEntries);
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
	public static SyntacticAnalysisModelContainsPos train(File file, File modelbinaryFile, File modeltxtFile, TrainingParameters params,
			SyntacticAnalysisContextGeneratorContainsPos contextGen, String encoding) {
		OutputStream modelOut = null;
		PlainTextGISModelWriter modelWriter = null;
		SyntacticAnalysisModelContainsPos model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(lineStream);
			model = SyntacticAnalysisMEContainsPos.train("zh", sampleStream, params, contextGen);
			 //模型的持久化，写出的为二进制文件
            modelOut = new BufferedOutputStream(new FileOutputStream(modelbinaryFile));           
            model.serialize(modelOut);
            //模型的写出，文本文件
            modelWriter = new PlainTextGISModelWriter((AbstractModel) model.getWordPosModel(), modeltxtFile);
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
	
	public String[] tag(String[] sentence) {
		return this.tag(sentence, (Object[]) null);
	}

	public String[] tag(String[] sentence, Object[] additionaContext) {
		this.bestSequence = this.model.bestSequence(sentence, additionaContext, this.contextGenerator,
				this.sequenceValidator);
		List t = this.bestSequence.getOutcomes();
		return (String[]) t.toArray(new String[t.size()]);
	}

	public String[][] tag(int numTaggings, String[] sentence) {
		Sequence[] bestSequences = this.model.bestSequences(numTaggings, sentence, (Object[]) null, this.contextGenerator,
				this.sequenceValidator);
		String[][] tags = new String[bestSequences.length][];

		for (int si = 0; si < tags.length; ++si) {
			List t = bestSequences[si].getOutcomes();
			tags[si] = (String[]) t.toArray(new String[t.size()]);
		}

		return tags;
	}
	/**
	 * 根据训练得到的模型文件得到
	 * @param modelFile 模型文件
	 * @param params 参数
	 * @param contextGen 上下文生成器
	 * @param encoding 编码方式
	 * @return
	 */
	public static SyntacticAnalysisModelContainsPos readModel(File modelFile, TrainingParameters params, SyntacticAnalysisContextGeneratorContainsPos contextGen,
			String encoding) {
		PlainTextGISModelReader modelReader = null;
		AbstractModel abModel = null;
		SyntacticAnalysisModelContainsPos model = null;
		String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);
	      
        int beamSize = SyntacticAnalysisMEContainsPos.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

		try {
			Map<String, String> manifestInfoEntries = new HashMap<String, String>();
			modelReader = new PlainTextGISModelReader(modelFile);			
			abModel = modelReader.getModel();
			model =  new SyntacticAnalysisModelContainsPos(encoding, abModel, beamSize,manifestInfoEntries);
	
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
	 * 得到最好的词性标注子树序列
	 * @param words 词语序列
	 * @return
	 */
	public List<HeadTreeNode> tagPos(String[] words){
		return tagKpos(1,words).get(0);
	}
	/**
	 * 得到最好的K个词性标注子树序列
	 * @param k 个数
	 * @param words 词语
	 * @return
	 */
	public List<List<HeadTreeNode>> tagKpos(int k, String[] words){
		List<List<HeadTreeNode>> allposTree = new ArrayList<>();
		
		String[][] poses = tag(k, words);
		for (int i = 0; i < poses.length; i++) {
			List<HeadTreeNode> posTree = new ArrayList<>();
			for (int j = 0; j < poses[i].length; j++) {
				HeadTreeNode pos = new HeadTreeNode(poses[i][j]);
				pos.addChild(new HeadTreeNode(words[j]));
				pos.setHeadWords(words[j]);
				posTree.add(pos);
			}
			allposTree.add(posTree);
		}
		return allposTree;
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
		TreeToHeadTree ttht = new TreeToHeadTree();
		HeadTreeToActions tta = new HeadTreeToActions();
		String txt = "";
		while((txt = lineStream.read())!= null){
			TreeNode tree = pgt.generateTree(txt);
			HeadTreeNode headTree = ttht.treeToHeadTree(tree);
			SyntacticAnalysisSample<HeadTreeNode> sample = tta.treeToAction(headTree);
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
	 * 统计词语出现的个数
	 * @param samples 训练语料流
	 * @return
	 * @throws IOException
	 */
    public static HashMap<String,Integer> buildDictionary(ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> samples) throws IOException{
    	HashMap<String,Integer> dict = new HashMap<String,Integer>();
    	SyntacticAnalysisSample<HeadTreeNode> sample = null;
    	while((sample = samples.read()) != null){
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
		return dict;
    }

    	
}
