package com.wxw.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 生成不带头结点的Tree
 * @author 王馨苇
 *
 */
public class PhraseGenerateTree {

	/**
	 * 为预处理产生的句法树
	 * @param treeStr 括号表达式
	 * @return
	 */
	public TreeNode generateTree(String treeStr){
		treeStr = format(treeStr);
		List<String> parts = stringToList(treeStr);
        Stack<TreeNode> tree = new Stack<TreeNode>();
        for (int i = 0; i < parts.size(); i++) {
			if(!parts.get(i).equals(")") && !parts.get(i).equals(" ")){
				TreeNode tn = new TreeNode(parts.get(i));
				tn.setFlag(true);
				tree.push(tn);
			}else if(parts.get(i).equals(" ")){
				
			}else if(parts.get(i).equals(")")){
				Stack<TreeNode> temp = new Stack<TreeNode>();
				while(!tree.peek().getNodeName().equals("(")){
					if(!tree.peek().getNodeName().equals(" ")){
						temp.push(tree.pop());
					}
				}
				tree.pop();
				TreeNode node = temp.pop();
				while(!temp.isEmpty()){		
					temp.peek().setParent(node);
					node.addChild(temp.pop());
				}
				tree.push(node);
			}
		}
        TreeNode treeStruct = tree.pop();
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
