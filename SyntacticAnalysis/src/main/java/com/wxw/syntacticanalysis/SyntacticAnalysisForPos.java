package com.wxw.syntacticanalysis;

import java.util.List;

import com.wxw.tree.TreeNode;

/**
 * 词性标注器
 * @author 王馨苇
 *
 */
public interface SyntacticAnalysisForPos<T extends TreeNode> {

	/**
	 * 得到词性标注的结果
	 * @param words 分词数组
	 * @return
	 */
	String[] pos(String[] words);
	/**
	 * 得到词性标注结果
	 * @param sentence 分词的句子
	 * @return
	 */
	String[] pos(String sentence);
	
	/**
	 * 得到词性标注子树序列
	 * @param words 分词数组
	 * @return
	 */
	List<T> posTree(String[] words);
	/**
	 * 得到词性标注子树序列
	 * @param sentece 分词句子
	 * @return
	 */
	List<T> posTree(String sentece);
}
