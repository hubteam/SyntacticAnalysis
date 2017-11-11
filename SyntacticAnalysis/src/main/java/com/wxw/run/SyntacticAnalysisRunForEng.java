package com.wxw.run;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import com.wxw.evaluate.SyntacticAnalysisErrorPrinter;
import com.wxw.evaluate.SyntacticAnalysisEvaluator;
import com.wxw.evaluate.SyntacticAnalysisMeasure;
import com.wxw.feature.FeatureForPosTools;
import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.feature.SyntacticAnalysisContextGeneratorConf;
import com.wxw.model.SyntacticAnalysisME;
import com.wxw.model.SyntacticAnalysisMEForPos;
import com.wxw.model.SyntacticAnalysisModel;
import com.wxw.model.SyntacticAnalysisModelForPos;
import com.wxw.stream.FileInputStreamFactory;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.stream.SyntacticAnalysisSampleStream;
import com.wxw.tree.TreePreTreatment;

import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 * 运行类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisRunForEng {

	private static String flag = "train";

	public static class Corpus{
		
		public String name;
		public String encoding;
		public String trainFile;
		public String testFile;
		public String posmodelbinaryFile;
		public String posmodeltxtFile;
		public String treemodelbinaryFile;
		public String treemodeltxtFile;
		public String errorFile;
	}
	
	private static String[] corpusName = {"tree"};
	
	/**
	 * 根据语料名称获取某个语料
	 * @param corpora 语料内部类数组，包含了所有语料的信息
	 * @param corpusName 语料的名称
	 * @return
	 */
	private static Corpus getCorpus(Corpus[] corpora, String corpusName) {
		for (Corpus c : corpora) {
            if (c.name.equalsIgnoreCase(corpusName)) {
                return c;
            }
        }
        return null;
	}
	
	/**
	 * 得到语料信息
	 * @param config
	 * @return
	 */
	private static Corpus[] getCorporaFromConf(Properties config) {
		Corpus[] corpuses = new Corpus[corpusName.length];
		for (int i = 0; i < corpuses.length; i++) {
			String name = corpusName[i];
			String encoding = config.getProperty(name + "." + "corpus.encoding");
			String trainFile = config.getProperty(name + "." + "corpus.train.file");
			String testFile = config.getProperty(name+"."+"corpus.test.file");
			String posmodelbinaryFile = config.getProperty(name + "." + "corpus.posmodelbinary.file");
			String posmodeltxtFile = config.getProperty(name + "." + "corpus.posmodeltxt.file");
			String treemodelbinaryFile = config.getProperty(name + "." + "corpus.treemodelbinary.file");
			String treemodeltxtFile = config.getProperty(name + "." + "corpus.treemodeltxt.file");
			String errorFile = config.getProperty(name + "." + "corpus.error.file");
			Corpus corpus = new Corpus();
			corpus.name = name;
			corpus.encoding = encoding;
			corpus.trainFile = trainFile;
			corpus.testFile = testFile;
			corpus.posmodeltxtFile = posmodeltxtFile;
			corpus.posmodelbinaryFile = posmodelbinaryFile;
			corpus.treemodeltxtFile = treemodeltxtFile;
			corpus.treemodelbinaryFile = treemodelbinaryFile;
			corpus.errorFile = errorFile;
			corpuses[i] = corpus;			
		}
		return corpuses;
	}
	
	/**
	 * 得到生成特征的实例对象
	 * @param config 配置文件
	 * @return
	 */
	private static SyntacticAnalysisContextGenerator getContextGenerator(Properties config) {
		String featureClass = config.getProperty("feature.class");
		if(featureClass.equals("com.wxw.ner.news.feature.SyntacticAnalysisContextGeneratorConf")){
        	return  new SyntacticAnalysisContextGeneratorConf(config);
		}else{
			return null;
		} 
	}
	
	/**
	 * 主函数
	 * @param args 命令行参数
	 * @throws IOException
	 * @throws CloneNotSupportedException 
	 * @throws UnsupportedOperationException 
	 */
	public static void main(String[] args) throws IOException, UnsupportedOperationException, CloneNotSupportedException {
		String cmd = args[0];
		if(cmd.equals("-train")){
			flag = "train";
			runFeature();
		}else if(cmd.equals("-model")){
			flag = "model";
			runFeature();
		}else if(cmd.equals("-evaluate")){
			flag = "evaluate";
			runFeature();
		}else if(cmd.equals("-cross")){
			String corpus = args[1];
//			crossValidation(corpus);
		}
	}

	/**
	 * 根据配置文件获取特征处理类
	 * @throws IOException
	 * @throws CloneNotSupportedException 
	 * @throws UnsupportedOperationException 
	 */
	private static void runFeature() throws IOException, UnsupportedOperationException, CloneNotSupportedException {
		TrainingParameters params = TrainingParameters.defaultParams();
		params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(3));
	
		//加载语料文件
        Properties config = new Properties();
        InputStream configStream = SyntacticAnalysisRunForEng.class.getClassLoader().getResourceAsStream("com/wxw/run/corpus.properties");
        config.load(configStream);
        Corpus[] corpora = getCorporaFromConf(config);//获取语料

        SyntacticAnalysisContextGenerator contextGen = getContextGenerator(config);

        runFeatureOnCorporaByFlag(contextGen, corpora, params);
	}

	/**
	 * 根据特征处理类和命令行输入参数，进行相应的操作
	 * @param contextGen 特征类
	 * @param corpora 内部类对象，语料信息
	 * @param params 训练模型的参数
	 * @throws CloneNotSupportedException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws UnsupportedOperationException 
	 */
	private static void runFeatureOnCorporaByFlag(SyntacticAnalysisContextGenerator contextGen, Corpus[] corpora,
			TrainingParameters params) throws UnsupportedOperationException, FileNotFoundException, IOException, CloneNotSupportedException {
		if(flag == "train" || flag.equals("train")){
			for (int i = 0; i < corpora.length; i++) {
				trainOnCorpus(contextGen,corpora[i],params);
			}
		}else if(flag == "model" || flag.equals("model")){
			for (int i = 0; i < corpora.length; i++) {
				modelOutOnCorpus(contextGen,corpora[i],params);
			}
		}else if(flag == "evaluate" || flag.equals("evaluate")){
			for (int i = 0; i < corpora.length; i++) {
				evaluateOnCorpus(contextGen,corpora[i],params);
			}
		}
	}

	/**
	 * 评价
	 * @param contextGen 特征类
	 * @param corpora 内部类对象，语料信息
	 * @param params 训练模型的参数
	 * @throws CloneNotSupportedException 
	 * @throws IOException 
	 */
	private static void evaluateOnCorpus(SyntacticAnalysisContextGenerator contextGen, Corpus corpus,
			TrainingParameters params) throws IOException, CloneNotSupportedException {
		System.out.println("ContextGenerator: " + contextGen);

        SyntacticAnalysisModelForPos posmodel = SyntacticAnalysisMEForPos.readModel(new File(corpus.posmodeltxtFile), params, contextGen, corpus.encoding);	
        SyntacticAnalysisModel treemodel = SyntacticAnalysisME.readModel(new File(corpus.treemodeltxtFile), params, contextGen, corpus.encoding);	
        SyntacticAnalysisMEForPos postagger = new SyntacticAnalysisMEForPos(posmodel, contextGen);
        SyntacticAnalysisME treetagger = new SyntacticAnalysisME(treemodel,contextGen);
        
        SyntacticAnalysisMeasure measure = new SyntacticAnalysisMeasure();
        SyntacticAnalysisEvaluator evaluator = null;
        SyntacticAnalysisErrorPrinter printer = null;
        if(corpus.errorFile != null){
        	System.out.println("Print error to file " + corpus.errorFile);
        	printer = new SyntacticAnalysisErrorPrinter(new FileOutputStream(corpus.errorFile));    	
        	evaluator = new SyntacticAnalysisEvaluator(postagger,treetagger,printer);
        }else{
        	evaluator = new SyntacticAnalysisEvaluator(postagger,treetagger);
        }
        evaluator.setMeasure(measure);
        //读测试语料之前也要预处理
        //第一步预处理训练语料，得到处理之后的一个完整的训练语料
        TreePreTreatment.pretreatment("test");
        //根据完整的训练语料对语料中的每个词语计数，得到一hashmap，键是词语，值是出现的次数
        HashMap<String,Integer> dict = SyntacticAnalysisME.buildDictionary(new File(corpus.testFile), "utf-8");
        FeatureForPosTools tools = new FeatureForPosTools(dict);
        ObjectStream<String> linesStream = new PlainTextByLineStream(new FileInputStreamFactory(new File(corpus.testFile)), corpus.encoding);
        ObjectStream<SyntacticAnalysisSample> sampleStream = new SyntacticAnalysisSampleStream(linesStream);
        evaluator.evaluate(sampleStream);
        SyntacticAnalysisMeasure measureRes = evaluator.getMeasure();
        System.out.println("--------结果--------");
        System.out.println(measureRes);
	}

	/**
	 * 训练和输出模型
	 * @param contextGen 特征类
	 * @param corpora 内部类对象，语料信息
	 * @param params 训练模型的参数
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws UnsupportedOperationException 
	 * @throws CloneNotSupportedException 
	 */
	private static void modelOutOnCorpus(SyntacticAnalysisContextGenerator contextGen, Corpus corpus,
			TrainingParameters params) throws UnsupportedOperationException, FileNotFoundException, IOException, CloneNotSupportedException {
		System.out.println("ContextGenerator: " + contextGen);       
		 //第一步预处理训练语料，得到处理之后的一个完整的训练语料
		TreePreTreatment.pretreatment("train");
		//根据完整的训练语料对语料中的每个词语计数，得到一hashmap，键是词语，值是出现的次数
		HashMap<String,Integer> dict = SyntacticAnalysisME.buildDictionary(new File(corpus.trainFile), "utf-8");
		FeatureForPosTools tools = new FeatureForPosTools(dict);
		//训练模型
		//(1)训练词性标记模型
		SyntacticAnalysisMEForPos.train(new File(corpus.trainFile), new File(corpus.posmodelbinaryFile),new File(corpus.posmodeltxtFile),params, contextGen, corpus.encoding);
		//（2）训练句法分析模型
		SyntacticAnalysisME.train(new File(corpus.trainFile), new File(corpus.treemodelbinaryFile),new File(corpus.treemodeltxtFile),params, contextGen, corpus.encoding);
	}

	/**
	 * 训练模型
	 * @param contextGen 特征类
	 * @param corpus 内部类对象，语料信息
	 * @param params 训练模型的参数
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws UnsupportedOperationException 
	 * @throws CloneNotSupportedException 
	 */
	private static void trainOnCorpus(SyntacticAnalysisContextGenerator contextGen, Corpus corpus,
			TrainingParameters params) throws UnsupportedOperationException, FileNotFoundException, IOException, CloneNotSupportedException {
		System.out.println("ContextGenerator: " + contextGen);       
		 //第一步预处理训练语料，得到处理之后的一个完整的训练语料
		TreePreTreatment.pretreatment("train");
		//根据完整的训练语料对语料中的每个词语计数，得到一hashmap，键是词语，值是出现的次数
		HashMap<String,Integer> dict = SyntacticAnalysisME.buildDictionary(new File(corpus.trainFile), "utf-8");
		FeatureForPosTools tools = new FeatureForPosTools(dict);
		//训练模型
		//(1)训练词性标记模型
		SyntacticAnalysisMEForPos.train(new File(corpus.trainFile), params, contextGen, corpus.encoding);
		//（2）训练句法分析模型
		SyntacticAnalysisME.train(new File(corpus.trainFile), params, contextGen, corpus.encoding);
	}
}
