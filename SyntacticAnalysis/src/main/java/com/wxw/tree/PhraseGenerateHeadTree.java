package com.wxw.tree;

import java.util.List;
import java.util.Stack;

/**
 * 根据括号表达式产生句法树
 * @author 王馨苇
 *
 */
public class PhraseGenerateHeadTree extends PhraseGenerateTree{
	
	/**
	 * 产生句法树
	 * @param treeStr 括号表达式
	 * @return
	 */
	public HeadTreeNode generateTree(String treeStr){
		treeStr = format(treeStr);
		int indexTree;//记录当前是第几颗子树
		List<String> parts = stringToList(treeStr); 
        Stack<HeadTreeNode> tree = new Stack<HeadTreeNode>();
        for (int i = 0; i < parts.size(); i++) {
			if(!parts.get(i).equals(")") && !parts.get(i).equals(" ")){
				tree.push(new HeadTreeNode(parts.get(i)));
			}else if(parts.get(i).equals(" ")){
				
			}else if(parts.get(i).equals(")")){
				indexTree = 0;
				Stack<HeadTreeNode> temp = new Stack<HeadTreeNode>();
				while(!tree.peek().getNodeName().equals("(")){
					if(!tree.peek().getNodeName().equals(" ")){
						temp.push(tree.pop());
					}
				}
				tree.pop();
				HeadTreeNode node = temp.pop();
				while(!temp.isEmpty()){		
					temp.peek().setParent(node);
					temp.peek().setIndex(indexTree++);
					node.addChild(temp.pop());
				}
				//设置头节点的部分
				//为每一个非终结符，且不是词性标记的设置头节点
				//对于词性标记的头节点就是词性标记对应的词本身				
				//(1)为词性标记的时候，头节点为词性标记下的词语
				if(node.getChildren().size() == 1 && node.getChildren().get(0).getChildren().size() == 0){
					node.setHeadWords(node.getChildren().get(0).getNodeName());
				//(2)为非终结符，且不是词性标记的时候，由规则推出
				}else if(!node.isLeaf()){
					node.setHeadWords(GenerateHeadWords.getHeadWords(node));
				}
				tree.push(node);
			}
		}
        HeadTreeNode treeStruct = tree.pop();
        return treeStruct;
	}
}
