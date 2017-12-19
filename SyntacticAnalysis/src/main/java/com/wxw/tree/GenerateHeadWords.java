package com.wxw.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * 生成头结点
 * @author 王馨苇
 *
 */
public class GenerateHeadWords {

	private static List<String> ADJP = new ArrayList<String>();
	private static List<String> ADVP = new ArrayList<String>();
	private static List<String> CONJP = new ArrayList<String>();
	private static List<String> LST = new ArrayList<String>();
	private static List<String> NAC = new ArrayList<String>();
	private static List<String> PP = new ArrayList<String>();
	private static List<String> PRT = new ArrayList<String>();
	private static List<String> QP = new ArrayList<String>();
	private static List<String> RRC = new ArrayList<String>();
	private static List<String> S = new ArrayList<String>();
	private static List<String> SBAR = new ArrayList<String>();
	private static List<String> SBARQ = new ArrayList<String>();
	private static List<String> SINV = new ArrayList<String>();
	private static List<String> SQ = new ArrayList<String>();
	private static List<String> VP = new ArrayList<String>();
	private static List<String> WHADJP = new ArrayList<String>(); 
	private static List<String> WHADVP = new ArrayList<String>();
	private static List<String> WHNP = new ArrayList<String>();
	private static List<String> WHPP = new ArrayList<String>();
	//NP
	private static List<String> LEFT2RIGHT = new ArrayList<String>();
	private static List<String> RIGHT2LEFT1 = new ArrayList<String>();
	private static List<String> RIGHT2LEFT2 = new ArrayList<String>();
	private static List<String> RIGHT2LEFT3 = new ArrayList<String>();
	private static List<String> RIGHT2LEFT4 = new ArrayList<String>();
	
	/**
	 * 找到节点的头节点
	 * @param node 当前的子树,此时的node肯定不是叶子,也不是词性标记
	 * @return
	 */
	public static String getHeadWords(HeadTreeNode node){
		//有些非终端节点需要进行处理，因为它可能是NP-SBJ的格式，我只需要拿NP的部分进行匹配操作
		String parentNonTerminal = node.getNodeName().split("-")[0];
		//处理X-X CC X的情况
		boolean flag = false;
		int record = -1;
		//先判断是不是这种结构
		for (int i = 0; i < node.getChildren().size() - 2; i++) {
			if(node.getChildren().get(i).getNodeName().split("-")[0].equals(parentNonTerminal) &&
					node.getChildren().get(i+1).getNodeName().equals("CC") &&
					node.getChildren().get(i+2).getNodeName().split("-")[0].equals(parentNonTerminal)){
				flag = true;
				record = i;
				break;
			}
		}
		if(flag == true && record != -1){
			return node.getChildren().get(record).getHeadWords();
		}
		//其他的情况
		if(parentNonTerminal.equals("ADJP")){
			return getHeadWordsByRules(node, ADJP, "left");
		}else if(parentNonTerminal.equals("ADVP")){
			return getHeadWordsByRules(node, ADVP, "right");
		}else if(parentNonTerminal.equals("CONJP")){
			return getHeadWordsByRules(node, CONJP, "right");
		}else if(parentNonTerminal.equals("FRAG")){//从右面
			return node.getChildren().get(node.getChildren().size() - 1).getHeadWords();
		}else if(parentNonTerminal.equals("INTJ")){//左面
			return node.getChildren().get(0).getHeadWords();
		}else if(parentNonTerminal.equals("LST")){
			return getHeadWordsByRules(node, LST, "right");
		}else if(parentNonTerminal.equals("NAC")){
			return getHeadWordsByRules(node, NAC, "left");
		}else if(parentNonTerminal.equals("PP")){
			return getHeadWordsByRules(node, PP, "right");
		}else if(parentNonTerminal.equals("PRN")){//左
			return node.getChildren().get(0).getHeadWords();
		}else if(parentNonTerminal.equals("PRT")){
			return getHeadWordsByRules(node, PRT, "right");
		}else if(parentNonTerminal.equals("QP")){
			return getHeadWordsByRules(node, QP, "left");
		}else if(parentNonTerminal.equals("RRC")){
			return getHeadWordsByRules(node, RRC, "right");
		}else if(parentNonTerminal.equals("S")){
			return getHeadWordsByRules(node, S, "left");
		}else if(parentNonTerminal.equals("SBAR")){
			return getHeadWordsByRules(node, SBAR, "left");
		}else if(parentNonTerminal.equals("SBARQ")){
			return getHeadWordsByRules(node, SBARQ, "left");
		}else if(parentNonTerminal.equals("SINV")){
			return getHeadWordsByRules(node, SINV, "left");
		}else if(parentNonTerminal.equals("SQ")){
			return getHeadWordsByRules(node, SQ, "left");
		}else if(parentNonTerminal.equals("UCP")){//右
			return node.getChildren().get(node.getChildren().size() - 1).getHeadWords();
		}else if(parentNonTerminal.equals("VP")){
			return getHeadWordsByRules(node, VP, "left");
		}else if(parentNonTerminal.equals("WHADJP")){
			return getHeadWordsByRules(node, WHADJP, "left");
		}else if(parentNonTerminal.equals("WHADVP")){
			return getHeadWordsByRules(node, WHADVP, "right");
		}else if(parentNonTerminal.equals("WHNP")){
			return getHeadWordsByRules(node, WHNP, "left");
		}else if(parentNonTerminal.equals("WHPP")){
			return getHeadWordsByRules(node, WHPP, "right");	    
			//特别的
			//（1）NP
		}else if(parentNonTerminal.equals("NP")){
			return getHeadWordsForNp(node);
		}
		return null;
	}	
	
	/**
	 * 根据规则获取头节点
	 * @param node 非叶子节点
	 * @param list 用于匹配的列表
	 * @param direction 匹配的方向
	 * @return
	 */
	public static String getHeadWordsByRules(HeadTreeNode node,List<String> list, String direction){
		
		if(direction.equals("left")){
			//用所有的子节点从左向右匹配规则中每一个
			for (int i = 0; i < list.size(); i++) {
				for (int j = 0; j < node.getChildren().size(); j++) {
					if(node.getChildren().get(j).getNodeName().equals(list.get(i))){
						return node.getChildren().get(j).getHeadWords();
					}
				}
			}
		}else if(direction.equals("right")){
			for (int i = list.size() -1 ; i >= 0; i--) {
				for (int j = 0; j < node.getChildren().size(); j++) {
					if(node.getChildren().get(j).getNodeName().equals(list.get(i))){
						return node.getChildren().get(j).getHeadWords();
					}
				}
			}
		}
		//如果所有的规则都没有匹配，返回最左边的第一个
		return node.getChildren().get(0).getHeadWords();
	}
	
	/**
	 * 为非终结符NP生成头节点
	 * @param node 子树
	 * @return
	 */
	public static String getHeadWordsForNp(HeadTreeNode node){
		//如果最后一个是POS，返回最后一个
		//即当前节点的最后一个子节点的孩子是1，最后一个子节点的孩子的孩子是0
		if(node.getChildren().get(node.getChildren().size() - 1).getNodeName().split("-")[0].equals("POS")){
			return node.getChildren().get(node.getChildren().size() - 1).getHeadWords();
		}
		//从右到左搜索NN,NNP,NNPS,NNS,NX,POS,JJR
		for (int i = node.getChildren().size() - 1; i >= 0; i--) {
			if(RIGHT2LEFT1.contains(node.getChildren().get(i).getNodeName().split("-")[0])){
				return node.getChildren().get(i).getHeadWords();
			}
		}
		//从左到右搜索NP
		for (int i = 0; i < node.getChildren().size(); i++) {
			if(LEFT2RIGHT.contains(node.getChildren().get(i).getNodeName().split("-")[0])){
				return node.getChildren().get(i).getHeadWords();
			}
		}
		//从右到左搜索$,ADJP,PRN
		for (int i = node.getChildren().size() - 1; i >= 0; i--) {
			if(RIGHT2LEFT2.contains(node.getChildren().get(i).getNodeName().split("-")[0])){
				return node.getChildren().get(i).getHeadWords();
			}
		}
		//从右到左搜索CD
		for (int i = node.getChildren().size() - 1; i >= 0; i--) {
			if(RIGHT2LEFT3.contains(node.getChildren().get(i).getNodeName().split("-")[0])){
				return node.getChildren().get(i).getHeadWords();
			}
		}
		//从右到左搜索"JJ","JJS","RB","QP"
		for (int i = node.getChildren().size() - 1; i >= 0; i--) {
			if(RIGHT2LEFT4.contains(node.getChildren().get(i).getNodeName().split("-")[0])){
				return node.getChildren().get(i).getHeadWords();
			}
		}
		//否则返回最后一个		
		return node.getChildren().get(node.getChildren().size() - 1).getHeadWords();
	}
	
	//静态代码块
	static{
		String[] ADJPStr = {"NNS","QP","NN","$","ADVP","JJ","VBN","VBG","ADJP","JJR","NP","JJS","DT","FW","RBR","RBS","SBAR","RB"};
		for (int i = 0; i < ADJPStr.length; i++) {
			ADJP.add(ADJPStr[i]);
		}
		String[] ADVPStr = {"RB","RBR","RBS","FW","ADVP","TO","CD","JJR","JJ","IN","NP","JJS","NN"};
		for (int i = 0; i < ADVPStr.length; i++) {
			ADVP.add(ADVPStr[i]);
		}
		String[] CONJPStr = {"CC","RB","IN"};
		for (int i = 0; i < CONJPStr.length; i++) {
			CONJP.add(CONJPStr[i]);
		}
		String[] LSTStr = {"LS",":"};
		for (int i = 0; i < LSTStr.length; i++) {
			LST.add(LSTStr[i]);
		}
		String[] NACStr = {"NN","NNS","NNP","NNPS","NP","NAC","EX","$","CD","QP","PRP","VBG","JJ","JJS","JJR","ADJP","FW"};
		for (int i = 0; i < NACStr.length; i++) {
			NAC.add(NACStr[i]);
		}
		String[] PPStr = {"IN","TO","VBG","VBN","RP","FW"};
		for (int i = 0; i < PPStr.length; i++) {
			PP.add(PPStr[i]);
		}
		String[] PRTStr = {"RP"};
		for (int i = 0; i < PRTStr.length; i++) {
			PRT.add(PRTStr[i]);
		}
		String[] QPStr = {"$","IN","NNS","NN","JJ","RB","DT","CD","NCD","QP","JJR","JJS"};
		for (int i = 0; i < QPStr.length; i++) {
			QP.add(QPStr[i]);
		}
		String[] RRCStr = {"VP","NP","ADVP","ADJP","PP"};
		for (int i = 0; i < RRCStr.length; i++) {
			RRC.add(RRCStr[i]);
		}
		String[] SStr = {"TO","IN","VP","S","SBAR","ADJP","UCP","NP"};
		for (int i = 0; i < SStr.length; i++) {
			S.add(SStr[i]);
		}
		String[] SBARStr = {"WHNP","WHPP","WHADVP","IN","DT","S","SQ","SINV","SBAR","FRAG"};
		for (int i = 0; i < SBARStr.length; i++) {
			SBAR.add(SBARStr[i]);
		}
		String[] SBARQStr = {"SQ","S","SINV","SBARQ","FRAG"};
		for (int i = 0; i < SBARQStr.length; i++) {
			SBARQ.add(SBARQStr[i]);
		}
		String[] SINVStr = {"VBZ","VBD","VBP","VB","MD","VP","S","SINV","ADJP","NP"};
		for (int i = 0; i < SINVStr.length; i++) {
			SINV.add(SINVStr[i]);
		}
		String[] SQStr = {"VBZ","VBD","VBP","VB","MD","VP","SQ"};
		for (int i = 0; i < SQStr.length; i++) {
			SQ.add(SQStr[i]);
		}
		String[] VPStr = {"TO","VBD","VBN","MD","VBZ","VB","VBG","VBP","VP","ADJP","NN","NNS","NP"};
		for (int i = 0; i < VPStr.length; i++) {
			VP.add(VPStr[i]);
		}
		String[] WHADJPStr = {"CC","WRB","JJ","ADJP"}; 
		for (int i = 0; i < WHADJPStr.length; i++) {
			WHADJP.add(WHADJPStr[i]);
		}
		String[] WHADVPStr = {"CC","WRB"};
		for (int i = 0; i < WHADVPStr.length; i++) {
			WHADVP.add(WHADVPStr[i]);
		}
		String[] WHNPStr = {"WDT","WP","WP$","WHADJP","WHPP","WHNP"};
		for (int i = 0; i < WHNPStr.length; i++) {
			WHNP.add(WHNPStr[i]);
		}
		String[] WHPPStr = {"IN","TO","FW"};
		for (int i = 0; i < WHPPStr.length; i++) {
			WHPP.add(WHPPStr[i]);
		}
		//下面是NP的部分
		String[] NPStr1 = {"NN","NNP","NNPS","NNS","NX","POS","JJR"};
		for (int i = 0; i < NPStr1.length; i++) {
			RIGHT2LEFT1.add(NPStr1[i]);
		}
		String[] NPStr2 = {"NP"};
		for (int i = 0; i < NPStr2.length; i++) {
			LEFT2RIGHT.add(NPStr2[i]);
		}
		String[] NPStr3 = {"$","ADJP","PRN"};
		for (int i = 0; i < NPStr3.length; i++) {
			RIGHT2LEFT2.add(NPStr3[i]);
		}
		String[] NPStr4 = {"CD"};
		for (int i = 0; i < NPStr4.length; i++) {
			RIGHT2LEFT3.add(NPStr4[i]);
		}
		String[] NPStr5 = {"JJ","JJS","RB","QP"};
		for (int i = 0; i < NPStr5.length; i++) {
			RIGHT2LEFT4.add(NPStr5[i]);
		}
	}
}
