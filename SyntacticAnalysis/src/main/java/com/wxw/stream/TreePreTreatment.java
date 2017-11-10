package com.wxw.stream;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import com.wxw.tree.PhraseGenerateTree;
import com.wxw.tree.TreeNode;

/**
 * 训练语料中树的初始化处理
 * @author 王馨苇
 *
 */
public class TreePreTreatment{

	/**
	 * 预处理
	 * @throws UnsupportedOperationException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void pretreatment() throws UnsupportedOperationException, FileNotFoundException, IOException{
		//读取一颗树
		String filename = "";
		PlainTextByTreeStream lineStream = null;
		PhraseGenerateTree pgt = new PhraseGenerateTree();		
		//创建输出流
		BufferedWriter bw = new BufferedWriter(new FileWriter("data\\train\\train.txt"));
//		for (int i = 0; i < 200; i++) {
//			if(i<100){
//				filename = "00" + i;
//			}else{
//				filename = "0" + i;
//			}
			lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File("data\\train\\wsj_0076.mrg")), "utf8");
			String tree = "";
			while((tree = lineStream.read()) != null){
				String treeStr = format(tree);
				TreeNode node = pgt.generateTree(tree);
				//对树进行遍历
				travelTree(node);
				System.out.println("res:"+node.toString());
				bw.write(node.toString());
				bw.newLine();
			}
//		}
		bw.close();
		lineStream.close();
	}
	
	static HashSet<Character> hsalbdigit = new HashSet<>();
	
	static{
		//罗列了半角和全角的情况
		String albdigits = "０１２３４５６７８９0123456789";
		for (int i = 0; i < albdigits.length(); i++) {
			hsalbdigit.add(albdigits.charAt(i));
		}
	}
	
	//判断是否是数字【中文数字，阿拉伯数字（全角和半角）】
	public static boolean isDigit(char c){
		if(hsalbdigit.contains(c)){
			return true;
		}else{
			return false;
		}	
	}
	
	/**
	 * 格式化为形如：(A(B1(C1 d1)(C2 d2))(B2 d3)) 的括号表达式。叶子及其父节点用一个空格分割，其他字符紧密相连。
	 * @param tree 从训练语料拼接出的一棵树
	 */
	public static String format(String tree){
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
	
	public static void travelTree(TreeNode node){
//		System.out.println(node.toString());
		if(!node.isLeaf()){
			if(node.getNodeName().contains("NONE")){
				//该节点的父节点只有空节点一个孩子
				if(node.getParent().getChildren().size() > 1){
					node.getParent().getChildren().remove(node.getIndex());
				}else if(node.getParent().getChildren().size() == 1){
					int index = node.getParent().getIndex();
					node = node.getParent().getParent();
					node.getChildren().remove(index);
//					travelTree(node);
//					return;
				}
			}else if(isDigit(node.getNodeName().charAt(node.getNodeName().length()-1))){
				node.setNewName(node.getNodeName().substring(0, node.getNodeName().length()-2));
			}
		}
		for (TreeNode treenode:node.getChildren()) {
			travelTree(treenode);
		}
		
	}
}
