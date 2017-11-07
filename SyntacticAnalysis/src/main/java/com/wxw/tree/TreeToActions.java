package com.wxw.tree;

import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 根据句法树得到动作序列
 * @author 王馨苇
 *
 */
public class TreeToActions {

	//句子的词语序列
	private List<String> words = new ArrayList<String>();
	//动作序列
	private List<String> actions = new ArrayList<String>();
	//第一步POS后得到的n颗子树
	private List<TreeNode> pos = new ArrayList<TreeNode>();
	//记录第二部CHUNK后得到的n棵子树
	private List<TreeNode> chunk = new ArrayList<TreeNode>();
	//第三部得到的列表
	private List<List<TreeNode>> buildAndCheckTree = new ArrayList<List<TreeNode>>();
		
	/**
	 * 第一步POS
	 * @param tree 一棵树
	 */
	public void getActionPOS(TreeNode tree){

		//如果是叶子节点，肯定是具体的词，父节点是词性
		if(tree.getChildren().size() == 0){
			pos.add(tree.getParent());
			actions.add(tree.getParent().getNodeName());
			words.add(tree.getNodeName());
		}else{
			//不是叶子节点的时候，递归
			for (TreeNode node:tree.getChildren()) {
				getActionPOS(node);
			}
		}
	}
	
	/**
	 * 第二部CHUNK
	 * @param tree 一颗完整的句法树
	 * @param subTree 第一步POS后得到的若干子树
	 * @throws CloneNotSupportedException 
	 */
	public void getActionCHUNK(TreeNode tree,List<TreeNode> subTree) throws CloneNotSupportedException{
		//为了防止原来的tree被修改
		TreeNode treeCopy = tree.clone();
		//如果当前节点只有一颗子树，这子树可能就是具体的词了，但也存在特殊：（NP(NN chairman)）
		//subTree.contains(tree):因为第二部在第一步的基础上产生，如果当前子树和第一步得到的结果匹配，去除了（NP(NN chairman)）这样的情况
		//这样得到的子树为1的都是具体的词性和词语组成的子树
		if(treeCopy.getChildren().size() == 1 && treeCopy.getChildren().get(0).getChildren().size() == 0){	
			//当前节点的父节点只有这一颗子树，也就是（NP(NN chairman)）这种情况
			if(treeCopy.getParent().getChildren().size() == 1){	
				//用start标记作为当前节点的父节点
				actions.add("start_"+treeCopy.getParent().getNodeName());
				TreeNode node = new TreeNode("start_"+treeCopy.getParent().getNodeName());
				node.addChild(treeCopy);
				node.setHeadWords(treeCopy.getParent().getHeadWords());
				treeCopy.setParent(node);
				chunk.add(node);
				//当前节点的父节点不止一个，就遍历所有的子树，判断当前节点是否为flat结构
			}else if(treeCopy.getParent().getChildren().size() > 1){
				int record = -1;
				for (int j = 0; j < treeCopy.getParent().getChildren().size(); j++) {
					//如果有一颗子树破坏了flat结构，退出
					if(treeCopy.getParent().getChildren().get(j).getChildren().size() > 1){
						record = j;
						break;
					//(PP-CLR(TO to)(NP(PRP it)))针对这种结构
					}else if(treeCopy.getParent().getChildren().get(j).getChildren().size() == 1
							&& treeCopy.getParent().getChildren().get(j).getChildren().get(0).getChildren().size() != 0){
						record = j;
						break;
					}
				}
				//当前节点的父节点的所有子树满足flat结构
				if(record == -1){
					//当前节点是是第一颗子树，
					if(treeCopy.getParent().getChildren().get(0).equals(treeCopy)){
						actions.add("start_"+treeCopy.getParent().getNodeName());
						TreeNode node = new TreeNode("start_"+treeCopy.getParent().getNodeName());
						node.addChild(treeCopy);
						node.setHeadWords(treeCopy.getParent().getHeadWords());
						treeCopy.setParent(node);
						chunk.add(node);
					}else{
						//不是第一个
						actions.add("join_"+treeCopy.getParent().getNodeName());
						TreeNode node = new TreeNode("join_"+treeCopy.getParent().getNodeName());
						node.addChild(treeCopy);
						node.setHeadWords(treeCopy.getParent().getHeadWords());
						treeCopy.setParent(node);
						chunk.add(node);
					}
				//当前节点的父节点的子树不满足flat结构	，用other标记
				}else{
					actions.add("other");
					TreeNode node = new TreeNode("other");
					node.addChild(treeCopy);
					node.setHeadWords(treeCopy.getParent().getHeadWords());
					treeCopy.setParent(node);
					chunk.add(node);
				}		
			}
		}else{
			//当前节点不满足上述条件，递归
			for (TreeNode node:treeCopy.getChildren()) {
				getActionCHUNK(node,subTree);
			}
		}
	}
	
	/**
	 * 第二部得到的CHUNK进行合并，就是合并start和join部分
	 * @param subTree 第二部CHUNK得到的若干棵子树
	 * @return
	 */
	public List<TreeNode> combine(List<TreeNode> subTree){
		List<TreeNode> combineChunk = new ArrayList<TreeNode>();
		//遍历所有子树
		for (int i = 0; i < subTree.size(); i++) {
			//当前子树的根节点是start标记的
			if(subTree.get(i).getNodeName().split("_")[0].equals("start")){
				//只要是start标记的就去掉root中的start，生成一颗新的子树，
				//因为有些结构，如（NP(NN chairman)），只有start没有join部分，
				//所以遇到start就生成新的子树
				TreeNode node = new TreeNode(subTree.get(i).getNodeName().split("_")[1]);
				node.addChild(subTree.get(i).getChildren().get(0));
				node.setHeadWords(subTree.get(i).getHeadWords());
				subTree.get(i).getChildren().get(0).setParent(node);
				for (int j = i+1; j < subTree.size(); j++) {
					//判断start后是否有join如果有，就和之前的start合并
					if(subTree.get(j).getNodeName().split("_")[0].equals("join")){
						node.addChild(subTree.get(j).getChildren().get(0));
						node.setHeadWords(subTree.get(j).getHeadWords());
						subTree.get(j).getChildren().get(0).setParent(node);
					}else if(subTree.get(j).getNodeName().split("_")[0].equals("start") ||
							subTree.get(j).getNodeName().split("_")[0].equals("other")){
						break;
					}
				}
				//将一颗合并过的完整子树加入列表
				combineChunk.add(node);
				//标记为other的，去掉other
			}else if(subTree.get(i).getNodeName().equals("other")){
				subTree.get(i).getChildren().get(0).setParent(null);
				subTree.get(i).getChildren().get(0).setHeadWords(subTree.get(i).getChildren().get(0).getHeadWords());
				combineChunk.add(subTree.get(i).getChildren().get(0));
			}
		}
		return combineChunk;
	}

	int i = 0;//List<TreeNode> subTree中的index
	/**
	 * 第三步：build和check
	 * @param tree 一棵完整的句法树
	 * @param subTree 第二步CHUNK得到的若干颗子树进行合并之后的若干颗子树
	 */
	public void getActionBUILDandCHECK(TreeNode tree,List<TreeNode> subTree){
		
		//这里的subTree用于判断，定义一个subTree的副本用于过程中的改变
		//这里的TreeNode实现了克隆的接口，这里也就是深拷贝
		List<TreeNode> subTreeCopy;
		//如果当前的节点子树是第二步CHUNK后合并后的一个结果
		if(subTree.get(i).equals(tree)){	
			
			if(tree.getParent().getChildren().size() == 1){
				//添加start标记
				actions.add("start_"+tree.getParent().getNodeName());
				//改变subTreeCopy
				TreeNode node = new TreeNode("start_"+tree.getParent().getNodeName());
				node.addChild(subTree.get(i));
				node.setHeadWords(subTree.get(i).getHeadWords());
				subTree.get(i).setParent(node);
				subTree.set(i, node);
				subTreeCopy = new ArrayList<TreeNode>(subTree);
				buildAndCheckTree.add(subTreeCopy);				
				actions.add("yes");
				//合并
				TreeNode tempnode = new TreeNode(tree.getParent().getNodeName());
				tempnode.setParent(tree.getParent().getParent());
				tempnode.setHeadWords(tree.getParent().getHeadWords());
				tempnode.addChild(tree.getParent().getChildren().get(0));
				tree.getParent().getChildren().get(0).setParent(tempnode);
				subTree.set(i, tree.getParent());
				subTreeCopy = new ArrayList<TreeNode>(subTree);
				buildAndCheckTree.add(subTreeCopy);				
				//合并之后，以合并后的节点的父节点继续递归
				if(tree.getParent().getParent() == null){
					return;
				}else{
					getActionBUILDandCHECK(tree.getParent().getParent(),subTree);
				}
			}else if(tree.getParent().getChildren().size() > 1){
				//if(tree.getParent().getChildren().get(0).equals(tree)){
				if(tree.getIndex() == 0){
					//添加start标记
					actions.add("start_"+tree.getParent().getNodeName());	
					TreeNode node = new TreeNode("start_"+tree.getParent().getNodeName());
					node.addChild(subTree.get(i));
					node.setHeadWords(tree.getParent().getHeadWords());
					subTree.get(i).setParent(node);
					subTree.set(i, node);
					subTreeCopy = new ArrayList<TreeNode>(subTree);
					buildAndCheckTree.add(subTreeCopy);					
					actions.add("no");
					subTreeCopy = new ArrayList<TreeNode>(subTree);
					//为no的时候没有合并的操作，其实是不变的
					buildAndCheckTree.add(subTreeCopy);
					i++;
					if(i >= subTree.size()){
						return;
					}
				//}else if(tree.getParent().getChildren().get(tree.getParent().getChildren().size()-1).equals(tree)){
				}else if(tree.getIndex() == tree.getParent().getChildren().size()-1){
//					System.out.println(tree.getParent().getChildren().size());
					actions.add("join_"+tree.getParent().getNodeName());
					TreeNode tempnode = new TreeNode("join_"+tree.getParent().getNodeName());
					tempnode.addChild(subTree.get(i));
					tempnode.setHeadWords(tree.getParent().getHeadWords());
					subTree.get(i).setParent(tempnode);
					subTree.set(i, tempnode);
					subTreeCopy = new ArrayList<TreeNode>(subTree);
					buildAndCheckTree.add(subTreeCopy);					
					actions.add("yes");
					//需要合并,node为合并后的父节点
					TreeNode node = new TreeNode(tree.getParent().getNodeName());
					node.setParent(tree.getParent().getParent());
					node.setHeadWords(tree.getParent().getHeadWords());
					for (int j = 0; j < tree.getParent().getChildren().size(); j++) {								
						node.addChild(tree.getParent().getChildren().get(j));
						tree.getParent().getChildren().get(j).setParent(node);						
					}
//					System.out.println(tree.getParent().getChildren().size());
					//对subTreeCopy更改
					//要更改的位置
					int index = i - tree.getParent().getChildren().size() + 1;
					subTree.set(index,node);
					//删除那些用于合并的join
					for (int k = i; k >= index+1; k--) {
						subTree.remove(index+1);
					}
//					for (int j = index+1; j <= i; j++) {
//						subTree.remove(j);
//					}
					subTreeCopy = new ArrayList<TreeNode>(subTree);
					buildAndCheckTree.add(subTreeCopy);
					//更改i为了下一次
					i = index;
					//合并之后，以合并后的节点的父节点继续递归，直到没有父节点，退出递归
					if(node.getParent() == null){
						return;
					}else{
						getActionBUILDandCHECK(node.getParent(),subTree);
					}
				}else{
					actions.add("join_"+tree.getParent().getNodeName());
					TreeNode node = new TreeNode("join_"+tree.getParent().getNodeName());
					node.addChild(subTree.get(i));
					node.setHeadWords(tree.getParent().getHeadWords());
					subTree.get(i).setParent(node);
					subTree.set(i, node);
					subTreeCopy = new ArrayList<TreeNode>(subTree);
					buildAndCheckTree.add(subTreeCopy);					
					actions.add("no");
					subTreeCopy = new ArrayList<TreeNode>(subTree);
					buildAndCheckTree.add(subTreeCopy);
					i++;
					if(i >= subTree.size()){
						return;
					}
				}
			}

		}else{
		
			for (TreeNode node:tree.getChildren()) {
				getActionBUILDandCHECK(node,subTree);
			}
		}
//		for (List<TreeNode> treeNode : buildAndCheckTree) {
//		for (TreeNode treeNode2 : treeNode) {
//			System.out.println(treeNode2);
//		}
//		System.out.println();
//	}
	}
	
	/**
	 * 由树生成动作
	 * @param tree 树
	 * @throws CloneNotSupportedException
	 */
	public void treeToAction(TreeNode tree) throws CloneNotSupportedException{
		getActionPOS(tree);
		
		getActionCHUNK(tree, pos);
		getActionBUILDandCHECK(tree, combine(chunk));

	}
	
	/**
	 * 得到第一步后的子树
	 * @return
	 */
	public List<TreeNode> getPos(){
		return pos;
	}
	
	/**
	 * 得到第2步后的子树
	 * @return
	 */
	public List<TreeNode> getChunk(){
		return chunk;
	}
	
	/**
	 * 得到第3步后的子树
	 * @return
	 */
	public List<List<TreeNode>> getBulidAndCheck(){
		return buildAndCheckTree;
	}
	
	/**
	 * 得到动作序列
	 * @return
	 */
	public List<String> getActions(){
		return actions;
	}
	
	/**
	 * 得到当前的句子
	 * @return
	 */
	public List<String> getWords(){
		return words;
	}
	
	@org.junit.Test
	public void test() throws CloneNotSupportedException{
		String treeStr = "(S(NP(PRP I))(VP(VP(VBD saw)(NP(DT the)(NN man)))(PP(IN with)(NP(DT the)(NN telescope)))))";
		PhraseGenerateTree gst = new PhraseGenerateTree();
	    TreeNode tree = gst.generateTree(treeStr);
	    System.out.println(tree.toString().equals(treeStr));
	    getActionPOS(tree);
	    
	    getActionCHUNK(tree,pos);

	    List<TreeNode> chunkCombine = combine(chunk);

	    getActionBUILDandCHECK(tree,chunkCombine);
	    
	    ActionsToTree a2t = new ActionsToTree();
	    String[] words = {"Mr.","Vinken","is","chairman","of","Elsevier","N.V.",",","the","Dutch","publishing","group"};

	    TreeNode completeTree = a2t.actionsToTree(Arrays.asList(words), actions);
	      
	    for (List<TreeNode> node : buildAndCheckTree) {
			for (TreeNode treeNode : node) {
				System.out.println(treeNode.toString());
			}
			System.out.println();
		}
	}
}
