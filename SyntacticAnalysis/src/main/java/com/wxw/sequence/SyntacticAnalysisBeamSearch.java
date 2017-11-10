package com.wxw.sequence;

import java.util.List;

import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.tree.TreeNode;

import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.util.Cache;
import opennlp.tools.util.Sequence;

/**
 * 得到最好的K个结果的实现类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisBeamSearch implements SyntacticAnalysisSequenceClassificationModel{

	public static final String BEAM_SIZE_PARAMETER = "BeamSize";
	private static final Object[] EMPTY_ADDITIONAL_CONTEXT = new Object[0];
	protected int size;
	protected MaxentModel model;
	private double[] probs;
	private Cache<String[], double[]> contextsCache;
	private static final int zeroLog = -100000;

	public SyntacticAnalysisBeamSearch(int size, MaxentModel model) {
		this(size, model, 0);
	}

	public SyntacticAnalysisBeamSearch(int size, MaxentModel model, int cacheSize) {
		this.size = size;
		this.model = model;
		if (cacheSize > 0) {
			this.contextsCache = new Cache(cacheSize);
		}

		this.probs = new double[model.getNumOutcomes()];
	}
	
	/**
	 * 得到最好的结果
	 * @param character 字序列
	 * @param tags 字的标记序列
	 * @param words 词语序列
	 * @param poses 词性标记序列
	 * @param chunkTree chunk步得到的树
	 * @param buildAndCheckTree buildAndCheckbuildAndCheck
	 * @param ac 额外的信息
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	@Override
	public Sequence bestSequence(List<String> characters, List<String> tags, List<String> words, List<String> poses,
			List<TreeNode> chunkTree, List<List<TreeNode>> buildAndCheckTree, Object[] ac,
			SyntacticAnalysisContextGenerator generator, SyntacticAnalysisSequenceValidator validator) {
		Sequence[] sequences = this.bestSequences(1, characters, tags, words, poses, chunkTree,buildAndCheckTree,ac,generator,validator);
		return sequences.length > 0 ? sequences[0] : null;
	}

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
	@Override
	public Sequence[] bestSequences(int num, List<String> character, List<String> tags, List<String> words,
			List<String> poses, List<TreeNode> chunkTree, List<List<TreeNode>> buildAndCheckTree, Object[] ac,
			double min, SyntacticAnalysisContextGenerator generator, SyntacticAnalysisSequenceValidator validator) {
		
		return null;
	}

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
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	@Override
	public Sequence[] bestSequences(int num, List<String> characters, List<String> tags, List<String> words,
			List<String> poses, List<TreeNode> chunkTree, List<List<TreeNode>> buildAndCheckTree, Object[] ac,
			SyntacticAnalysisContextGenerator generator, SyntacticAnalysisSequenceValidator validator) {
		
		return this.bestSequences(1, characters, tags, words, poses, chunkTree,buildAndCheckTree,ac,-1000.0D,generator,validator);
	}

	/**
	 * 得到最好的结果
	 * @return
	 */
	@Override
	public String[] getOutcomes() {
		String[] outcomes = new String[this.model.getNumOutcomes()];

		for (int i = 0; i < this.model.getNumOutcomes(); ++i) {
			outcomes[i] = this.model.getOutcome(i);
		}

		return outcomes;
	}

}
