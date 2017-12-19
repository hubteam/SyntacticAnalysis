package com.wxw.actions;

import java.util.ArrayList;
import java.util.List;

import com.wxw.tree.GenerateHeadWords;
import com.wxw.tree.HeadTreeNode;

public class ActionsToHeadTree {

	/**
	 * 第一步pos
	 * @param words 词语
	 * @param actions 动作序列
	 * @return
	 */
	public List<HeadTreeNode> getPosTree(List<String> words,List<String> actions){
		//第一步pos
		List<HeadTreeNode> postree = new ArrayList<HeadTreeNode>();
		for (int i = 0; i < words.size(); i++) {
			HeadTreeNode node = new HeadTreeNode(words.get(i));
			HeadTreeNode actionsNode = new HeadTreeNode(actions.get(i));
			actionsNode.addChild(node);
			node.setParent(actionsNode);
			actionsNode.setHeadWords(node.getNodeName());//需要设置头结点
			postree.add(actionsNode);
		}
		return postree;
	}

	/**
	 * 第二步chunk
	 * @param words 词语
	 * @param actions 动作序列
	 * @return
	 */
	public List<HeadTreeNode> getChunkTree(List<HeadTreeNode> postree,List<String> actions){
		//第二部chunk
		//不用在这里设置头结点
		List<HeadTreeNode> chunktree = new ArrayList<HeadTreeNode>();
		int len = postree.size();
		for (int i = 0; i < len; i++) {
			HeadTreeNode chunk = new HeadTreeNode(actions.get(i+len));
			chunk.addChild(postree.get(i));
			postree.get(i).setParent(chunk);
			chunktree.add(chunk);
		}
		return chunktree;		
	}
	
	/**
	 * chunk步得到的结果进行合并
	 * @param chunktree chunk子树
	 * @return
	 */
	public List<HeadTreeNode> combine(List<HeadTreeNode> chunktree){
		//第三部合并
		//需要为合并后的结点设置头结点
		List<HeadTreeNode> combine = new ArrayList<HeadTreeNode>();
		//遍历所有子树
		for (int i = 0; i < chunktree.size(); i++) {		
			//当前子树的根节点是start标记的		
			if(chunktree.get(i).getNodeName().split("_")[0].equals("start")){			
				//只要是start标记的就去掉root中的start，生成一颗新的子树，		
				//因为有些结构，如（NP(NN chairman)），只有start没有join部分，
				//所以遇到start就生成新的子树
				HeadTreeNode node = new HeadTreeNode(chunktree.get(i).getNodeName().split("_")[1]);			
				node.addChild(chunktree.get(i).getChildren().get(0));	
				node.setHeadWords(GenerateHeadWords.getHeadWords(node));
				chunktree.get(i).getChildren().get(0).setParent(node);			
				for (int j = i+1; j < chunktree.size(); j++) {			
					//判断start后是否有join如果有，就和之前的start合并				
					if(chunktree.get(j).getNodeName().split("_")[0].equals("join")){					
						node.addChild(chunktree.get(j).getChildren().get(0));					
						chunktree.get(j).getChildren().get(0).setParent(node);				
					}else if(chunktree.get(j).getNodeName().split("_")[0].equals("start") ||					
							chunktree.get(j).getNodeName().split("_")[0].equals("other")){				
						break;				
					}			
				}
				//头结点
				node.setHeadWords(GenerateHeadWords.getHeadWords(node));
				//将一颗合并过的完整子树加入列表
				combine.add(node);
				//标记为other的，去掉other		 
			}else if(chunktree.get(i).getNodeName().equals("other")){										
				chunktree.get(i).getChildren().get(0).setParent(null);	
				//其实这里的头结点就是在pos步骤中设置的头结点
				chunktree.get(i).getChildren().get(0).setHeadWords(chunktree.get(i).getChildren().get(0).getHeadWords());
				combine.add(chunktree.get(i).getChildren().get(0));		
			}
		}
		return combine;
	}
	
	/**
	 * build和check步得到完整的树
	 * @param len 词语的长度，作用是根据这个长度计算出当前应从动作序列中的哪个位置开始
	 * @param combine combine之后的子树
	 * @param actions 动作序列
	 * @return
	 */
	public HeadTreeNode getTree(int len,List<HeadTreeNode> combine,List<String> actions){
		//第四部build和check
		int j = 0;
		//遍历上一步得到的combine，根据action进行操作
		for (int i = 0; i < combine.size(); i++) {
			HeadTreeNode node = new HeadTreeNode(actions.get(j+2*len));
			node.addChild(combine.get(i));
			combine.get(i).setParent(node);
			combine.set(i, node);
			j++;
			if(actions.get(j+2*len).equals("no")){//检测为no什么都不做
				
			}else if(actions.get(j+2*len).equals("yes")){//检测为yes，要和前面到start的部分合并
				//合并的時候是从当前的位置往前寻找，找到start
				int currentIndex = i;
				int preIndex = -1;//记录前面的start位置
				while(!combine.get(i--).getNodeName().split("_")[0].equals("start")){
					if(i < 0){
						break;
					}					
				}
				preIndex = i+1;
				//进行合并
				//建立合并后的父节点
				HeadTreeNode combineNode = new HeadTreeNode(combine.get(preIndex).getNodeName().split("_")[1]);
				for (int k = preIndex; k <= currentIndex; k++) {
					combineNode.addChild(combine.get(k).getChildren().get(0));
					combine.get(k).getChildren().get(0).setParent(combineNode);
				}
				//设置头结点
				combineNode.setHeadWords(GenerateHeadWords.getHeadWords(combineNode));
				combine.set(preIndex, combineNode);
				//删除那些用于合并的join
				for (int k = currentIndex; k >= preIndex+1; k--) {
					combine.remove(preIndex+1);
				}
				//从合并后的位置继续开始搜索
				i = preIndex - 1;
			}
			if(j+1+2*len < actions.size()){
				j++;
			}else{
				break;
			}
		}		
		return combine.get(0);		
	}
	
	/**
	 * 动作序列转成一颗完整的树
	 * @param words 词语
	 * @param actions 动作序列
	 * @return
	 */
	public HeadTreeNode actionsToTree(List<String> words,List<String> actions){
		//第一步pos
		List<HeadTreeNode> postree = getPosTree(words,actions);
		//第二部chunk
		List<HeadTreeNode> chunktree = getChunkTree(postree,actions);
		//第三部合并
		//需要为合并后的结点设置头结点
		List<HeadTreeNode> combine = combine(chunktree);
		//第四部build和check
		HeadTreeNode tree = getTree(words.size(),combine,actions);
		return tree;
	}
}
