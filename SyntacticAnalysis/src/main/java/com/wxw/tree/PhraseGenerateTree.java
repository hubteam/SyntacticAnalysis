package com.wxw.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.wxw.tree.TreeNode;

/**
 * 根据括号表达式产生句法树
 * @author 王馨苇
 *
 */
public class PhraseGenerateTree {
	
	/**
	 * 产生句法树
	 * @param treeStr 括号表达式
	 * @return
	 */
	public TreeNode generateTree(String treeStr){
		int indexTree;//记录当前是第几颗子树
		List<String> parts = stringToList(treeStr);
        Stack<TreeNode> tree = new Stack<TreeNode>();
        for (int i = 0; i < parts.size(); i++) {
			if(!parts.get(i).equals(")") && !parts.get(i).equals(" ")){
				tree.push(new TreeNode(parts.get(i)));
			}else if(parts.get(i).equals(" ")){
				
			}else if(parts.get(i).equals(")")){
				indexTree = 0;
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
//				System.out.println(tree.toString());
			}
		}
        TreeNode treeStruct = tree.pop();

        return treeStruct;
	}
	
	/**
	 * 为预处理产生的句法树
	 * @param treeStr 括号表达式
	 * @return
	 */
	public TreeNode generateTreeForPreTreatment(String treeStr){
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
//        System.out.println(treeStruct.toNewSample());
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
}
