package com.wxw.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.wxw.tree.TreeNode;

/**
 * 样本类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisSample {

	private List<String> words = new ArrayList<String>();
	private List<String> poses = new ArrayList<String>();
	private List<TreeNode> posTree;
	private List<TreeNode> chunkTree;
	private List<List<TreeNode>> buildAndCheckTree;
	private List<String> actions;
	private String[][] addtionalContext;
	
	public SyntacticAnalysisSample(List<TreeNode> posTree, List<TreeNode> chunkTree, List<List<TreeNode>> buildAndCheckTree, List<String> actions){
		this(posTree,chunkTree,buildAndCheckTree,actions,null);
	}
	
    public SyntacticAnalysisSample(List<TreeNode> posTree, List<TreeNode> chunkTree, List<List<TreeNode>> buildAndCheckTree, List<String> actions,String[][] additionalContext){   
    	wordsToCharacters(posTree);
    	this.posTree = Collections.unmodifiableList(posTree);
        this.chunkTree = Collections.unmodifiableList(chunkTree);
        this.buildAndCheckTree = Collections.unmodifiableList(buildAndCheckTree);
        this.actions = Collections.unmodifiableList(actions);

//        for (int i = 0; i < actions.size(); i++) {
//			System.out.print(actions.get(i)+" ");
//		}
//        System.out.println();
        
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
    public void wordsToCharacters(List<TreeNode> posTree){
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        } else if (obj instanceof SyntacticAnalysisSample) {
        	SyntacticAnalysisSample a = (SyntacticAnalysisSample) obj;
            return Arrays.equals(getActions().stream().toArray(), a.getActions().stream().toArray());
        } else {
            return false;
        }
	}
	
	/**
	 * 将词性标注和词语转成树的形式
	 * @param words k个最好的词语序列
	 * @param poses k个最好的词性标注序列
	 * @return
	 */
	public static List<List<TreeNode>> toPosTree(String[] words, String[][] poses){
		List<List<TreeNode>> posTrees = new ArrayList<>();
		for (int i = 0; i < poses.length; i++) {
			List<TreeNode> posTree = new ArrayList<TreeNode>();
			for (int j = 0; j < poses[i].length && j < words.length; j++) {
				TreeNode pos = new TreeNode(poses[i][j]);
				TreeNode word = new TreeNode(words[j]);
				pos.addChild(word);
				word.setParent(pos);
				pos.setFlag(true);
				word.setFlag(true);
				pos.setHeadWords(words[j]);
				posTree.add(pos);
			}
			posTrees.add(posTree);
		}
		return posTrees;
	}
}