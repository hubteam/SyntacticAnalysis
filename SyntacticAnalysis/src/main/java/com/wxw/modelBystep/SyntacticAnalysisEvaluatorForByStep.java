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

import opennlp.tools.util.eval.Evaluator;
/**
 * 分步骤训练模型评估类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisEvaluatorForByStep extends Evaluator<SyntacticAnalysisSample<HeadTreeNode>>{

	private Logger logger = Logger.getLogger(SyntacticAnalysisEvaluatorForByStep.class.getName());
	private POSTaggerMEExtend postagger;
	private SyntacticAnalysisMEForChunk chunktagger;
	private SyntacticAnalysisMEForBuildAndCheck buildAndChecktagger;
	private SyntacticAnalysisMeasure measure;
	
	public SyntacticAnalysisEvaluatorForByStep(POSTaggerMEExtend postagger,SyntacticAnalysisMEForChunk chunktagger,SyntacticAnalysisMEForBuildAndCheck buildAndChecktagger) {
		this.postagger = postagger;
		this.chunktagger = chunktagger;
		this.buildAndChecktagger = buildAndChecktagger;
	}
	
	public SyntacticAnalysisEvaluatorForByStep(POSTaggerMEExtend postagger,SyntacticAnalysisMEForChunk chunktagger,SyntacticAnalysisMEForBuildAndCheck buildAndChecktagger, SyntacticAnalysisEvaluateMonitor... evaluateMonitors) {
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
				List<List<HeadTreeNode>> posTree = postagger.tagKpos(20,words.toArray(new String[words.size()]));
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
}
