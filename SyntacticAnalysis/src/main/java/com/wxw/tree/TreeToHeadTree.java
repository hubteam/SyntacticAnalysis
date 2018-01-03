package com.wxw.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.wxw.headwords.AbsractGenerateHeadWords;
import com.wxw.headwords.ConcreteGenerateHeadWords;
import com.wxw.headwords.HeadWordsRuleSet;
/**
 * 
 * @author 王馨苇
 *
 */
public class TreeToHeadTree {

	private AbsractGenerateHeadWords<HeadTreeNode> aghw = new ConcreteGenerateHeadWords(); 
	/**
	 * 将一颗无头结点的树转成带头结点的树
	 * @param treeNode
	 * @return
	 */
	public HeadTreeNode treeToHeadTree(TreeNode treeNode){
		String treeStr = "("+treeNode.toString()+")";
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
					node.setHeadWords(aghw.extractHeadWords(node, HeadWordsRuleSet.getNormalRuleSet(), HeadWordsRuleSet.getSpecialRuleSet()));
				}
				tree.push(node);
			}
		}
        HeadTreeNode treeStruct = tree.pop();
        return treeStruct;
	}
	
	/**
	 * 将括号表达式去掉空格转成列表的形式
	 * @param treeStr 括号表达式
	 * @return
	 */
	public List<String> stringToList(String treeStr){

		List<String> parts = new ArrayList<String>();
        for (int index = 0; index < treeStr.length(); ++index) {
            if (treeStr.charAt(index) == '(' || treeStr.charAt(index) == ')' || treeStr.charAt(index) == ' ') {
                parts.add(Character.toString(treeStr.charAt(index)));
            } else {
                for (int i = index + 1; i < treeStr.length(); ++i) {
                    if (treeStr.charAt(i) == '(' || treeStr.charAt(i) == ')' || treeStr.charAt(i) == ' ') {
                        parts.add(treeStr.substring(index, i));
                        index = i - 1;
                        break;
                    }
                }
            }
        }
        return parts;
	}
	
	/**
	 * 格式化为形如：(A(B1(C1 d1)(C2 d2))(B2 d3)) 的括号表达式。叶子及其父节点用一个空格分割，其他字符紧密相连。
	 * @param tree 从训练语料拼接出的一棵树
	 */
	public String format(String tree){
		//去除最外围的括号
        tree = tree.substring(1, tree.length() - 1).trim();
        //所有空白符替换成一位空格
        tree = tree.replaceAll("\\s+", " ");
        //去掉 ( 和 ) 前的空格
        String newTree = "";
        for (int c = 0; c < tree.length(); ++c) {
            if (tree.charAt(c) == ' ' && (tree.charAt(c + 1) == '(' || tree.charAt(c + 1) == ')')) {
                continue;
            } else {
                newTree = newTree + (tree.charAt(c));
            }
        }
        return newTree;
	}
}
