package com.wxw.modelUnused;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.wxw.evaluate.SyntacticAnalysisEvaluateMonitor;
import com.wxw.evaluate.SyntacticAnalysisMeasure;
import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.modelBystep.POSTaggerMEExtend;
import com.wxw.posModelUnused.FeatureContainsPosTools;
import com.wxw.posModelUnused.SyntacticAnalysisContextGeneratorContainsPos;
import com.wxw.posModelUnused.SyntacticAnalysisEvaluatorContainsPos;
import com.wxw.posModelUnused.SyntacticAnalysisMEContainsPos;
import com.wxw.posModelUnused.SyntacticAnalysisModelContainsPos;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.HeadTreeNode;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.eval.CrossValidationPartitioner;

/**
 * 一步训练句法模型的交叉验证类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisCrossValidationForOneStep {
	private final String languageCode;
	private final TrainingParameters params;
	private SyntacticAnalysisEvaluateMonitor[] monitor;
	
	/**
	 * 构造
	 * @param languageCode 编码格式
	 * @param params 训练的参数
	 * @param listeners 监听器
	 */
	public SyntacticAnalysisCrossValidationForOneStep(String languageCode,TrainingParameters params,SyntacticAnalysisEvaluateMonitor... monitor){
		this.languageCode = languageCode;
		this.params = params;
		this.monitor = monitor;
	}
	
	/**
	 * 交叉验证十折评估
	 * @param file 词性标注的模型文件
	 * @param sample 样本流
	 * @param nFolds 折数
	 * @param contextGenerator 上下文
	 * @throws IOException io异常
	 */
	public void evaluate(File file,ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sample, int nFolds,
			SyntacticAnalysisContextGenerator<HeadTreeNode> contextGenerator) throws IOException{
		CrossValidationPartitioner<SyntacticAnalysisSample<HeadTreeNode>> partitioner = new CrossValidationPartitioner<SyntacticAnalysisSample<HeadTreeNode>>(sample, nFolds);
		int run = 1;
		//小于折数的时候
		while(partitioner.hasNext()){
			System.out.println("Run"+run+"...");
			CrossValidationPartitioner.TrainingSampleStream<SyntacticAnalysisSample<HeadTreeNode>> trainingSampleStream = partitioner.next();
			POSModel posmodel = new POSModelLoader().load(file);
			POSTaggerMEExtend postagger = new POSTaggerMEExtend(posmodel);
			//训练句法分析模型
			SyntacticAnalysisModel model = SyntacticAnalysisME.train(languageCode, trainingSampleStream, params, contextGenerator);
			SyntacticAnalysisEvaluatorForOneStep evaluator = new SyntacticAnalysisEvaluatorForOneStep(postagger,new SyntacticAnalysisME(model, contextGenerator),monitor);
			SyntacticAnalysisMeasure measure = new SyntacticAnalysisMeasure();
			
			evaluator.setMeasure(measure);
	        //设置测试集（在测试集上进行评价）
	        evaluator.evaluate(trainingSampleStream.getTestSampleStream());
	        
	        System.out.println(measure);
	        run++;
		}
	}
	
	/**
	 * 交叉验证十折评估
	 * @param sample 样本流
	 * @param nFolds 折数
	 * @param contextGenerator 上下文
	 * @throws IOException io异常
	 */
	public void evaluateContainsPos(ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sample, int nFolds,
			SyntacticAnalysisContextGenerator<HeadTreeNode> contextGenerator,SyntacticAnalysisContextGeneratorContainsPos contextGenForPos) throws IOException{
		CrossValidationPartitioner<SyntacticAnalysisSample<HeadTreeNode>> partitioner = new CrossValidationPartitioner<SyntacticAnalysisSample<HeadTreeNode>>(sample, nFolds);
		int run = 1;
		//小于折数的时候
		while(partitioner.hasNext()){
			System.out.println("Run"+run+"...");
			CrossValidationPartitioner.TrainingSampleStream<SyntacticAnalysisSample<HeadTreeNode>> trainingSampleStream = partitioner.next();
			//根据完整的训练语料对语料中的每个词语计数，得到一hashmap，键是词语，值是出现的次数【为训练词性标注的模型准备】
			HashMap<String,Integer> dict = SyntacticAnalysisMEContainsPos.buildDictionary(trainingSampleStream);
			FeatureContainsPosTools tools = new FeatureContainsPosTools(dict);
			trainingSampleStream.reset();
			//训练模型
			//(1)训练词性标记模型
			SyntacticAnalysisModelContainsPos posModel = SyntacticAnalysisMEContainsPos.train(languageCode, trainingSampleStream, params, contextGenForPos);
			trainingSampleStream.reset();
			//（2）训练句法分析模型
			SyntacticAnalysisModel model = SyntacticAnalysisME.train(languageCode, trainingSampleStream, params, contextGenerator);
			SyntacticAnalysisEvaluatorContainsPos evaluator = new SyntacticAnalysisEvaluatorContainsPos(new SyntacticAnalysisMEContainsPos(posModel,contextGenForPos),
					new SyntacticAnalysisME(model, contextGenerator),monitor);
			SyntacticAnalysisMeasure measure = new SyntacticAnalysisMeasure();
			
			evaluator.setMeasure(measure);
	        //设置测试集（在测试集上进行评价）
	        evaluator.evaluate(trainingSampleStream.getTestSampleStream());
	        
	        System.out.println(measure);
	        run++;
		}
	}
}

