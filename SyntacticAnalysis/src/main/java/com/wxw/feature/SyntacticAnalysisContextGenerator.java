package com.wxw.feature;

import java.util.List;

import com.wxw.tree.TreeNode;

/**
 * 特征生成接口
 * @author 王馨苇
 *
 */
public interface SyntacticAnalysisContextGenerator {
	
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
	 * check步的上下文特征
	 * @param index 当前位置
	 * @param buildAndCheckTree 子树序列
	 * @param actions 动作序列
	 * @param ac 
	 * @return
	 */
	public String[] getContextForCheck(int index,List<TreeNode> buildAndCheckTree, List<String> actions, Object[] ac);
	
	/**
	 * 为测试语料的chunk步骤生成上下文特征
	 * @param index 索引位置
	 * @param posTree 词性标注的子树，【区别于训练语料中的chunkTree，这里chunkTree还包含了动作序列作为根节点】
	 * @param actions 动作序列
	 * @param ac
	 * @return
	 */
	public String[] getContextForChunkForTest(int index, List<TreeNode> posTree, List<String> actions, Object[] ac);
	
	/**
	 * 为测试语料的build步的上下文特征
	 * @param index 当前位置
	 * @param chunkTree 子树序列
	 * @param actions 动作序列
	 * @param ac 
	 * @return
	 */
	public String[] getContextForBuildForTest(int index,List<TreeNode> chunkTree, List<String> actions, Object[] ac);
	
	/**
	 * 为测试语料的check步的上下文特征
	 * @param index 当前位置
	 * @param chunkTree 子树序列
	 * @param actions 动作序列
	 * @param out 当前输出的动作序列
	 * @param ac 
	 * @return
	 */
	public String[] getContextForCheckForTest(int index,List<TreeNode> chunkTree, List<String> actions, String out, Object[] ac);	
}
