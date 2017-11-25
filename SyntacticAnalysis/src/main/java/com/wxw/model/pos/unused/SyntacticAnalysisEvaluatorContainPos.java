package com.wxw.model.pos.unused;

import org.junit.Test;

import com.wxw.evaluate.SyntacticAnalysisEvaluateMonitor;
import com.wxw.evaluate.SyntacticAnalysisMeasure;
import com.wxw.model.all.SyntacticAnalysisME;
import com.wxw.stream.SyntacticAnalysisSample;

import opennlp.tools.util.eval.Evaluator;

/**
 * 评估器中的词性标注方法是自己写的
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisEvaluatorContainPos extends Evaluator<SyntacticAnalysisSample>{

	private SyntacticAnalysisMEForPos postagger;
	private SyntacticAnalysisME treetagger;
	private SyntacticAnalysisMeasure measure;
	
	public SyntacticAnalysisEvaluatorContainPos(SyntacticAnalysisMEForPos postagger,SyntacticAnalysisME treetagger) {
		this.postagger = postagger;
		this.treetagger = treetagger;
	}
	
	public SyntacticAnalysisEvaluatorContainPos(SyntacticAnalysisMEForPos postagger,SyntacticAnalysisME treetagger,SyntacticAnalysisEvaluateMonitor... evaluateMonitors) {
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
