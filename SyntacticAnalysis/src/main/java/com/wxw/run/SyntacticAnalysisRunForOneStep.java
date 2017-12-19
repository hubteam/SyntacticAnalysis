package com.wxw.run;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import com.wxw.cross.SyntacticAnalysisCrossValidationForOneStep;
import com.wxw.evaluate.SyntacticAnalysisErrorPrinter;
import com.wxw.evaluate.SyntacticAnalysisMeasure;
import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.feature.SyntacticAnalysisContextGeneratorConf;
import com.wxw.model.all.SyntacticAnalysisEvaluatorForOneStep;
import com.wxw.model.all.SyntacticAnalysisME;
import com.wxw.model.all.SyntacticAnalysisModel;
import com.wxw.model.bystep.POSTaggerMEExtend;
import com.wxw.model.pos.unused.FeatureContainsPosTools;
import com.wxw.model.pos.unused.SyntacticAnalysisContextGeneratorConfContainsPos;
import com.wxw.model.pos.unused.SyntacticAnalysisContextGeneratorContainsPos;
import com.wxw.model.pos.unused.SyntacticAnalysisEvaluatorContainsPos;
import com.wxw.model.pos.unused.SyntacticAnalysisMEContainsPos;
import com.wxw.model.pos.unused.SyntacticAnalysisModelContainsPos;
import com.wxw.stream.FileInputStreamFactory;
import com.wxw.stream.PlainTextByTreeStream;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.stream.SyntacticAnalysisSampleStream;
import com.wxw.tree.HeadTreeNode;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;

/**
 * 英文句法分析运行类
 * 包括：自己写的词性标注+一步训练得到的树模型
 * 包括：opennlp的词性标注模型+一步训练得到的树模型
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisRunForOneStep {

	private static String flag = "train";

	public static class Corpus{
		
		public String name;
		public String encoding;
		public String trainFile;
		public String testFile;
		public String posmodelbinaryFile;
		public String posmodeltxtFile;
		public String posenglish;
		public String poschina;
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
			String posenglish = config.getProperty(name + "." + "corpus.posenglish.file");
			String poschina = config.getProperty(name + "." + "corpus.poschina.file");
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
			corpus.posenglish = posenglish;
			corpus.poschina = poschina;
			corpus.treemodeltxtFile = treemodeltxtFile;
			corpus.treemodelbinaryFile = treemodelbinaryFile;
			corpus.errorFile = errorFile;
			corpuses[i] = corpus;			
		}
		return corpuses;
	}
	
	/**
	 * 得到生成特征的实例对象【主要是句法树的特征】
	 * @param config 配置文件
	 * @return
	 */
	private static SyntacticAnalysisContextGenerator<HeadTreeNode> getContextGenerator(Properties config) {
		String featureClass = config.getProperty("feature.class");
		if(featureClass.equals("com.wxw.feature.SyntacticAnalysisContextGeneratorConf")){
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
		if(cmd.equals("-train")){//只训练句法分析模型
			flag = "train";
			runFeature();
		}else if(cmd.equals("-model")){//训练并输出句法分析模型
			flag = "model";
			runFeature();
		}else if(cmd.equals("-evaluate")){//opennlp中的词性标注模型
			flag = "evaluate";
			runFeature();
		}else if(cmd.equals("-cross")){
			String corpus = args[1];
			crossValidation(corpus);
		}else if(cmd.equals("-trainpt")){//同时训练词性标注模型和句法分析模型
			flag = "trainpt";
			runFeatureForPosAndTree();
		}else if(cmd.equals("-modelpt")){//同时输出词性标注模型和句法分析模型
			flag = "modelpt";
			runFeatureForPosAndTree();
		}else if(cmd.equals("-evaluatept")){//用自己训练的词性标注模型和句法分析模型
			flag = "evaluatept";
			runFeatureForPosAndTree();
		}else if(cmd.equals("-crosspt")){//用自己训练的词性标注模型和句法分析模型
			String corpus = args[1];
			crossValidationForPosAndTree(corpus);
		}
	}

	/**
	 * 交叉验证[用最大熵模型的词性标注器]
	 * @param corpus 语料的名称
	 * @throws IOException 
	 */
	private static void crossValidation(String corpusName) throws IOException {
		Properties config = new Properties();
		InputStream configStream = SyntacticAnalysisRunByStep.class.getClassLoader().getResourceAsStream("com/wxw/run/corpus.properties");
		config.load(configStream);
		Corpus[] corpora = getCorporaFromConf(config);
        //定位到某一语料
        Corpus corpus = getCorpus(corpora, corpusName);
        SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen = getContextGenerator(config);
        ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File(corpus.trainFile)), corpus.encoding);
        
        ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(lineStream);

        //默认参数
        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(3));

        //把刚才属性信息封装
        SyntacticAnalysisCrossValidationForOneStep crossValidator = new SyntacticAnalysisCrossValidationForOneStep("zh", params);

        System.out.println(contextGen);
        crossValidator.evaluate(new File(corpus.posenglish),sampleStream, 10, contextGen);
	}
	
	/**
	 * 交叉验证【用自己训练的词性标注器】
	 * @param corpus 语料的名称
	 * @throws IOException 
	 */
	private static void crossValidationForPosAndTree(String corpusName) throws IOException {
		Properties config = new Properties();
		InputStream configStream = SyntacticAnalysisRunByStep.class.getClassLoader().getResourceAsStream("com/wxw/run/corpus.properties");
		config.load(configStream);
		Corpus[] corpora = getCorporaFromConf(config);
        //定位到某一语料
        Corpus corpus = getCorpus(corpora, corpusName);
        SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen = getContextGenerator(config);
        ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File(corpus.trainFile)), corpus.encoding);
        
        ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(lineStream);

        //默认参数
        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(3));

        //把刚才属性信息封装
        SyntacticAnalysisCrossValidationForOneStep crossValidator = new SyntacticAnalysisCrossValidationForOneStep("zh", params);

        SyntacticAnalysisContextGeneratorContainsPos contextGenForPos = new SyntacticAnalysisContextGeneratorConfContainsPos();
        System.out.println(contextGen);
        crossValidator.evaluateContainsPos(sampleStream, 10, contextGen,contextGenForPos);
	}
	
	/**
	 * 根据配置文件获取特征处理类
	 * @throws IOException
	 * @throws CloneNotSupportedException 
	 * @throws UnsupportedOperationException 
	 */
	private static void runFeatureForPosAndTree() throws IOException, UnsupportedOperationException, CloneNotSupportedException {
		TrainingParameters params = TrainingParameters.defaultParams();
		params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(3));
	
		//加载语料文件
        Properties config = new Properties();
        InputStream configStream = SyntacticAnalysisRunForOneStep.class.getClassLoader().getResourceAsStream("com/wxw/run/corpus.properties");
        config.load(configStream);
        Corpus[] corpora = getCorporaFromConf(config);//获取语料

        SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen = getContextGenerator(config);
        SyntacticAnalysisContextGeneratorContainsPos contextGenForPos = new SyntacticAnalysisContextGeneratorConfContainsPos();
        runFeatureOnCorpora(contextGen, contextGenForPos,corpora, params);
	}

	/**
	 * 根据特征处理类和命令行输入参数，进行相应的操作【这里区别于下面的方法是因为要自己训练词性标注模型，不是直接用opennlp的】
	 * @param contextGen 特征类
	 * @param contextGenForPos 词性标注特征类
	 * @param corpora 内部类对象，语料信息
	 * @param params 训练模型的参数
	 * @throws CloneNotSupportedException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws UnsupportedOperationException 
	 */
	private static void runFeatureOnCorpora(SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen,
			SyntacticAnalysisContextGeneratorContainsPos contextGenForPos, Corpus[] corpora, TrainingParameters params) throws UnsupportedOperationException, FileNotFoundException, IOException, CloneNotSupportedException {
		if(flag == "trainpt" || flag.equals("trainpt")){
			for (int i = 0; i < corpora.length; i++) {
				trainOnCorpusContainPos(contextGen,contextGenForPos,corpora[i],params);
			}
		}else if(flag == "modelpt" || flag.equals("modelpt")){
			for (int i = 0; i < corpora.length; i++) {
				modelOutOnCorpusContainPos(contextGen,contextGenForPos,corpora[i],params);
			}
		}else if(flag == "evaluatept" || flag.equals("evaluatept")){
			for (int i = 0; i < corpora.length; i++) {
				evaluateOnCorpusContainPos(contextGen,contextGenForPos,corpora[i],params);
			}
		}
		
	}

	/**
	 * 用自己训练的词性标注模型评估
	 * @param contextGen 特征
	 * @param contextGenForPos 词性标注特征
	 * @param corpus 语料
	 * @param params 参数
	 * @throws IOException
	 * @throws CloneNotSupportedException
	 */
	private static void evaluateOnCorpusContainPos(SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen,
			SyntacticAnalysisContextGeneratorContainsPos contextGenForPos, Corpus corpus, TrainingParameters params) throws IOException, CloneNotSupportedException {
		System.out.println("ContextGenerator: " + contextGen);

      SyntacticAnalysisModelContainsPos posmodel = SyntacticAnalysisMEContainsPos.readModel(new File(corpus.posmodeltxtFile), params, contextGenForPos, corpus.encoding);	
      SyntacticAnalysisModel treemodel = SyntacticAnalysisME.readModel(new File(corpus.treemodeltxtFile), params, contextGen, corpus.encoding);	
      SyntacticAnalysisMEContainsPos postagger = new SyntacticAnalysisMEContainsPos(posmodel, contextGenForPos);
      SyntacticAnalysisME treetagger = new SyntacticAnalysisME(treemodel,contextGen);
      
      SyntacticAnalysisMeasure measure = new SyntacticAnalysisMeasure();
      SyntacticAnalysisEvaluatorContainsPos evaluator = null;
      SyntacticAnalysisErrorPrinter printer = null;
      if(corpus.errorFile != null){
      	System.out.println("Print error to file " + corpus.errorFile);
      	printer = new SyntacticAnalysisErrorPrinter(new FileOutputStream(corpus.errorFile));    	
      	evaluator = new SyntacticAnalysisEvaluatorContainsPos(postagger,treetagger,printer);
      }else{
      	evaluator = new SyntacticAnalysisEvaluatorContainsPos(postagger,treetagger);
      }
      evaluator.setMeasure(measure);
      ObjectStream<String> linesStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File(corpus.testFile)), corpus.encoding);
      ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(linesStream);
      evaluator.evaluate(sampleStream);
      SyntacticAnalysisMeasure measureRes = evaluator.getMeasure();
      System.out.println("--------结果--------");
      System.out.println(measureRes);		
	}

	/**
	 * 同时输出词性标注模型和句法分析模型
	 * @param contextGen 特征
	 * @param contextGenForPos 词性标注特征
	 * @param corpus 语料
	 * @param params 参数
	 * @throws UnsupportedOperationException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws CloneNotSupportedException
	 */
	private static void modelOutOnCorpusContainPos(SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen,
			SyntacticAnalysisContextGeneratorContainsPos contextGenForPos, Corpus corpus, TrainingParameters params) throws UnsupportedOperationException, FileNotFoundException, IOException, CloneNotSupportedException {
		System.out.println("ContextGenerator: " + contextGen);       
		//根据完整的训练语料对语料中的每个词语计数，得到一hashmap，键是词语，值是出现的次数【为训练词性标注的模型准备】
		HashMap<String,Integer> dict = SyntacticAnalysisMEContainsPos.buildDictionary(new File(corpus.trainFile), "utf-8");
		FeatureContainsPosTools tools = new FeatureContainsPosTools(dict);
		//训练模型
		//(1)训练词性标记模型
		SyntacticAnalysisMEContainsPos.train(new File(corpus.trainFile), new File(corpus.posmodelbinaryFile),new File(corpus.posmodeltxtFile),params, contextGenForPos, corpus.encoding);
		//（2）训练句法分析模型
		SyntacticAnalysisME.train(new File(corpus.trainFile), new File(corpus.treemodelbinaryFile),new File(corpus.treemodeltxtFile),params, contextGen, corpus.encoding);
		
	}

	/**
	 * 同时训练词性标注模型和句法分析模型
	 * @param contextGen 特征
	 * @param contextGenForPos 词性标注特征
	 * @param corpus 语料
	 * @param params 参数
	 * @throws IOException
	 * @throws CloneNotSupportedException
	 */
	private static void trainOnCorpusContainPos(SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen,
			SyntacticAnalysisContextGeneratorContainsPos contextGenForPos, Corpus corpus, TrainingParameters params) throws IOException, CloneNotSupportedException {
		System.out.println("ContextGenerator: " + contextGen);       
		//根据完整的训练语料对语料中的每个词语计数，得到一hashmap，键是词语，值是出现的次数【为训练词性标注的模型准备】
		HashMap<String,Integer> dict = SyntacticAnalysisMEContainsPos.buildDictionary(new File(corpus.trainFile), "utf-8");
		FeatureContainsPosTools tools = new FeatureContainsPosTools(dict);
		//训练模型
		//(1)训练词性标记模型
		SyntacticAnalysisMEContainsPos.train(new File(corpus.trainFile), params, contextGenForPos, corpus.encoding);
		//（2）训练句法分析模型
		SyntacticAnalysisME.train(new File(corpus.trainFile), params, contextGen, corpus.encoding);
		
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
        InputStream configStream = SyntacticAnalysisRunForOneStep.class.getClassLoader().getResourceAsStream("com/wxw/run/corpus.properties");
        config.load(configStream);
        Corpus[] corpora = getCorporaFromConf(config);//获取语料

        SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen = getContextGenerator(config);

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
	private static void runFeatureOnCorporaByFlag(SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen, Corpus[] corpora,
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
	private static void evaluateOnCorpus(SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen, Corpus corpus,
			TrainingParameters params) throws IOException, CloneNotSupportedException {
		System.out.println("ContextGenerator: " + contextGen);

        SyntacticAnalysisModel treemodel = SyntacticAnalysisME.readModel(new File(corpus.treemodeltxtFile), params, contextGen, corpus.encoding);	
        SyntacticAnalysisME treetagger = new SyntacticAnalysisME(treemodel,contextGen);
		POSModel model = new POSModelLoader().load(new File(corpus.posenglish));
		POSTaggerMEExtend postagger = new POSTaggerMEExtend(model);
        
        SyntacticAnalysisMeasure measure = new SyntacticAnalysisMeasure();
        SyntacticAnalysisEvaluatorForOneStep evaluator = null;
        SyntacticAnalysisErrorPrinter printer = null;
        if(corpus.errorFile != null){
        	System.out.println("Print error to file " + corpus.errorFile);
        	printer = new SyntacticAnalysisErrorPrinter(new FileOutputStream(corpus.errorFile));    	
        	evaluator = new SyntacticAnalysisEvaluatorForOneStep(postagger,treetagger,printer);
        }else{
        	evaluator = new SyntacticAnalysisEvaluatorForOneStep(postagger,treetagger);
        }
        evaluator.setMeasure(measure);
        ObjectStream<String> linesStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File(corpus.testFile)), corpus.encoding);
        ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(linesStream);
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
	private static void modelOutOnCorpus(SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen, Corpus corpus,
			TrainingParameters params) throws UnsupportedOperationException, FileNotFoundException, IOException, CloneNotSupportedException {
		System.out.println("ContextGenerator: " + contextGen);       
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
	private static void trainOnCorpus(SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen, Corpus corpus,
			TrainingParameters params) throws UnsupportedOperationException, FileNotFoundException, IOException, CloneNotSupportedException {
		System.out.println("ContextGenerator: " + contextGen);       
		SyntacticAnalysisME.train(new File(corpus.trainFile), params, contextGen, corpus.encoding);
	}
}
