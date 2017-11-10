package com.wxw.sequence;

import java.util.List;

import com.wxw.tree.TreeNode;

/**
 * 序列校验接口
 * @author 王馨苇
 *
 */
public interface SyntacticAnalysisSequenceValidator {

	/**
	 * 
	 * @param i 当前位置
	 * @param character 字序列
	 * @param tags 字的标记序列
	 * @param words 词语序列
	 * @param chunkTree chunk步得到的树
	 * @param buildAndCheckTree buildAndCheck步得到的树
	 * @param out 当前位置的结果
	 * @return
	 */
	boolean validSequence(int i, List<String> words, List<String> poses, List<String> tags, List<TreeNode> chunkTree,List<List<TreeNode>> buildAndCheckTree,String out);
}
