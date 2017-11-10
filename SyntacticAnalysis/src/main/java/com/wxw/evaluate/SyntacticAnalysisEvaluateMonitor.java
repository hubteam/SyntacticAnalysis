package com.wxw.evaluate;

import com.wxw.stream.SyntacticAnalysisSample;

import opennlp.tools.util.eval.EvaluationMonitor;

public class SyntacticAnalysisEvaluateMonitor implements EvaluationMonitor<SyntacticAnalysisSample>{

	/**
	 * 预测正确的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	@Override
	public void correctlyClassified(SyntacticAnalysisSample arg0, SyntacticAnalysisSample arg1) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 预测正确的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	@Override
	public void missclassified(SyntacticAnalysisSample arg0, SyntacticAnalysisSample arg1) {
		// TODO Auto-generated method stub
		
	}

}
