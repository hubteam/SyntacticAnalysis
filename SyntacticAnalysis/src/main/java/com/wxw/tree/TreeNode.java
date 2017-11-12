package com.wxw.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * 树结构
 * @author 王馨苇
 *
 */
public class TreeNode implements Cloneable{

	//节点名称
	private String nodename;
	//头节点
	private String headwords;
	//父节点
	private TreeNode parent;
	//子节点
	private List<TreeNode> children = new ArrayList<TreeNode>();
	//当前父节点下的第几颗子树
	private int index;
	//为预处理步骤所用，标记当前节点是否要用
	private boolean flag;
	
	public TreeNode(){
		
	}
	
	public TreeNode(String nodename){
		this.nodename = nodename;
	}
	
	public void setNewName(String name){
		this.nodename = name;
	}
	
	//设置头节点
	public void setHeadWords(String headwords){
		this.headwords = headwords;
	}
	
	//设置父节点
	public void setParent(TreeNode parent){
		this.parent = parent;
	}
	
	public void setIndex(int index){
		this.index = index;
	}
	
	public void setFlag(boolean flag){
		this.flag = flag;
	}
//	public void setParent(String parent){
//		this.parent = new TreeNode(parent);
//	}
	//添加子节点
	public void addChild(String children){
		this.children.add(new TreeNode(children));
	}
	public void addChild(TreeNode children){
		this.children.add(children);
	}
	//添加数个孩子
	public void addChild(TreeNode[] children){
		for (TreeNode treeNode : children) {
			this.addChild(treeNode);
		}
	}
	public void addChild(String[] children){
		for (String treeNode : children) {
			this.addChild(treeNode);
		}
	}
	
	//判断是否为叶子节点
	public boolean isLeaf(){
		return this.children.size() == 0;
	}
	
	//子节点的个数
	public int numChildren(){
		return this.children.size();
	}
	//节点名称
	public String getNodeName(){
		return this.nodename;
	}
	//头节点
	public String getHeadWords(){
		return this.headwords;
	}
	
	//返回父节点
	public TreeNode getParent(){
		return this.parent;
	}

	//返回子节点列表
	public List<TreeNode> getChildren(){
		return this.children;
	}
	
	public int getIndex(){
		return this.index;
	}
	
	@Override
	public String toString() {
		if(this.children.size() == 0){
			return " "+this.nodename;
		}else{
			String treestr = "";
			if(this.headwords != null){
				treestr = "("+this.nodename+"{"+this.headwords+"}";
			}else{
				treestr = "("+this.nodename;
			}
			
			for (TreeNode node:this.children) {
				treestr += node.toString();
			}
			treestr += ")";
			return treestr;
		}
	}

	@Override
	public boolean equals(Object obj) {

		TreeNode node = (TreeNode)obj;
		if(this.toString().equals(node.toString())){
			return true;
		}else{
			return false;
		}
	}
	
	public TreeNode clone() throws CloneNotSupportedException{
		return (TreeNode) super.clone();
	}
	
	public String toNewSample(){
		if(this.children.size() == 0 && this.flag == true){
			return " "+this.nodename;
		}else{
			String treestr = "";
			if(this.flag == true){
				treestr = "("+this.nodename;
			}	
			for (TreeNode node:this.children) {
				
				treestr += node.toNewSample();
			}
			if(this.flag == true){
				treestr += ")";
			}
			
			return treestr;
		}
	}
}
