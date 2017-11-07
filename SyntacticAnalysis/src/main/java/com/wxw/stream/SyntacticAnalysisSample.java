package com.wxw.stream;

import java.util.Collections;
import java.util.List;

import com.wxw.tree.TreeNode;

/**
 * 样本类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisSample {

	private List<String> words;
	private List<TreeNode> posTree;
	private List<TreeNode> chunkTree;
	private List<List<TreeNode>> buildAndCheckTree;
	private List<String> actions;
	private String[][] addtionalContext;
	
	public SyntacticAnalysisSample(List<String> words, List<TreeNode> posTree, List<TreeNode> chunkTree, List<List<TreeNode>> buildAndCheckTree, List<String> actions){
		this(words,posTree,chunkTree,buildAndCheckTree,actions,null);
	}
	
    public SyntacticAnalysisSample(List<String> words, List<TreeNode> posTree, List<TreeNode> chunkTree, List<List<TreeNode>> buildAndCheckTree, List<String> actions,String[][] additionalContext){
        this.words = Collections.unmodifiableList(words);     
        this.posTree = Collections.unmodifiableList(posTree);
        this.chunkTree = Collections.unmodifiableList(chunkTree);
        this.buildAndCheckTree = Collections.unmodifiableList(buildAndCheckTree);
        this.actions = Collections.unmodifiableList(actions);

        String[][] ac;
        if (additionalContext != null) {
            ac = new String[additionalContext.length][];

            for (int i = 0; i < additionalContext.length; i++) {
                ac[i] = new String[additionalContext[i].length];
                System.arraycopy(additionalContext[i], 0, ac[i], 0,
                        additionalContext[i].length);
            }
        } else {
            ac = null;
        }
        this.addtionalContext = ac;
	}
	
	/**
	 * 获取词语
	 * @return
	 */
	public List<String> getWords(){
		return this.words;
	}

	/**
	 * pos操作得到的子树序列
	 * @return
	 */
	public List<TreeNode> getPosTree(){
		return this.posTree;
	}
	
	/**
	 * chunk操作得到的子树序列
	 * @return
	 */
	public List<TreeNode> getChunkTree(){
		return this.chunkTree;
	}
	
	/**
	 * buildAndCheck操作得到的子树序列
	 * @return
	 */
	public List<List<TreeNode>> getBuildAndCheckTree(){
		return this.buildAndCheckTree;
	}
	
	/**
	 * 动作序列
	 * @return
	 */
	public List<String> getActions(){
		return this.actions;
	}
	
	/**
	 * 获取额外的上下文信息
	 * @return
	 */
	public String[][] getAdditionalContext(){
		return this.addtionalContext;
	}
}
