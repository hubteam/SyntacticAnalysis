package com.wxw.model.bystep;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.HeadTreeNode;

import opennlp.tools.ml.model.Event;
import opennlp.tools.util.AbstractEventStream;
import opennlp.tools.util.ObjectStream;
/**
 * 为check模型生成事件
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisSampleEventForChunk extends AbstractEventStream<SyntacticAnalysisSample<HeadTreeNode>>{

	private SyntacticAnalysisContextGenerator<HeadTreeNode> generator;
	
	/**
	 * 构造
	 * @param samples 样本流
	 * @param generator 上下文产生器
	 */
	public SyntacticAnalysisSampleEventForChunk(ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> samples,SyntacticAnalysisContextGenerator<HeadTreeNode> generator) {
		super(samples);
		this.generator = generator;
	}

	/**
	 * 生成事件
	 */
	@Override
	protected Iterator<Event> createEvents(SyntacticAnalysisSample<HeadTreeNode> sample) {
		List<String> words = sample.getWords();
		List<String> actions = sample.getActions();
		List<HeadTreeNode> chunkTree = sample.getChunkTree();
		String[][] ac = sample.getAdditionalContext();
		List<Event> events = generateEvents(words, chunkTree, actions,ac);
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
	private List<Event> generateEvents( List<String> words, List<HeadTreeNode> chunkTree,
			List<String> actions, String[][] ac) {
		List<Event> events = new ArrayList<Event>(actions.size());		
		//chunk
		for (int i = words.size(); i < 2*words.size(); i++) {		
			String[] context = generator.getContextForChunk(i-words.size(),chunkTree, actions, ac);
            events.add(new Event(actions.get(i), context));
		}
		
		return events;
	}
}
