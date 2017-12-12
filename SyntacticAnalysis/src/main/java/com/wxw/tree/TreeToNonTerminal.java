package com.wxw.tree;

import java.util.ArrayList;
import java.util.List;

import com.wxw.tool.EvaluationTools;

/**
 * 将一颗完整的树转成非终端节点和包含的词语的开始结束序号的一个序列，用于比较
 * @author 王馨苇
 *
 */
public class TreeToNonTerminal {

	private int wordindex = 0;

	/**
	 * 将一颗完整的树转成非终端节点和包含的词语的开始结束序号的一个序列，用于比较
	 * @param node 一颗完整的树
	 * @return
	 */
	public List<EvaluationTools> getTreeToNonterminal(TreeNode node){
		
		List<EvaluationTools> list = new ArrayList<>();
		for (int i = 0; i < node.getChildren().size(); i++) {
			list.addAll(getTreeToNonterminal(node.getChildren().get(i)));
		}
		if(node.getChildren().size() != 0){	
			int begin = Integer.parseInt(node.getChildren().get(0).getNodeName().split("_")[1]);			
			int end = Integer.parseInt(node.getChildren().get(node.getChildren().size()-1).getNodeName().split("_")[2]);							
			node.setNewName(node.getNodeName()+"_"+begin+"_"+end);
			if(node.getChildren().size() == 1 && node.getChildren().get(0).getChildren().size() == 0){
				
			}else{
				list.add(new EvaluationTools(node.getNodeName().split("_")[0], begin, end));
			}
		}else{
			node.setNewName(node.getNodeName()+"_"+wordindex+"_"+(++wordindex));
		}
		return list;
	}
}
