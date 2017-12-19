package com.wxw.constituent;

import com.wxw.tree.TreeNode;

/**
 * 成分树
 * @author 王馨苇
 *
 */
public class ConstituentTree<T extends TreeNode> {

	private T treeNode;
	
	public void setTreeNode(T treeNode){
		this.treeNode = treeNode;
	}
	
	public T getTreeNode(){
		return this.treeNode;
	}
}
