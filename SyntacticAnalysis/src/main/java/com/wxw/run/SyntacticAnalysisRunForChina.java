package com.wxw.run;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.wxw.cross.SyntacticAnalysisCrossValidationForChina;
import com.wxw.evaluate.SyntacticAnalysisErrorPrinter;
import com.wxw.evaluate.SyntacticAnalysisMeasure;
import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.feature.SyntacticAnalysisContextGeneratorConf;
import com.wxw.model.all.unused.SyntacticAnalysisRunForOneStep;
import com.wxw.model.bystep.SyntacticAnalysisEvaluatorForChina;
import com.wxw.model.bystep.SyntacticAnalysisMEForBuildAndCheck;
import com.wxw.model.bystep.SyntacticAnalysisMEForChunk;
import com.wxw.model.bystep.SyntacticAnalysisModelForBuildAndCheck;
import com.wxw.model.bystep.SyntacticAnalysisModelForChunk;
import com.wxw.stream.FileInputStreamFactory;
import com.wxw.stream.PlainTextByTreeStream;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.stream.SyntacticAnalysisSampleStream;
import com.wxw.tree.HeadTreeNode;
import com.wxw.wordsegandpos.feature.WordSegAndPosContextGenerator;
import com.wxw.wordsegandpos.feature.WordSegAndPosContextGeneratorConfExtend;
import com.wxw.wordsegandpos.model.WordSegAndPosME;
import com.wxw.wordsegandpos.model.WordSegAndPosModel;
import com.wxw.wordsegandpos.model.WordSegAndPosModelLoader;

import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;

/**
 * 中文句法分析运行类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisRunForChina {

	private static String flag = "train";

	public static class Corpus{
		
		public String name;
		public String encoding;
		public String trainFile;
		public String testFile;
		public String posChina;
		public String chunkmodelbinaryFile;
		public String chunkmodeltxtFile;
		public String buildmodelbinaryFile;
		public String buildmodeltxtFile;
		public String checkmodelbinaryFile;
		public String checkmodeltxtFile;
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
			String posChina = config.getProperty(name + "." + "corpus.poschina.file");
			String chunkmodelbinaryFile = config.getProperty(name + "." + "corpus.chunkmodelbinary.file");
			String chunkmodeltxtFile = config.getProperty(name + "." + "corpus.chunkmodeltxt.file");
			String buildmodelbinaryFile = config.getProperty(name + "." + "corpus.buildmodelbinary.file");
			String buildmodeltxtFile = config.getProperty(name + "." + "corpus.buildmodeltxt.file");
			String checkmodelbinaryFile = config.getProperty(name + "." + "corpus.checkmodelbinary.file");
			String checkmodeltxtFile = config.getProperty(name + "." + "corpus.checkmodeltxt.file");
			String errorFile = config.getProperty(name + "." + "corpus.error.file");
			Corpus corpus = new Corpus();
			corpus.name = name;
			corpus.encoding = encoding;
			corpus.trainFile = trainFile;
			corpus.testFile = testFile;
			corpus.chunkmodeltxtFile = chunkmodeltxtFile;
			corpus.chunkmodelbinaryFile = chunkmodelbinaryFile;
			corpus.posChina = posChina;
			corpus.buildmodeltxtFile = buildmodeltxtFile;
			corpus.buildmodelbinaryFile = buildmodelbinaryFile;
			corpus.checkmodeltxtFile = checkmodeltxtFile;
			corpus.checkmodelbinaryFile = checkmodelbinaryFile;
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
		}
	}
	
	/**
	 * 交叉验证
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
        SyntacticAnalysisCrossValidationForChina crossValidator = new SyntacticAnalysisCrossValidationForChina("zh", params);

        System.out.println(contextGen);
        crossValidator.evaluate(new File(corpus.posChina),sampleStream, 10, contextGen);
	}

	/**
	 * 根据配置文件获取特征处理类
	 * @throws IOException
	 * @throws CloneNotSupportedException 
	 * @throws UnsupportedOperationException 
	 */
	private static void runFeature() throws IOException, UnsupportedOperationException, CloneNotSupportedException {
		TrainingParameters params = TrainingParameters.defaultParams();
		params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(1));
	
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
		WordSegAndPosModel posmodel = new WordSegAndPosModelLoader().load(new File(corpus.posChina));
		WordSegAndPosContextGenerator generator = new WordSegAndPosContextGeneratorConfExtend();
		WordSegAndPosME postagger = new WordSegAndPosME(posmodel, generator);
        SyntacticAnalysisModelForChunk chunkmodel = SyntacticAnalysisMEForChunk.readModel(new File(corpus.chunkmodeltxtFile), params, contextGen, corpus.encoding);	
        SyntacticAnalysisMEForChunk chunktagger = new SyntacticAnalysisMEForChunk(chunkmodel,contextGen);
      
        SyntacticAnalysisModelForBuildAndCheck buildandcheckmodel = SyntacticAnalysisMEForBuildAndCheck.readModel(new File(corpus.buildmodeltxtFile), new File(corpus.checkmodeltxtFile),params, contextGen, corpus.encoding);	
        SyntacticAnalysisMEForBuildAndCheck buildandchecktagger = new SyntacticAnalysisMEForBuildAndCheck(buildandcheckmodel,contextGen);
        
        SyntacticAnalysisMeasure measure = new SyntacticAnalysisMeasure();
        SyntacticAnalysisEvaluatorForChina evaluator = null;
        SyntacticAnalysisErrorPrinter printer = null;
        if(corpus.errorFile != null){
        	System.out.println("Print error to file " + corpus.errorFile);
        	printer = new SyntacticAnalysisErrorPrinter(new FileOutputStream(corpus.errorFile));    	
        	evaluator = new SyntacticAnalysisEvaluatorForChina(postagger,chunktagger,buildandchecktagger,printer);
        }else{
        	evaluator = new SyntacticAnalysisEvaluatorForChina(postagger,chunktagger,buildandchecktagger);
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
		//训练句法分析模型
		SyntacticAnalysisMEForChunk.train(new File(corpus.trainFile), new File(corpus.chunkmodeltxtFile),params, contextGen, corpus.encoding);
		SyntacticAnalysisMEForBuildAndCheck.train(new File(corpus.trainFile), new File(corpus.buildmodeltxtFile),new File(corpus.checkmodeltxtFile),params, contextGen, corpus.encoding);

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
		//训练句法分析模型
		SyntacticAnalysisMEForChunk.train(new File(corpus.trainFile), params, contextGen, corpus.encoding);
		SyntacticAnalysisMEForBuildAndCheck.train(new File(corpus.trainFile), params, contextGen, corpus.encoding);
	}
}
