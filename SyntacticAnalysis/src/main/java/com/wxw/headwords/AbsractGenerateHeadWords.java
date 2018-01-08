package com.wxw.headwords;

import java.util.HashMap;
import java.util.List;

import com.wxw.headwords.Rule;
import com.wxw.tree.HeadTreeNode;

/**
 * 生成头结点的模板类【模板设计模式】
 * @author 王馨苇
 *
 */
public abstract class AbsractGenerateHeadWords<T extends HeadTreeNode>{
	
	/**
	 * 为并列结构生成头结点
	 * @param node 子节点带头结点，父节点不带头结点的树
	 * @return
	 */
	public abstract String generateHeadWordsForCordinator(T node);

	/**
	 * 为特殊规则生成头结点
	 * @param node 子节点带头结点，父节点不带头结点的树
	 * @param specialRules 生成头结点的特殊规则
	 * @return
	 */
	public abstract String generateHeadWordsForSpecialRules(T node,HashMap<String,List<Rule>> specialRules);
	
	/**
	 * 为一般规则生成头结点
	 * @param node 子节点带头结点，父节点不带头结点的树
	 * @param normalRules 生成头结点的一般规则
	 * @return
	 */
	public abstract String generateHeadWordsForNormalRules(T node,HashMap<String,Rule> normalRules);

	/**
	 * 提取头结点【自底向上生成头结点】
	 * @param node 子节点带头结点，父节点不带头结点的树
	 * @param normalRules 生成头结点的一般规则
	 * @param specialRules 生成头结点的特殊规则
	 * @return
	 */
	public String extractHeadWords(T node, HashMap<String,Rule> normalRules,HashMap<String,List<Rule>> specialRules){
		String headWords = null;
		headWords = generateHeadWordsForCordinator(node);
		
		if(headWords == null && specialRules != null){			
			headWords = generateHeadWordsForSpecialRules(node,specialRules);	
		}
		
		if(headWords == null && normalRules != null){
			headWords = generateHeadWordsForNormalRules(node,normalRules);
		}
		return headWords;
	}
}
