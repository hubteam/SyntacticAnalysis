package com.wxw.feature;

import java.util.HashMap;
import java.util.List;

import com.wxw.tree.TreeNode;

/**
 * 特征生成接口
 * @author 王馨苇
 *
 */
public interface SyntacticAnalysisContextGenerator {
	
	/**
	 * 生成词性标注的上下文特征
	 * @param index 当前位置
	 * @param words 词语
	 * @param poses 词性
	 * @param ac 
	 * @return
	 */
	public String[] getContextForPos(int index, List<String> words, List<String> poses, Object[] ac);
	
	/**
	 * chunk步的上下文特征
	 * @param index 当前位置
	 * @param chunkTree 子树序列
	 * @param actions 动作序列
	 * @param ac 
	 * @return
	 */
	public String[] getContextForChunk(int index,List<TreeNode> chunkTree,List<String> actions, Object[] ac);
	
	/**
	 * build步的上下文特征
	 * @param index 当前位置
	 * @param buildAndCheckTree 子树序列
	 * @param actions 动作序列
	 * @param ac 
	 * @return
	 */
	public String[] getContextForBuild(int index,List<TreeNode> buildAndCheckTree, List<String> actions, Object[] ac);
	
	/**
	 * build步的上下文特征
	 * @param index 当前位置
	 * @param buildAndCheckTree 子树序列
	 * @param actions 动作序列
	 * @param ac 
	 * @return
	 */
	public String[] getContextForCheck(int index,List<TreeNode> buildAndCheckTree, List<String> actions, Object[] ac);
	
}
