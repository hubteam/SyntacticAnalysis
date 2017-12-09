package com.wxw.model.bystep;

import java.util.ArrayList;
import java.util.List;

import com.wxw.evaluate.SyntacticAnalysisEvaluateMonitor;
import com.wxw.evaluate.SyntacticAnalysisMeasure;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.PhraseGenerateTree;
import com.wxw.tree.TreeNode;
import com.wxw.tree.TreeToActions;

import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.eval.Evaluator;
/**
 * 分步骤训练模型评估类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisEvaluatorForByStep extends Evaluator<SyntacticAnalysisSample>{

	private SyntacticAnalysisMEForPos postagger;
	private SyntacticAnalysisMEForChunk chunktagger;
	private SyntacticAnalysisMEForBuildAndCheck buildAndChecktagger;
	private SyntacticAnalysisMeasure measure;
	private long countNotDecodeTree = 0;
	
	public SyntacticAnalysisEvaluatorForByStep(SyntacticAnalysisMEForPos postagger,SyntacticAnalysisMEForChunk chunktagger,SyntacticAnalysisMEForBuildAndCheck buildAndChecktagger) {
		this.postagger = postagger;
		this.chunktagger = chunktagger;
		this.buildAndChecktagger = buildAndChecktagger;
	}
	
	public SyntacticAnalysisEvaluatorForByStep(SyntacticAnalysisMEForPos postagger,SyntacticAnalysisMEForChunk chunktagger,SyntacticAnalysisMEForBuildAndCheck buildAndChecktagger, SyntacticAnalysisEvaluateMonitor... evaluateMonitors) {
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
	int i = 0;
	@Override
	protected SyntacticAnalysisSample processSample(SyntacticAnalysisSample sample) {
		SyntacticAnalysisSample samplePre = null;
		//在验证的过程中，有些配ignore的句子，也会来验证，这是没有意义的，为了防止这种情况，就加入判断
		if(sample.getActions().size() == 0 && sample.getWords().size() == 0){
			return new SyntacticAnalysisSample(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		}else{
			try {
				List<String> actionsRef = sample.getActions();
				List<String> words = sample.getWords();
				for (int i = 0; i < words.size(); i++) {
					System.out.print(words.get(i)+" ");
				}
				System.out.println();
				List<List<TreeNode>> posTree = postagger.tagKpos(5,words.toArray(new String[words.size()]));
				List<List<TreeNode>> chunkTree = chunktagger.tagKChunk(5, posTree, null);	
				TreeNode buildAndCheckTree = buildAndChecktagger.tagBuildAndCheck(chunkTree, null);
				if(buildAndCheckTree == null){
					samplePre = new SyntacticAnalysisSample(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
					measure.countNodeDecodeTrees(buildAndCheckTree);
				}else{
					TreeToActions tta = new TreeToActions();
					PhraseGenerateTree pgt = new PhraseGenerateTree();
					TreeNode node = pgt.generateTree("("+buildAndCheckTree.toBracket()+")");
					samplePre = tta.treeToAction(node);
					List<String> actionsPre = samplePre.getActions();
					measure.update(actionsRef, actionsPre);
					i++;
					System.err.println(i);
				}	
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return samplePre;
		}
	}

}
