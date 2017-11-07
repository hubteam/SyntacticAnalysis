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
		List<String> actions = sample.getActions();
		List<TreeNode> posTree = sample.getPosTree();
		List<TreeNode> chunkTree = sample.getChunkTree();
		List<List<TreeNode>> buildAndCheck = sample.getBuildAndCheckTree();
		String[][] ac = sample.getAdditionalContext();
		List<Event> events = generateEvents(words,posTree, chunkTree, buildAndCheck,actions,ac);
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
	private List<Event> generateEvents(List<String> words, List<TreeNode> posTree, List<TreeNode> chunkTree,
			List<List<TreeNode>> buildAndCheck, List<String> actions, String[][] ac) {
		List<Event> events = new ArrayList<Event>(actions.size());
		//pos
		for (int i = 0; i < words.size(); i++) {		
			String[] context = null;
            events.add(new Event(actions.get(i), context));
		}
		//chunk
		for (int i = words.size(); i < 2*words.size(); i++) {		
			String[] context = null;
            events.add(new Event(actions.get(i), context));
		}
		//buildAndCheck
		for (int i = 2*words.size(); i < actions.size(); i=i+2) {
			String[] buildContext = null;
            events.add(new Event(actions.get(i), buildContext));
            String[] checkContext = null;
            events.add(new Event(actions.get(i+1), checkContext));
		}
		return events;
	}

}
