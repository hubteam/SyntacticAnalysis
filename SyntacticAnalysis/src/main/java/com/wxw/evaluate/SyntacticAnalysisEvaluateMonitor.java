package com.wxw.evaluate;

import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.HeadTreeNode;

import opennlp.tools.util.eval.EvaluationMonitor;

public class SyntacticAnalysisEvaluateMonitor implements EvaluationMonitor<SyntacticAnalysisSample<HeadTreeNode>>{

	/**
	 * 预测正确的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	@Override
	public void correctlyClassified(SyntacticAnalysisSample<HeadTreeNode> arg0, SyntacticAnalysisSample<HeadTreeNode> arg1) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 预测正确的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	@Override
	public void missclassified(SyntacticAnalysisSample<HeadTreeNode> arg0, SyntacticAnalysisSample<HeadTreeNode> arg1) {
		// TODO Auto-generated method stub
		
	}

}
