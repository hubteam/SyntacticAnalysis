package com.wxw.posModelUnused;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.HeadTreeNode;

import opennlp.tools.ml.model.Event;
import opennlp.tools.util.AbstractEventStream;
import opennlp.tools.util.ObjectStream;

/**
 * 为英文词性标注生成事件
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisSampleEventContainsPos extends AbstractEventStream<SyntacticAnalysisSample<HeadTreeNode>>{

	private SyntacticAnalysisContextGeneratorContainsPos generator;
	
	/**
	 * 构造
	 * @param samples 样本流
	 * @param generator 上下文产生器
	 */
	public SyntacticAnalysisSampleEventContainsPos(ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> samples,SyntacticAnalysisContextGeneratorContainsPos generator) {
		super(samples);
		this.generator = generator;
	}

	/**
	 * 生成事件
	 */
	@Override
	protected Iterator<Event> createEvents(SyntacticAnalysisSample<HeadTreeNode> sample) {
		List<String> words = sample.getWords();
		List<String> poses = sample.getPoses();
		String[][] ac = sample.getAdditionalContext();
		List<Event> events = generateEvents(words, poses, ac);
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
	private List<Event> generateEvents( List<String> words, List<String> poses, String[][] ac) {
		List<Event> events = new ArrayList<Event>(words.size());
		String[] word = words.toArray(new String[words.size()]);
		String[] pos = poses.toArray(new String[poses.size()]);
		for (int i = 0; i < words.size(); i++) {
			String[] context = generator.getContext(i,word, pos, ac);
			events.add(new Event(poses.get(i), context));
		}
		return events;
	}

}

