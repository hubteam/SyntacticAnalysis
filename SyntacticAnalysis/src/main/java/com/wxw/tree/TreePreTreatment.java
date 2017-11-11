package com.wxw.tree;

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

import com.wxw.stream.FileInputStreamFactory;
import com.wxw.stream.PlainTextByTreeStream;

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
//			System.out.println(filename);
			lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File("data\\"+path+"\\wsj_"+filename+".mrg")), "utf8");
			String tree = "";
			while((tree = lineStream.read()) != ""){
				String treeStr = format(tree);
				TreeNode node = pgt.generateTreeForPreTreatment(tree);
				//对树进行遍历
				travelTree(node);				
				bw.write(node.toNewSample());
//				System.out.println(node.toNewSample());
				bw.newLine();
			}
		}
		bw.close();
		lineStream.close();
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
				}else if(node.getParent().getChildren().size() == 1){
					//将NONE和NONE的子节点和父节点标记位false
					node.setFlag(false);
					node.getChildren().get(0).setFlag(false);
					node.getParent().setFlag(false);
				}
			}else if(isDigit(node.getNodeName().charAt(node.getNodeName().length()-1))){
//				node.setNewName(node.getNodeName().substring(0, node.getNodeName().length()-2));
				if(isDigit(node.getNodeName().charAt(node.getNodeName().length()-2))){
					node.setNewName(node.getNodeName().substring(0, node.getNodeName().length()-3));
				}else{
					node.setNewName(node.getNodeName().substring(0, node.getNodeName().length()-2));
				}
			}
		}
	}
}
