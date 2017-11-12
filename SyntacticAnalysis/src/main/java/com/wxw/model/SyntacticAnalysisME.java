package com.wxw.model;

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
import com.wxw.sequence.SyntacticAnalysisSequenceValidator;
import com.wxw.stream.FileInputStreamFactory;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.stream.SyntacticAnalysisSampleEvent;
import com.wxw.stream.SyntacticAnalysisSampleStream;
import com.wxw.tree.PhraseGenerateTree;
import com.wxw.tree.TreeNode;
import com.wxw.tree.TreeToActions;

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
import opennlp.tools.util.TrainingParameters;

/**
 * 训练模型
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisME {

	public static final int DEFAULT_BEAM_SIZE = 20;
	private SyntacticAnalysisContextGenerator contextGenerator;
	private int size;
	private Sequence bestSequence;
	private SyntacticAnalysisSequenceClassificationModel model;
	private SyntacticAnalysisModel modelPackage;

    private SyntacticAnalysisSequenceValidator sequenceValidator;
	
	/**
	 * 构造函数，初始化工作
	 * @param model 模型
	 * @param contextGen 特征
	 */
	public SyntacticAnalysisME(SyntacticAnalysisModel model, SyntacticAnalysisContextGenerator contextGen) {
		init(model , contextGen);
	}
    /**
     * 初始化工作
     * @param model 模型
     * @param contextGen 特征
     */
	private void init(SyntacticAnalysisModel model, SyntacticAnalysisContextGenerator contextGen) {
		int beamSize = SyntacticAnalysisME.DEFAULT_BEAM_SIZE;

        String beamSizeString = model.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER);

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
                    model.getTreeModel(), 0);
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
	public static SyntacticAnalysisModel train(File file, TrainingParameters params, SyntacticAnalysisContextGenerator contextGen,
			String encoding){
		SyntacticAnalysisModel model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<SyntacticAnalysisSample> sampleStream = new SyntacticAnalysisSampleStream(lineStream);
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
	public static SyntacticAnalysisModel train(String languageCode, ObjectStream<SyntacticAnalysisSample> sampleStream, TrainingParameters params,
			SyntacticAnalysisContextGenerator contextGen) throws IOException {
		String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);
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
			SyntacticAnalysisContextGenerator contextGen, String encoding) {
		OutputStream modelOut = null;
		PlainTextGISModelWriter modelWriter = null;
		SyntacticAnalysisModel model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<SyntacticAnalysisSample> sampleStream = new SyntacticAnalysisSampleStream(lineStream);
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
	public static SyntacticAnalysisModel readModel(File modelFile, TrainingParameters params, SyntacticAnalysisContextGenerator contextGen,
			String encoding) {
		PlainTextGISModelReader modelReader = null;
		AbstractModel abModel = null;
		SyntacticAnalysisModel model = null;
		String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);
	      
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
}
