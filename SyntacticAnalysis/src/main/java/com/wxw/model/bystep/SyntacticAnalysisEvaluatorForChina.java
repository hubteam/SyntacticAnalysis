package com.wxw.model.bystep;

import java.util.List;

import com.wxw.evaluate.SyntacticAnalysisEvaluateMonitor;
import com.wxw.evaluate.SyntacticAnalysisMeasure;
import com.wxw.model.all.SyntacticAnalysisME;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.TreeNode;
import com.wxw.tree.TreeToActions;
import com.wxw.wordsegandpos.model.WordSegAndPosME;

import opennlp.tools.util.eval.Evaluator;

/**
 * 中文句法分析评测类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisEvaluatorForChina extends Evaluator<SyntacticAnalysisSample>{

	private WordSegAndPosME postagger;
	private SyntacticAnalysisMEForChunk chunktagger;
	private SyntacticAnalysisMEForBuildAndCheck buildAndChecktagger;
	private SyntacticAnalysisMeasure measure;
	
	public SyntacticAnalysisEvaluatorForChina(WordSegAndPosME postagger,SyntacticAnalysisMEForChunk chunktagger,SyntacticAnalysisMEForBuildAndCheck buildAndChecktagger) {
		this.postagger = postagger;
		this.chunktagger = chunktagger;
		this.buildAndChecktagger = buildAndChecktagger;
	}
	
	public SyntacticAnalysisEvaluatorForChina(WordSegAndPosME postagger,SyntacticAnalysisMEForChunk chunktagger,SyntacticAnalysisMEForBuildAndCheck buildAndChecktagger,SyntacticAnalysisEvaluateMonitor... evaluateMonitors) {
		super(evaluateMonitors);
		this.postagger = postagger;
		this.chunktagger = chunktagger;
		this.buildAndChecktagger = buildAndChecktagger;
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
			List<List<TreeNode>> chunkTree = chunktagger.tagKChunk(5, posTree, null);	
			TreeNode buildAndCheckTree = buildAndChecktagger.tagBuildAndCheck(chunkTree, null);
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
