package com.wxw.model.bystep;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.wxw.evaluate.SyntacticAnalysisEvaluateMonitor;
import com.wxw.evaluate.SyntacticAnalysisMeasure;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tool.EvaluationTools;
import com.wxw.tree.ActionsToTree;
import com.wxw.tree.PhraseGenerateTree;
import com.wxw.tree.TreeNode;
import com.wxw.tree.TreeToActions;
import com.wxw.tree.TreeToNonTerminal;
import com.wxw.wordsegandpos.model.WordSegAndPosME;

import opennlp.tools.util.eval.Evaluator;

/**
 * 中文句法分析评测类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisEvaluatorForChina extends Evaluator<SyntacticAnalysisSample>{

	private Logger logger = Logger.getLogger(SyntacticAnalysisEvaluatorForByStep.class.getName());
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
		TreeNode buildAndCheckTree = null;
		//在验证的过程中，有些配ignore的句子，也会来验证，这是没有意义的，为了防止这种情况，就加入判断
		if(sample.getActions().size() == 0 && sample.getWords().size() == 0){
			return new SyntacticAnalysisSample(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		}else{
			try {
				List<String> words = sample.getWords();
				List<String> actionsRef = sample.getActions();
				ActionsToTree att = new ActionsToTree();
				//参考样本没有保存完整的一棵树，需要将动作序列转成一颗完整的树
				TreeNode treeRef = att.actionsToTree(words, actionsRef);
				TreeToNonTerminal ttn1 = new TreeToNonTerminal();
				List<EvaluationTools> etRef = ttn1.getTreeToNonterminal(treeRef);
				String[][] poses = postagger.tag(5, words.toArray(new String[words.size()]));
				List<List<TreeNode>> posTree = SyntacticAnalysisSample.toPosTree(words.toArray(new String[words.size()]), poses);
				List<List<TreeNode>> chunkTree = chunktagger.tagKChunk(20, posTree, null);	
				buildAndCheckTree = buildAndChecktagger.tagBuildAndCheck(chunkTree, null);
				if(buildAndCheckTree == null){
					samplePre = new SyntacticAnalysisSample(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
					measure.countNodeDecodeTrees(buildAndCheckTree);
				}else{
					TreeToActions tta = new TreeToActions();
					PhraseGenerateTree pgt = new PhraseGenerateTree();
					TreeNode treePre = pgt.generateTree("("+buildAndCheckTree.toBracket()+")");
					samplePre = tta.treeToAction(treePre);
					TreeToNonTerminal ttn2 = new TreeToNonTerminal();
					List<EvaluationTools> etPre = ttn2.getTreeToNonterminal(treePre);
					measure.update(etRef, etPre);
				}	
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			} catch(Exception e){
				if (logger.isLoggable(Level.WARNING)) {						
                    logger.warning("Error during parsing, ignoring sentence: " + buildAndCheckTree.toBracket());
                }	
				samplePre = new SyntacticAnalysisSample(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
			}
			return samplePre;
		}
	}
}
