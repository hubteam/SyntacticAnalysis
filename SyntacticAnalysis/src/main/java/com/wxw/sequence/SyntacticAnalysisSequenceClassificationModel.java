package com.wxw.sequence;

import java.util.List;

import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.tree.TreeNode;

/**
 * 得到结果序列的接口
 * @author 王馨苇
 *
 */
public interface SyntacticAnalysisSequenceClassificationModel {

	/**
	 * 得到最好的chunk结果
	 * @param posTree pos步得到的最好的K棵树
	 * @param ac 额外的信息
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	SyntacticAnalysisSequenceForChunk bestSequenceForChunk(List<List<TreeNode>> posTree, Object[] ac, SyntacticAnalysisContextGenerator generator, SyntacticAnalysisSequenceValidator validator) ;

	/**
	 * 得到最好的num个chunk结果
	 * @param num 最好的num个序列
	 * @param posTree pos步得到的最好的K棵树
	 * @param ac 额外的信息
	 * @param min 得分最低的限制
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	SyntacticAnalysisSequenceForChunk[] bestSequencesForChunk(int num, List<List<TreeNode>> posTree, Object[] ac, double min, SyntacticAnalysisContextGenerator generator,
			SyntacticAnalysisSequenceValidator validator);

	/**
	 * 得到最好的num个chunk结果
	 * @param num 最好的num个序列
	 * @param posTree pos步得到的最好的K棵树
	 * @param ac 额外的信息
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	SyntacticAnalysisSequenceForChunk[] bestSequencesForChunk(int num, List<List<TreeNode>> posTree, Object[] ac, SyntacticAnalysisContextGenerator generator,
			SyntacticAnalysisSequenceValidator validator);
	
	/**
	 * 得到最好的BuildAndCheck结果
	 * @param comnineChunkTree chunk步得到的最好的K棵树合并之后
	 * @param ac 额外的信息
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	SyntacticAnalysisSequenceForBuildAndCheck bestSequenceForBuildAndCheck(List<List<TreeNode>> comnineChunkTree, Object[] ac, SyntacticAnalysisContextGenerator generator, SyntacticAnalysisSequenceValidator validator) ;

	/**
	 * 得到最好的num个BuildAndCheck结果
	 * @param num 最好的num个序列
	 * @param comnineChunkTree chunk步得到的最好的K棵树合并之后
	 * @param ac 额外的信息
	 * @param min 得分最低的限制
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	SyntacticAnalysisSequenceForBuildAndCheck[] bestSequencesForBuildAndCheck(int num, List<List<TreeNode>> comnineChunkTree, Object[] ac, double min, SyntacticAnalysisContextGenerator generator,
			SyntacticAnalysisSequenceValidator validator);

	/**
	 * 得到最好的num个BuildAndCheck结果
	 * @param num 最好的num个序列
	 * @param comnineChunkTree chunk步得到的最好的K棵树合并之后
	 * @param ac 额外的信息
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	SyntacticAnalysisSequenceForBuildAndCheck[] bestSequencesForBuildAndCheck(int num, List<List<TreeNode>> comnineChunkTree, Object[] ac, SyntacticAnalysisContextGenerator generator,
			SyntacticAnalysisSequenceValidator validator);
}
