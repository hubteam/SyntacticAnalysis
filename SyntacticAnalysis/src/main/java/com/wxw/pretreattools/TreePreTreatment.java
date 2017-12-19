package com.wxw.pretreattools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import com.wxw.stream.FileInputStreamFactory;
import com.wxw.stream.PlainTextByTreeStream;
import com.wxw.tree.PhraseGenerateTree;
import com.wxw.tree.TreeNode;

/**
 * 训练语料中树的初始化处理
 * @author 王馨苇
 *
 */
public class TreePreTreatment{

	
	private static HashSet<Character> hsalbdigit = new HashSet<>();
	
	static{
		//罗列了半角和全角的情况
		String albdigits = "０１２３４５６７８９0123456789";
		for (int i = 0; i < albdigits.length(); i++) {
			hsalbdigit.add(albdigits.charAt(i));
		}
	}
	
	/**
	 * 预处理
	 * @param path 路径
	 * @throws UnsupportedOperationException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void pretreatment(String path) throws UnsupportedOperationException, FileNotFoundException, IOException{
		//读取一颗树
		String filename = "";
		PlainTextByTreeStream lineStream = null;
		PhraseGenerateTree pgt = new PhraseGenerateTree();		
		//创建输出流
		BufferedWriter bw = new BufferedWriter(new FileWriter("data\\tree\\"+path+".txt"));
		for (int i = 1; i < 200; i++) {
			if(i < 10){
				filename = "000" + i;
			}else if(i < 100){
				filename = "00" + i;
			}else{
				filename = "0" + i;
			}
			lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File("data\\"+path+"\\wsj_"+filename+".mrg")), "utf8");
			String tree = "";
			while((tree = lineStream.read()) != ""){
				TreeNode node = pgt.generateTree(tree);
				//对树进行遍历
				travelTree(node);	
				String newTreeStr = node.toNoNoneSample();
				TreeNode newTree = pgt.generateTree("("+newTreeStr+")");
				bw.write("("+TreeNode.printTree(newTree, 1)+")");
				bw.newLine();
			}
		}
		bw.close();
		lineStream.close();
	}
	
	//判断是否是数字【中文数字，阿拉伯数字（全角和半角）】
	private static boolean isDigit(char c){
		if(hsalbdigit.contains(c)){
			return true;
		}else{
			return false;
		}	
	}

	/**
	 * 对树进行遍历删除NONE【这里的删除试将属性flag设置为false】
	 * @param node 一棵树
	 */
	public static void travelTree(TreeNode node){
		if(node.getChildren().size() != 0){
			for (TreeNode treenode:node.getChildren()) {
				travelTree(treenode);
			}
		}		
		if(!node.isLeaf()){
			if(node.getNodeName().contains("NONE")){
				//该节点的父节点只有空节点一个孩子
				if(node.getParent().getChildren().size() > 1){
					//将NONE和NONE的子节点标记位false
					node.setFlag(false);
					node.getChildren().get(0).setFlag(false);	
					//(SBAR(-NONE- 0)(S(-NONE- *T*-1)))
					if(node.getParent().getChildren().size() == 2){
						node.getParent().setFlag(false);
						if(node.getParent().getChildren().get(1).getChildren().size() == 1){
							if(node.getParent().getChildren().get(1).getChildren().get(0).getNodeName().contains("NONE")){
								node.getParent().getChildren().get(1).setFlag(false);
								node.getParent().getChildren().get(1).getChildren().get(0).setFlag(false);
								node.getParent().getChildren().get(1).getChildren().get(0).getChildren().get(0).setFlag(false);
								//(VP (VBD reported) (SBAR (-NONE- 0) (S (-NONE- *T*-1) )))变为(VBD reported)
								if(node.getParent().getParent().getChildren().size() == 2){
									node.getParent().getParent().setFlag(false);
								}
							}
						}
					}
				}else if(node.getParent().getChildren().size() == 1){
					//将NONE和NONE的子节点和父节点标记位false
					node.setFlag(false);
					node.getChildren().get(0).setFlag(false);
					node.getParent().setFlag(false);
					//(S(NP(-NONE- *-1))(VP(To to)(VP ....)))
					if(node.getParent().getParent().getChildren().size() == 2){
						node.getParent().getParent().setFlag(false);
					}
				}
			}else if(node.getNodeName().contains("-")){
				if(!node.getNodeName().equals("-LRB-") && !(node.getNodeName().equals("-RRB-"))){
					node.setNewName(node.getNodeName().split("-")[0]);
				}
			}else if(isDigit(node.getNodeName().charAt(node.getNodeName().length()-1))){
				if(isDigit(node.getNodeName().charAt(node.getNodeName().length()-2))){
					node.setNewName(node.getNodeName().substring(0, node.getNodeName().length()-3));
				}else{
					node.setNewName(node.getNodeName().substring(0, node.getNodeName().length()-2));
				}
			}
		}
	}
}
