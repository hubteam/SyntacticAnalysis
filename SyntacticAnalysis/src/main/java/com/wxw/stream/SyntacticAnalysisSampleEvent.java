package com.wxw.stream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.tree.TreeNode;

import opennlp.tools.ml.model.Event;
import opennlp.tools.util.AbstractEventStream;
import opennlp.tools.util.ObjectStream;

/**
 * 生成事件
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisSampleEvent  extends AbstractEventStream<SyntacticAnalysisSample>{

	private SyntacticAnalysisContextGenerator generator;
	
	/**
	 * 构造
	 * @param samples 样本流
	 * @param generator 上下文产生器
	 */
	public SyntacticAnalysisSampleEvent(ObjectStream<SyntacticAnalysisSample> samples,SyntacticAnalysisContextGenerator generator) {
		super(samples);
		this.generator = generator;
	}

	/**
	 * 生成事件
	 */
	@Override
	protected Iterator<Event> createEvents(SyntacticAnalysisSample sample) {
		List<String> words = sample.getWords();
		List<String> poses = sample.getPoses();
		List<String> actions = sample.getActions();
	
		List<TreeNode> chunkTree = sample.getChunkTree();
		List<List<TreeNode>> buildAndCheckTree = sample.getBuildAndCheckTree();
		String[][] ac = sample.getAdditionalContext();
		List<Event> events = generateEvents(words, poses, chunkTree, buildAndCheckTree,actions,ac);
        return events.iterator();
	}

	/**
	 * 事件生成
	 * @param words 词语序列
	 * @param posTree pos得到的子树
	 * @param chunkTree chunk得到的子树
	 * @param buildAndCheck buildAndCheck得到的子树
	 * @param actions 动作序列
	 * @param ac
	 * @return
	 */
	private List<Event> generateEvents( List<String> words, List<String> poses, List<TreeNode> chunkTree,
			List<List<TreeNode>> buildAndCheckTree, List<String> actions, String[][] ac) {
		List<Event> events = new ArrayList<Event>(actions.size());
		
		//chunk
		for (int i = words.size(); i < 2*words.size(); i++) {		
			String[] context = generator.getContextForChunk(i,chunkTree, actions, ac);
            events.add(new Event(actions.get(i), context));
		}
		//buildAndCheck
		for (int i = 2*words.size(); i < actions.size(); i=i+2) {
			String[] buildContext = generator.getContextForBuild(i,buildAndCheckTree, actions, ac);
            events.add(new Event(actions.get(i), buildContext));
            String[] checkContext = generator.getContextForCheck(i+1,buildAndCheckTree, actions, ac);
            events.add(new Event(actions.get(i+1), checkContext));
		}
		return events;
	}

}
