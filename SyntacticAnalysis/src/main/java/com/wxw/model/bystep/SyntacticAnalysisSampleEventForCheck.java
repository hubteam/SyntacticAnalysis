package com.wxw.model.bystep;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.TreeNode;

import opennlp.tools.ml.model.Event;
import opennlp.tools.util.AbstractEventStream;
import opennlp.tools.util.ObjectStream;
/**
 * 为check模型生成事件
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisSampleEventForCheck extends AbstractEventStream<SyntacticAnalysisSample>{

	private SyntacticAnalysisContextGenerator generator;
	
	/**
	 * 构造
	 * @param samples 样本流
	 * @param generator 上下文产生器
	 */
	public SyntacticAnalysisSampleEventForCheck(ObjectStream<SyntacticAnalysisSample> samples,SyntacticAnalysisContextGenerator generator) {
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
		List<Event> events = generateEvents(words, buildAndCheckTree,actions,ac);
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
	private List<Event> generateEvents( List<String> words, 
			List<List<TreeNode>> buildAndCheckTree, List<String> actions, String[][] ac) {
		List<Event> events = new ArrayList<Event>(actions.size());		
		//buildAndCheck
		//两个变量i j
		//i控制第几个list
		//j控制list中的第几个
		int j = 0;
		for (int i = 2*words.size(); i < actions.size(); i=i+2) {    
            if(actions.get(i+1).equals("yes")){
            	String[] checkContext = generator.getContextForCheck(j,buildAndCheckTree.get(i+1-2*words.size()), actions, ac);
            	int record = j-1;
		        for (int k = record; k >= 0; k--) {
		        	if(buildAndCheckTree.get(i-2*words.size()).get(k).getNodeName().split("_")[0].equals("start")){
		        		j = k;
		        		break;
		        	}
				}
                events.add(new Event(actions.get(i+1), checkContext));
            }else if(actions.get(i+1).equals("no")){            	
            	String[] checkContext = generator.getContextForCheck(j,buildAndCheckTree.get(i+1-2*words.size()), actions, ac);
                events.add(new Event(actions.get(i+1), checkContext));
            	j++;
            }  
		}
		return events;
	}
}
