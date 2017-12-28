package com.wxw.cross;

import java.io.File;
import java.io.IOException;

import com.wxw.evaluate.SyntacticAnalysisEvaluateMonitor;
import com.wxw.evaluate.SyntacticAnalysisMeasure;
import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.modelBystep.SyntacticAnalysisEvaluatorForByStep;
import com.wxw.modelBystep.SyntacticAnalysisEvaluatorForChina;
import com.wxw.modelBystep.SyntacticAnalysisMEForBuildAndCheck;
import com.wxw.modelBystep.SyntacticAnalysisMEForChunk;
import com.wxw.modelBystep.SyntacticAnalysisMEForPos;
import com.wxw.modelBystep.SyntacticAnalysisModelForBuildAndCheck;
import com.wxw.modelBystep.SyntacticAnalysisModelForChunk;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.HeadTreeNode;
import com.wxw.wordsegandpos.feature.WordSegAndPosContextGenerator;
import com.wxw.wordsegandpos.feature.WordSegAndPosContextGeneratorConfExtend;
import com.wxw.wordsegandpos.model.WordSegAndPosME;
import com.wxw.wordsegandpos.model.WordSegAndPosModel;
import com.wxw.wordsegandpos.model.WordSegAndPosModelLoader;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.eval.CrossValidationPartitioner;

/**
 * 分步训练中文句法分析模型的交叉验证类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisCrossValidationForChina {
	private final String languageCode;
	private final TrainingParameters params;
	private SyntacticAnalysisEvaluateMonitor[] monitor;
	
	/**
	 * 构造
	 * @param languageCode 编码格式
	 * @param params 训练的参数
	 * @param listeners 监听器
	 */
	public SyntacticAnalysisCrossValidationForChina(String languageCode,TrainingParameters params,SyntacticAnalysisEvaluateMonitor... monitor){
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
			WordSegAndPosModel posmodel = new WordSegAndPosModelLoader().load(file);
			WordSegAndPosContextGenerator generator = new WordSegAndPosContextGeneratorConfExtend();
			WordSegAndPosME postagger = new WordSegAndPosME(posmodel, generator);
			//训练句法分析模型
			SyntacticAnalysisModelForChunk chunkModel = SyntacticAnalysisMEForChunk.train(languageCode, trainingSampleStream, params, contextGenerator);
			trainingSampleStream.reset();
			SyntacticAnalysisModelForBuildAndCheck buildAndCheckModel = SyntacticAnalysisMEForBuildAndCheck.train(languageCode, trainingSampleStream, params, contextGenerator);

			SyntacticAnalysisEvaluatorForChina evaluator = new SyntacticAnalysisEvaluatorForChina(postagger,new SyntacticAnalysisMEForChunk(chunkModel, contextGenerator),
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


