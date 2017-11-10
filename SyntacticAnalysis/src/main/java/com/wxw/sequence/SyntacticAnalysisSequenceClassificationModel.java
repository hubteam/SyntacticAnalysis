package com.wxw.sequence;

import java.util.List;

import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.tree.TreeNode;

import opennlp.tools.util.Sequence;

/**
 * 得到结果序列的接口
 * @author 王馨苇
 *
 */
public interface SyntacticAnalysisSequenceClassificationModel {

	/**
	 * 得到最好的结果
	 * @param character 字序列
	 * @param tags 字的标记序列
	 * @param words 词语序列
	 * @param poses 词性标记序列
	 * @param chunkTree chunk步得到的树
	 * @param buildAndCheckTree buildAndCheck步得到的树
	 * @param ac 额外的信息
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	Sequence bestSequence(List<String> character, List<String> tags, List<String> words, List<String> poses, List<TreeNode> chunkTree, 
			List<List<TreeNode>> buildAndCheckTree, Object[] ac, SyntacticAnalysisContextGenerator generator, SyntacticAnalysisSequenceValidator validator) ;

	/**
	 * 得到最好的num个结果
	 * @param num 最好的num个序列
	 * @param character 字序列
	 * @param tags 字的标记序列
	 * @param words 词语序列
	 * @param poses 词性标记序列
	 * @param chunkTree chunk步得到的树
	 * @param buildAndCheckTree buildAndCheckbuildAndCheck
	 * @param ac 额外的信息
	 * @param min 得分最低的限制
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	Sequence[] bestSequences(int num, List<String> character, List<String> tags, List<String> words, List<String> poses, 
			List<TreeNode> chunkTree, List<List<TreeNode>> buildAndCheckTree, Object[] ac, double min, SyntacticAnalysisContextGenerator generator,
			SyntacticAnalysisSequenceValidator validator);

	/**
	 * 得到最好的num个结果
	 * @param num 最好的num个序列
	 * @param character 字序列
	 * @param tags 字的标记序列
	 * @param words 词语序列
	 * @param poses 词性标记序列
	 * @param chunkTree chunk步得到的树
	 * @param buildAndCheckTree buildAndCheck步得到的树
	 * @param ac 额外的信息
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	Sequence[] bestSequences(int num, List<String> character, List<String> tags, List<String> words, List<String> poses, 
			List<TreeNode> chunkTree, List<List<TreeNode>> buildAndCheckTree, Object[] ac, SyntacticAnalysisContextGenerator generator,
			SyntacticAnalysisSequenceValidator validator);
	
	/**
	 * 得到最好的结果
	 * @return
	 */
	String[] getOutcomes();
}
