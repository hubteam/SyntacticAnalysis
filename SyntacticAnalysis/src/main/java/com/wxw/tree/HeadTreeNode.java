package com.wxw.tree;

import java.util.ArrayList;
import java.util.List;

public class HeadTreeNode extends TreeNode{

	private String headWords;
	private String headWordsPos;

	public HeadTreeNode(String nodename){
		super(nodename);
	}

	public void setHeadWords(String headWords){
		this.headWords = headWords;
	}
	
	public String getHeadWords(){
		return this.headWords;
	}

	public void setHeadWordsPos(String headWordsPos){
		this.headWordsPos = headWordsPos;
	}
	
	public String getHeadWordsPos(){
		return this.headWordsPos;
	}
	
	//返回父节点
	public HeadTreeNode getParent(){
		return (HeadTreeNode) parent;
	}

	//返回子节点列表
	public List<HeadTreeNode> getChildren(){
		List<HeadTreeNode> hnode = new ArrayList<>();
		for (TreeNode treeNode : children) {
			HeadTreeNode node = (HeadTreeNode) treeNode;
			hnode.add(node);
		}
		return hnode;
	}
	
	/**
	 * 带有头结点的树的输出（一行括号表达式）
	 */
	@Override
	public String toString() {
		if(super.children.size() == 0){
			return " "+this.nodename+"["+this.getWordIndex()+"]";
		}else{
			String treestr = "";
			treestr = "("+this.nodename+"{"+this.headWords+"["+this.headWordsPos+"]}";
			
			for (HeadTreeNode node:getChildren()) {
				treestr += node.toString();
			}
			treestr += ")";
			return treestr;
		}
	}
	
	/**
	 * 没有头结点的括号表达式
	 * @return
	 */
	public String toBracket(){
		if(this.children.size() == 0){
			return " "+this.nodename;
		}else{
			String treestr = "";
			treestr = "("+this.nodename;
			for (TreeNode node:this.children) {
				treestr += node.toString();
			}
			treestr += ")";
			return treestr;
		}
	}

	@Override
	public boolean equals(Object obj) {
		HeadTreeNode node = (HeadTreeNode)obj;
		if(this.toString().equals(node.toString())){
			return true;
		}else{
			return false;
		}
	}
}
