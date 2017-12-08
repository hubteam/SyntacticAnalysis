package com.wxw.syntacticanalysis;

import java.util.List;

import com.wxw.tree.TreeNode;

/**
 * 句法分析器
 * @author 王馨苇
 *
 */
public interface SyntacticAnalysis {

	/**
	 * 得到句法树
	 * @param chunkTree chunk子树序列
	 * @return
	 */
	TreeNode syntacticTree(List<TreeNode> chunkTree);
	/**
	 * 得到句法树
	 * @param words 词语
	 * @param poses 词性标记
	 * @param chunkTag chunk标记
	 * @return
	 */
	TreeNode syntacticTree(String[] words,String[] poses,String[] chunkTag);
	/**
	 * 得到句法树
	 * @param sentence 由词语词性标记和chunk标记组成的句子
	 * @return
	 */
	TreeNode syntacticTree(String sentence);
	/**
	 * 得到句法树的括号表达式
	 * @param chunkTree chunk子树序列
	 * @return
	 */
	String syntacticBracket(List<TreeNode> chunkTree);
	/**
	 * 得到句法树的括号表达式
	 * @param words 词语
	 * @param poses 词性标记
	 * @param chunkTag chunk标记
	 * @return
	 */
	String syntacticBracket(String[] words,String[] poses,String[] chunkTag);
	/**
	 * 得到句法树的括号表达式
	 * @param sentence 由词语词性标记和chunk标记组成的句子
	 * @return
	 */
	String syntacticBracket(String sentence);
}
