package com.wxw.modelUnused;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.wxw.evaluate.SyntacticAnalysisEvaluateMonitor;
import com.wxw.evaluate.SyntacticAnalysisMeasure;
import com.wxw.modelBystep.POSTaggerMEExtend;
import com.wxw.modelBystep.SyntacticAnalysisEvaluatorForByStep;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.ActionsToHeadTree;
import com.wxw.tree.HeadTreeNode;

import opennlp.tools.util.eval.Evaluator;

/**
 * 一步训练树模型评估器
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisEvaluatorForOneStep extends Evaluator<SyntacticAnalysisSample<HeadTreeNode>>{
	private Logger logger = Logger.getLogger(SyntacticAnalysisEvaluatorForByStep.class.getName());

	private POSTaggerMEExtend postagger;
	private SyntacticAnalysisME treetagger;
	private SyntacticAnalysisMeasure measure;
	
	public SyntacticAnalysisEvaluatorForOneStep(POSTaggerMEExtend postagger,SyntacticAnalysisME treetagger) {
		this.postagger = postagger;
		this.treetagger = treetagger;
	}
	
	public SyntacticAnalysisEvaluatorForOneStep(POSTaggerMEExtend postagger,SyntacticAnalysisME treetagger,SyntacticAnalysisEvaluateMonitor... evaluateMonitors) {
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
				ActionsToHeadTree att = new ActionsToHeadTree();
				//参考样本没有保存完整的一棵树，需要将动作序列转成一颗完整的树
				HeadTreeNode treeRef = att.actionsToTree(words, actionsRef);
				List<List<HeadTreeNode>> posTree = postagger.tagKpos(20,words.toArray(new String[words.size()]));
				List<List<HeadTreeNode>> chunkTree = treetagger.tagKChunk(20, posTree, null);	
				treePre = treetagger.tagBuildAndCheck(chunkTree, null);
				if(treePre == null){
					samplePre = new SyntacticAnalysisSample<HeadTreeNode>(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
					measure.countNodeDecodeTrees(treePre);
				}else{
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
