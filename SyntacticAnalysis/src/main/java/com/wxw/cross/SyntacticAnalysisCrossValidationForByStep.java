package com.wxw.cross;

import java.io.File;
import java.io.IOException;

import com.wxw.evaluate.SyntacticAnalysisEvaluateMonitor;
import com.wxw.evaluate.SyntacticAnalysisMeasure;
import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.model.bystep.POSTaggerMEExtend;
import com.wxw.model.bystep.SyntacticAnalysisEvaluatorForByStep;
import com.wxw.model.bystep.SyntacticAnalysisMEForBuildAndCheck;
import com.wxw.model.bystep.SyntacticAnalysisMEForChunk;
import com.wxw.model.bystep.SyntacticAnalysisMEForPos;
import com.wxw.model.bystep.SyntacticAnalysisModelForBuildAndCheck;
import com.wxw.model.bystep.SyntacticAnalysisModelForChunk;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.HeadTreeNode;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.eval.CrossValidationPartitioner;

/**
 * 分步训练句法模型的交叉验证
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisCrossValidationForByStep {
	private final String languageCode;
	private final TrainingParameters params;
	private SyntacticAnalysisEvaluateMonitor[] monitor;
	
	/**
	 * 构造
	 * @param languageCode 编码格式
	 * @param params 训练的参数
	 * @param listeners 监听器
	 */
	public SyntacticAnalysisCrossValidationForByStep(String languageCode,TrainingParameters params,SyntacticAnalysisEvaluateMonitor... monitor){
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
			POSModel posmodel = new POSModelLoader().load(file);
			POSTaggerMEExtend postagger = new POSTaggerMEExtend(posmodel);
//			SyntacticAnalysisMEForPos postagger = new SyntacticAnalysisMEForPos(posmodel);
			CrossValidationPartitioner.TrainingSampleStream<SyntacticAnalysisSample<HeadTreeNode>> trainingSampleStream = partitioner.next();			
			//训练句法分析模型
			SyntacticAnalysisModelForChunk chunkModel = SyntacticAnalysisMEForChunk.train(languageCode, trainingSampleStream, params, contextGenerator);
			trainingSampleStream.reset();
			SyntacticAnalysisModelForBuildAndCheck buildAndCheckModel = SyntacticAnalysisMEForBuildAndCheck.train(languageCode, trainingSampleStream, params, contextGenerator);

			SyntacticAnalysisEvaluatorForByStep evaluator = new SyntacticAnalysisEvaluatorForByStep(postagger,new SyntacticAnalysisMEForChunk(chunkModel, contextGenerator),
					new SyntacticAnalysisMEForBuildAndCheck(buildAndCheckModel,contextGenerator), monitor);
			SyntacticAnalysisMeasure measure = new SyntacticAnalysisMeasure();
			
			evaluator.setMeasure(measure);
	        //设置测试集（在测试集上进行评价）
	        evaluator.evaluate(trainingSampleStream.getTestSampleStream());
	        
	        System.out.println(measure);
	        run++;
		}
	}
}

