package com.wxw.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.wxw.tree.ActionsToTree;
import com.wxw.tree.HeadTreeNode;
import com.wxw.tree.TreeNode;

/**
 * 样本类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisSample<T extends TreeNode> {

	private List<String> words = new ArrayList<String>();
	private List<String> poses = new ArrayList<String>();
	private List<T> posTree;
	private List<T> chunkTree;
	private List<List<T>> buildAndCheckTree;
	private List<String> actions;
	private String[][] addtionalContext;
	
	public SyntacticAnalysisSample(List<T> posTree, List<T> chunkTree, List<List<T>> buildAndCheckTree, List<String> actions){
		this(posTree,chunkTree,buildAndCheckTree,actions,null);
	}
	
    public SyntacticAnalysisSample(List<T> posTree, List<T> chunkTree, List<List<T>> buildAndCheckTree, List<String> actions,String[][] additionalContext){   
    	posTreeToWordsAndPoses(posTree);
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
     * 将得到的词性标注子树转成字符和字符标记
     * @param posTree
     */
    public void posTreeToWordsAndPoses(List<T> posTree){
    	for (int i = 0; i < posTree.size(); i++) {
    		String word = posTree.get(i).getChildren().get(0).getNodeName();
    		poses.add(posTree.get(i).getNodeName());
			words.add(word);				
		}
    }
    
	/**
	 * 获取词语
	 * @return
	 */
	public List<String> getWords(){
		return this.words;
	}

	/**
	 * 获取词性
	 * @return
	 */
	public List<String> getPoses(){
		return this.poses;
	}
	
	/**
	 * pos操作得到的子树序列
	 * @return
	 */
	public List<T> getPosTree(){
		return this.posTree;
	}
	
	/**
	 * chunk操作得到的子树序列
	 * @return
	 */
	public List<T> getChunkTree(){
		return this.chunkTree;
	}
	
	/**
	 * buildAndCheck操作得到的子树序列
	 * @return
	 */
	public List<List<T>> getBuildAndCheckTree(){
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        } else if (obj instanceof SyntacticAnalysisSample) {
        	SyntacticAnalysisSample<T> a = (SyntacticAnalysisSample<T>) obj;
            return Arrays.equals(getActions().stream().toArray(), a.getActions().stream().toArray());
        } else {
            return false;
        }
	}
	
	/**
	 * 转成样本类
	 * @param words 词语序列
	 * @param actions 动作序列
	 * @return
	 */
	public static TreeNode toSample(List<String> words ,List<String> actions){
		ActionsToTree att = new ActionsToTree();
		TreeNode tree = att.actionsToTree(words, actions);
		return tree;
	}
}
