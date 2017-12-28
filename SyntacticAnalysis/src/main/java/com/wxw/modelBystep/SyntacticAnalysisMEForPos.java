package com.wxw.modelBystep;

import java.util.ArrayList;
import java.util.List;

import com.wxw.syntacticanalysis.SyntacticAnalysisForPos;
import com.wxw.tree.HeadTreeNode;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
/**
 * 实现词性标注器的功能
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisMEForPos extends POSTaggerME implements SyntacticAnalysisForPos<HeadTreeNode>{
	
	public SyntacticAnalysisMEForPos(POSModel model) {
		super(model);
	}

	/**
	 * 得到最好的词性标注子树序列
	 * @param words 词语序列
	 * @return
	 */
	public List<HeadTreeNode> tagPos(String[] words){
		return tagKpos(1,words).get(0);
	}
	/**
	 * 得到最好的K个词性标注子树序列
	 * @param k 个数
	 * @param words 词语
	 * @return
	 */
	public List<List<HeadTreeNode>> tagKpos(int k, String[] words){
		String[][] poses = super.tag(k, words);
		return toPosTree(words, poses);
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
	
	/**
	 * 得到词性标注的结果
	 * @param words 分词数组
	 * @return
	 */
	@Override
	public String[] pos(String[] words) {
		String[] poses = super.tag(words);
		String[] output = new String[poses.length];
		for (int i = 0; i < poses.length && i < words.length; i++) {
			output[i] = words[i]+"/"+poses[i];
		}
		return output;
	}

	/**
	 * 得到词性标注结果
	 * @param sentence 分词的句子
	 * @return
	 */
	@Override
	public String[] pos(String sentence) {
		String[] words = WhitespaceTokenizer.INSTANCE.tokenize(sentence);
		return pos(words);
	}

	/**
	 * 得到词性标注子树序列
	 * @param words 分词数组
	 * @return
	 */
	@Override
	public List<HeadTreeNode> posTree(String[] words) {
		
		return tagPos(words);
	}

	/**
	 * 得到词性标注子树序列
	 * @param sentece 分词句子
	 * @return
	 */
	@Override
	public List<HeadTreeNode> posTree(String sentece) {
		String[] words = WhitespaceTokenizer.INSTANCE.tokenize(sentece);
		return posTree(words);
	}
}
