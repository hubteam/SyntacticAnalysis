package com.wxw.model.pos.unused;

import java.util.List;

import org.junit.Test;

import com.wxw.evaluate.SyntacticAnalysisEvaluateMonitor;
import com.wxw.evaluate.SyntacticAnalysisMeasure;
import com.wxw.model.all.SyntacticAnalysisME;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.TreeNode;
import com.wxw.tree.TreeToActions;

import opennlp.tools.util.eval.Evaluator;

/**
 * 评估器中的词性标注方法是自己写的
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisEvaluatorContainsPos extends Evaluator<SyntacticAnalysisSample>{

	private SyntacticAnalysisMEContainsPos postagger;
	private SyntacticAnalysisME treetagger;
	private SyntacticAnalysisMeasure measure;
	
	public SyntacticAnalysisEvaluatorContainsPos(SyntacticAnalysisMEContainsPos postagger,SyntacticAnalysisME treetagger) {
		this.postagger = postagger;
		this.treetagger = treetagger;
	}
	
	public SyntacticAnalysisEvaluatorContainsPos(SyntacticAnalysisMEContainsPos postagger,SyntacticAnalysisME treetagger,SyntacticAnalysisEvaluateMonitor... evaluateMonitors) {
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
		SyntacticAnalysisSample samplePre = null;
		try{
			List<String> words = sample.getWords();
			List<String> actionsRef = sample.getActions();
			String[][] poses = postagger.tag(5, words.toArray(new String[words.size()]));
			List<List<TreeNode>> posTree = SyntacticAnalysisSample.toPosTree(words.toArray(new String[words.size()]), poses);
			List<List<TreeNode>> chunkTree = treetagger.tagKChunk(5, posTree, null);	
			TreeNode buildAndCheckTree = treetagger.tagBuildAndCheck(chunkTree, null);
			if(buildAndCheckTree == null){
				measure.countNodeDecodeTrees(buildAndCheckTree);
			}else{
				TreeToActions tta = new TreeToActions();
				samplePre = tta.treeToAction(buildAndCheckTree);
				List<String> actionsPre = samplePre.getActions();
				measure.update(actionsRef, actionsPre);
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return samplePre;
	}
}
