package com.wxw.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionsToTree {

	public TreeNode actionsToTree(List<String> words,List<String> actions){
		//第一步pos
		List<TreeNode> postree = new ArrayList<TreeNode>();
		for (int i = 0; i < words.size(); i++) {
			TreeNode node = new TreeNode(words.get(i));
			TreeNode actionsNode = new TreeNode(actions.get(i));
			actionsNode.addChild(node);
			node.setParent(actionsNode);
			postree.add(actionsNode);
		}
		
		//第二部chunk
		List<TreeNode> chunktree = new ArrayList<TreeNode>();
		int len = words.size();
		for (int i = 0; i < len; i++) {
			TreeNode chunk = new TreeNode(actions.get(i+len));
			chunk.addChild(postree.get(i));
			postree.get(i).setParent(chunk);
			chunktree.add(chunk);
		}
		
		//第三部合并
		List<TreeNode> combine = new ArrayList<TreeNode>();
		//遍历所有子树
		for (int i = 0; i < chunktree.size(); i++) {		
			//当前子树的根节点是start标记的		
			if(chunktree.get(i).getNodeName().split("_")[0].equals("start")){			
				//只要是start标记的就去掉root中的start，生成一颗新的子树，		
				//因为有些结构，如（NP(NN chairman)），只有start没有join部分，
				//所以遇到start就生成新的子树
				TreeNode node = new TreeNode(chunktree.get(i).getNodeName().split("_")[1]);			
				node.addChild(chunktree.get(i).getChildren().get(0));			
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
				//将一颗合并过的完整子树加入列表
				combine.add(node);
				//标记为other的，去掉other		 
			}else if(chunktree.get(i).getNodeName().equals("other")){										
				chunktree.get(i).getChildren().get(0).setParent(null);			
				combine.add(chunktree.get(i).getChildren().get(0));		
			}
		}
				
		//第四部build和check
//		List<TreeNode> buildAndCheckTree = new ArrayList<TreeNode>();
		int j = 0;
		//遍历上一步得到的combine，根据action进行操作
		for (int i = 0; i < combine.size(); i++) {
			TreeNode node = new TreeNode(actions.get(j+2*len));
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
				TreeNode combineNode = new TreeNode(combine.get(preIndex).getNodeName().split("_")[1]);
				for (int k = preIndex; k <= currentIndex; k++) {
					combineNode.addChild(combine.get(k).getChildren().get(0));
					combine.get(k).getChildren().get(0).setParent(combineNode);
				}
				combine.set(preIndex, combineNode);
				//删除那些用于合并的join
				for (int k = currentIndex; k >= preIndex+1; k--) {
					combine.remove(preIndex+1);
				}
//				for (int k = preIndex+1; k <= currentIndex; k++) {
//					combine.remove(k);
//				}
				
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
	
	@org.junit.Test
	public void test(){
		String[] words = {"I","saw","the","man","with","the","telescope"};
		String[] actions = {"PRP","VBD","DT","NN","IN","DT","NN","start_NP","other","start_NP","join_NP","other","start_NP","join_NP",
				"start_S","no","start_VP","no","join_VP","yes","start_VP","no","start_PP","no","join_PP","yes","join_VP","yes","join_S","yes"};
		TreeNode tree = actionsToTree(Arrays.asList(words),Arrays.asList(actions));
		System.out.println(tree.toString());
	}
}
