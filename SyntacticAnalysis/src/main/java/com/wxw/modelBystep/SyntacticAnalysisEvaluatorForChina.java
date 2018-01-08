package com.wxw.modelBystep;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.wxw.evaluate.SyntacticAnalysisEvaluateMonitor;
import com.wxw.evaluate.SyntacticAnalysisMeasure;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.ActionsToTree;
import com.wxw.tree.HeadTreeNode;
import com.wxw.tree.HeadTreeToActions;
import com.wxw.tree.TreeNode;
import com.wxw.tree.TreeToHeadTree;
import com.wxw.wordsegandpos.model.WordSegAndPosME;

import opennlp.tools.util.eval.Evaluator;

/**
 * 中文句法分析评测类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisEvaluatorForChina extends Evaluator<SyntacticAnalysisSample<HeadTreeNode>>{

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
	protected SyntacticAnalysisSample<HeadTreeNode> processSample(SyntacticAnalysisSample<HeadTreeNode> sample) {
		SyntacticAnalysisSample<HeadTreeNode> samplePre = null;
		HeadTreeNode treePre = null;
		//在验证的过程中，有些配ignore的句子，也会来验证，这是没有意义的，为了防止这种情况，就加入判断
		if(sample.getActions().size() == 0 && sample.getWords().size() == 0){
			return new SyntacticAnalysisSample<HeadTreeNode>(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		}else{
			try {
				List<String> words = sample.getWords();
				List<String> actionsRef = sample.getActions();
				ActionsToTree att = new ActionsToTree();
				//参考样本没有保存完整的一棵树，需要将动作序列转成一颗完整的树
				TreeNode treeRef = att.actionsToTree(words, actionsRef);
				String[][] poses = postagger.tag(5, words.toArray(new String[words.size()]));
				List<List<HeadTreeNode>> posTree = toPosTree(words.toArray(new String[words.size()]), poses);
				List<List<HeadTreeNode>> chunkTree = chunktagger.tagKChunk(20, posTree, null);	
				treePre = buildAndChecktagger.tagBuildAndCheck(chunkTree, null);
				if(treePre == null){
					samplePre = new SyntacticAnalysisSample<HeadTreeNode>(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
					measure.countNodeDecodeTrees(treePre);
				}else{
					HeadTreeToActions tta = new HeadTreeToActions();
					samplePre = tta.treeToAction(treePre);
					measure.update(treeRef, treePre);
				}	
			} catch(Exception e){
				if (logger.isLoggable(Level.WARNING)) {						
                    logger.warning("Error during parsing, ignoring sentence: " + treePre.toBracket());
                }	
				samplePre = new SyntacticAnalysisSample<HeadTreeNode>(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
			}
			return samplePre;
		}
	}
	
	/**
	 * 将词性标注和词语转成树的形式
	 * @param words k个最好的词语序列
	 * @param poses k个最好的词性标注序列
	 * @return
	 */
	public static List<List<HeadTreeNode>> toPosTree(String[] words, String[][] poses){
		List<List<HeadTreeNode>> posTrees = new ArrayList<>();
		for (int i = 0; i < poses.length; i++) {
			List<HeadTreeNode> posTree = new ArrayList<HeadTreeNode>();
			for (int j = 0; j < poses[i].length && j < words.length; j++) {
				HeadTreeNode pos = new HeadTreeNode(poses[i][j]);
				HeadTreeNode word = new HeadTreeNode(words[j]);
				pos.addChild(word);
				word.setParent(pos);
				pos.setHeadWords(words[j]);
				posTree.add(pos);
			}
			posTrees.add(posTree);
		}
		return posTrees;
	}
}
