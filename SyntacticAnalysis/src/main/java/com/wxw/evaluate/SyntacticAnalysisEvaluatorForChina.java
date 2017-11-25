package com.wxw.evaluate;

import com.wxw.model.all.SyntacticAnalysisME;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.wordsegandpos.model.WordSegAndPosME;

import opennlp.tools.util.eval.Evaluator;

/**
 * 中文句法分析评测类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisEvaluatorForChina extends Evaluator<SyntacticAnalysisSample>{

	private WordSegAndPosME postagger;
	private SyntacticAnalysisME treetagger;
	private SyntacticAnalysisMeasure measure;
	
	public SyntacticAnalysisEvaluatorForChina(WordSegAndPosME postagger,SyntacticAnalysisME treetagger) {
		this.postagger = postagger;
		this.treetagger = treetagger;
	}
	
	public SyntacticAnalysisEvaluatorForChina(WordSegAndPosME postagger,SyntacticAnalysisME treetagger,SyntacticAnalysisEvaluateMonitor... evaluateMonitors) {
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
