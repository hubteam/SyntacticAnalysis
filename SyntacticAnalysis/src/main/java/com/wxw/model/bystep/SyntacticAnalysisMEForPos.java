package com.wxw.model.bystep;

import java.util.List;

import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.syntacticanalysis.SyntacticAnalysisForPos;
import com.wxw.tree.TreeNode;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
/**
 * 实现词性标注器的功能
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisMEForPos extends POSTaggerME implements SyntacticAnalysisForPos{

	public SyntacticAnalysisMEForPos(POSModel model) {
		super(model);
	}

	/**
	 * 得到最好的词性标注子树序列
	 * @param words 词语序列
	 * @return
	 */
	public List<TreeNode> tagPos(String[] words){
		return tagKpos(1,words).get(0);
	}
	/**
	 * 得到最好的K个词性标注子树序列
	 * @param k 个数
	 * @param words 词语
	 * @return
	 */
	public List<List<TreeNode>> tagKpos(int k, String[] words){
		String[][] poses = super.tag(k, words);
		return SyntacticAnalysisSample.toPosTree(words, poses);
	}
	
	/**
	 * 得到词性标注的结果
	 * @param words 分词数组
	 * @return
	 */
	@Override
	public String[] pos(String[] words) {
		String[] poses = super.tag(words);
		String[] output = null;
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
	public List<TreeNode> posTree(String[] words) {
		
		return tagPos(words);
	}

	/**
	 * 得到词性标注子树序列
	 * @param sentece 分词句子
	 * @return
	 */
	@Override
	public List<TreeNode> posTree(String sentece) {
		String[] words = WhitespaceTokenizer.INSTANCE.tokenize(sentece);
		return posTree(words);
	}
}
