package com.wxw.model.bystep;

import java.util.List;

import com.wxw.evaluate.SyntacticAnalysisEvaluateMonitor;
import com.wxw.evaluate.SyntacticAnalysisMeasure;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.TreeNode;

import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.eval.Evaluator;
/**
 * 分步骤训练模型评估类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisEvaluatorForStep extends Evaluator<SyntacticAnalysisSample>{

	private POSTaggerME postagger;
	private SyntacticAnalysisMEForChunk chunktagger;
	private SyntacticAnalysisMEForBuildAndCheck buildAndChecktagger;
	private SyntacticAnalysisMeasure measure;
	
	public SyntacticAnalysisEvaluatorForStep(POSTaggerME postagger,SyntacticAnalysisMEForChunk chunktagger,SyntacticAnalysisMEForBuildAndCheck buildAndChecktagger) {
		this.postagger = postagger;
		this.chunktagger = chunktagger;
		this.buildAndChecktagger = buildAndChecktagger;
	}
	
	public SyntacticAnalysisEvaluatorForStep(POSTaggerME postagger,SyntacticAnalysisMEForChunk chunktagger,SyntacticAnalysisMEForBuildAndCheck buildAndChecktagger, SyntacticAnalysisEvaluateMonitor... evaluateMonitors) {
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
//		List<String> words = sample.getWords();
//		String[][] poses = postagger.tag(3, words.toArray(new String[words.size()]));
//		List<List<TreeNode>> posTree = SyntacticAnalysisSample.toPosTree(words.toArray(new String[words.size()]), poses);
//		List<List<TreeNode>> chunkTree = treetagger.tagChunk(3, posTree, null);
//		List<List<TreeNode>> buildAndCheckTree = treetagger.tagBuildAndCheck(3, chunkTree, null);
//		TreeNode tree = buildAndCheckTree.get(0).get(0);		
		return null;
	}

}
