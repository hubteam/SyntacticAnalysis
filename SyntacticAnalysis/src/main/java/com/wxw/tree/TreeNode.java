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
	
	public String toBracket() {
		if(this.children.size() == 0){
			return " "+this.nodename;
		}else{
			String treestr = "";
			treestr = "("+this.nodename;
			for (TreeNode node:this.children) {
				treestr += node.toBracket();
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
	
	/**
	 * 打印没有换行的一整行括号表达式
	 * @return
	 */
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
	
	/**
	 * 输出有缩进和换行的括号表达式
	 * @param level 缩进的空格数
	 */
	public static String printTree(TreeNode tree, int level){
		
		if(tree.getChildren().size() == 1 && tree.getChildren().get(0).getChildren().size() == 0){
			return "("+tree.getNodeName()+" "+tree.getChildren().get(0).getNodeName()+")";
		}else if(tree.getChildren().size() == 1 && tree.getChildren().get(0).getChildren().size() == 1 && tree.getChildren().get(0).getChildren().get(0).getChildren().size() == 0){
			return "("+tree.getNodeName()+" "+"("+tree.getChildren().get(0).getNodeName()+" "+tree.getChildren().get(0).getChildren().get(0).getNodeName()+")"+")";
		}else if(tree.getChildren().size() > 1 && childrenHasOne(tree)){
			String str = "";
			str += "("+tree.getNodeName();
			str += " "+"("+tree.getChildren().get(0).getNodeName()+" "+tree.getChildren().get(0).getChildren().get(0).getNodeName()+")"+"\n";
			String s = "";
			for (int i = 1; i < tree.getChildren().size(); i++) {
//				s+="\n";
				for (int j = 0; j < level; j++) {
					s += "	";
				}
				s += printTree(tree.getChildren().get(i),level+1);
				if(i == tree.getChildren().size()-1){
					s += ")";
				}else{
					s += "\n";
				}
			}
			return str + s;
		}
		else if(tree.getChildren().size() > 1  && childrenOnlyOne(tree)){
			String str = "";
			str += "("+tree.getNodeName();
			for (int i = 0; i < tree.getChildren().size(); i++) {
				if(tree.getChildren().get(i).getChildren().size() == 1 && tree.getChildren().get(0).getChildren().get(0).getChildren().size() == 0){
					if(i == tree.getChildren().size()-1){
						str += " "+"("+tree.getChildren().get(i).getNodeName()+" "+tree.getChildren().get(i).getChildren().get(0).getNodeName()+")"+")";
						return str;
					}else{
						str += " "+"("+tree.getChildren().get(i).getNodeName()+" "+tree.getChildren().get(i).getChildren().get(0).getNodeName()+")";
					}
				}
			}
			return str;
		}else{
			String treeStr = "";
			treeStr = "("+tree.getNodeName();
			treeStr += "\n";
			for (int i = 0; i < tree.getChildren().size(); i++) {
				for (int j = 0; j < level; j++) {
					treeStr += "	";
				}
				treeStr += printTree(tree.getChildren().get(i),level+1);
				if(i == tree.getChildren().size()-1){
					treeStr += ")";
				}else{
					treeStr += "\n";
				}
			}
			return treeStr;
		}
		
	}
	
	/**
	 * 判断节点下，是否所有的节点都是子节点都是叶子节点
	 * @param tree
	 * @return
	 */
	private static boolean childrenOnlyOne(TreeNode tree){
		boolean flag = false;
		for (int i = 0; i < tree.getChildren().size(); i++) {
			if(tree.getChildren().get(i).getChildren().size() == 1 && tree.getChildren().get(0).getChildren().get(0).getChildren().size() == 0){
				flag = true;
			}else if(tree.getChildren().get(i).getChildren().size() > 1 || (tree.getChildren().get(1).getChildren().size() == 1) && tree.getChildren().get(1).getChildren().get(0).getChildren().size() > 0){
				flag = false;
				break;
			}
	   }
	   return flag;
    }
	
	public static boolean childrenHasOne(TreeNode tree){
		if(tree.getChildren().get(0).getChildren().size() == 1 && tree.getChildren().get(0).getChildren().get(0).getChildren().size() == 0){
			if(tree.getChildren().get(1).getChildren().size() > 1 || (tree.getChildren().get(1).getChildren().size() == 1) && tree.getChildren().get(1).getChildren().get(0).getChildren().size() > 0){
				return true;
			}
		}
		return false;
	}
}
