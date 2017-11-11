package com.wxw.evaluate;

import com.wxw.model.SyntacticAnalysisME;
import com.wxw.model.SyntacticAnalysisMEForPos;
import com.wxw.stream.SyntacticAnalysisSample;

import opennlp.tools.util.eval.Evaluator;

/**
 * 评估器
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisEvaluator extends Evaluator<SyntacticAnalysisSample>{

	private SyntacticAnalysisMEForPos postagger;
	private SyntacticAnalysisME treetagger;
	private SyntacticAnalysisMeasure measure;
	
	public SyntacticAnalysisEvaluator(SyntacticAnalysisMEForPos postagger,SyntacticAnalysisME treetagger) {
		this.postagger = postagger;
		this.treetagger = treetagger;
	}
	
	public SyntacticAnalysisEvaluator(SyntacticAnalysisMEForPos postagger,SyntacticAnalysisME treetagger,SyntacticAnalysisEvaluateMonitor... evaluateMonitors) {
		super(evaluateMonitors);
		this.postagger = postagger;
		this.treetagger = treetagger;
	}
	
	/**
	 * 设置评估指标的对象
	 * @param measure 评估指标计算的对象
	 */
	public void setMeasure(SyntacticAnalysisMeasure measure){
		this.measure = measure;
	}
	
	/**
	 * 得到评估的指标
	 * @return
	 */
	public SyntacticAnalysisMeasure getMeasure(){
		return this.measure;
	}
	
	@Override
	protected SyntacticAnalysisSample processSample(SyntacticAnalysisSample sample) {
		// TODO Auto-generated method stub
		return null;
	}

}
