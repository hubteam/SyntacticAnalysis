package com.wxw.sequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import org.hamcrest.core.CombinableMatcher.CombinableBothMatcher;

import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.tree.GenerateHeadWords;
import com.wxw.tree.TreeNode;

import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.util.Cache;

/**
 * 得到最好的K个结果的实现类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisBeamSearch implements SyntacticAnalysisSequenceClassificationModel{

	public static final String BEAM_SIZE_PARAMETER = "BeamSize";
	private static final Object[] EMPTY_ADDITIONAL_CONTEXT = new Object[0];
	protected int size;
	protected MaxentModel buildmodel;
	protected MaxentModel checkmodel;
	protected MaxentModel chunkmodel;
	private double[] buildprobs;
	private double[] checkprobs;
	private double[] chunkprobs;
	private Cache<String[], double[]> contextsCache;
	private static final int zeroLog = -100000;

	public SyntacticAnalysisBeamSearch(int size, MaxentModel buildmodel, MaxentModel checkmodel) {
		this(size, buildmodel, checkmodel, 0);
	}

	public SyntacticAnalysisBeamSearch(int size, MaxentModel buildmodel, MaxentModel checkmodel, int cacheSize) {
		this.size = size;
		this.buildmodel = buildmodel;
		this.checkmodel = checkmodel;
		if (cacheSize > 0) {
			this.contextsCache = new Cache(cacheSize);
		}

		this.buildprobs = new double[buildmodel.getNumOutcomes()];
		this.checkprobs = new double[checkmodel.getNumOutcomes()];
	}
	
	public SyntacticAnalysisBeamSearch(int size, MaxentModel chunkmodel) {
		this(size, chunkmodel, 0);
	}

	public SyntacticAnalysisBeamSearch(int size, MaxentModel chunkmodel, int cacheSize) {
		this.size = size;
		this.chunkmodel = chunkmodel;
		if (cacheSize > 0) {
			this.contextsCache = new Cache(cacheSize);
		}

		this.chunkprobs = new double[chunkmodel.getNumOutcomes()];
	}
	
	/**
	 * 得到最好的结果
	 * @param posTree pos步得到的最好的K棵树
	 * @param ac 额外的信息
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	@Override
	public SyntacticAnalysisSequenceForChunk bestSequenceForChunk( List<List<TreeNode>> posTree, Object[] ac,
			SyntacticAnalysisContextGenerator generator, SyntacticAnalysisSequenceValidator validator) {
		SyntacticAnalysisSequenceForChunk[] sequences = this.bestSequencesForChunk(1,posTree,ac,generator,validator);
		return sequences.length > 0 ? sequences[0] : null;
	}

	/**
	 * 得到最好的num个结果
	 * @param num 最好的num个序列
	 * @param posTree pos步得到的最好的K棵
	 * @param ac 额外的信息
	 * @param minSequenceScore 得分最低的限制
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	@Override
	public SyntacticAnalysisSequenceForChunk[] bestSequencesForChunk(int num,List<List<TreeNode>> posTree, Object[] ac,
			double minSequenceScore, SyntacticAnalysisContextGenerator generator, SyntacticAnalysisSequenceValidator validator) {
		//用于存放输入的K个结果中每一个得到的K个结果
		PriorityQueue<SyntacticAnalysisSequenceForChunk> kRes = new PriorityQueue<>(this.size);
		//遍历posTree中的K个结果
		for (int i = 0; i < posTree.size(); i++) {
			PriorityQueue<SyntacticAnalysisSequenceForChunk> prev = new PriorityQueue<>(this.size);
			PriorityQueue<SyntacticAnalysisSequenceForChunk> next = new PriorityQueue<>(this.size);
			prev.add(new SyntacticAnalysisSequenceForChunk());
			if (ac == null) {
				ac = EMPTY_ADDITIONAL_CONTEXT;
			}
			
			int numSeq;
			int seqIndex;
			for (numSeq = 0; numSeq < posTree.get(i).size(); ++numSeq) {//遍历其中的序列的长度
				//前一个结果如果小于beam size的大小，就取前一个结果的大小
				//如果前一个结果大于beam size的大小，就取beam size 的大小
				int topSequences = Math.min(this.size, prev.size());
				//遍历前面的结果的所有可能
				for (seqIndex = 0; prev.size() > 0 && seqIndex < topSequences; ++seqIndex) {
					SyntacticAnalysisSequenceForChunk top = prev.remove();//取出beam size个结果中的第一个
					List<String> tmpOutcomes = top.getOutcomes();//取出beam size个结果中的第一个中的结果序列
					String[] contexts = generator.getContextForChunkForTest(numSeq, posTree.get(i), tmpOutcomes, ac);
					double[] scores;
					//得到每个类别的分数
					if (this.contextsCache != null) {
						scores = (double[]) this.contextsCache.get(contexts);
						if (scores == null) {
							scores = this.chunkmodel.eval(contexts, this.chunkprobs);
							this.contextsCache.put(contexts, scores);
						}
					} else {
						scores = this.chunkmodel.eval(contexts, this.chunkprobs);
					}
					//temp_scores的作用就是取出第前beam size个的分数的界限
					double[] temp_scores = new double[scores.length];
					System.arraycopy(scores, 0, temp_scores, 0, scores.length);//数组的复制
					Arrays.sort(temp_scores);//排序
					//取beam size位置的值，保证取的分数值在前beam size个
					double min = temp_scores[Math.max(0, scores.length-this.size)];
					
					int p;
					String out;
					SyntacticAnalysisSequenceForChunk ns = null;
					for (p = 0; p < scores.length; ++p) {
						if(scores[p] >= min){
							out = this.chunkmodel.getOutcome(p);
							if(validator.validSequenceForChunk(numSeq,posTree.get(i),tmpOutcomes,out)){
								ns = new SyntacticAnalysisSequenceForChunk(top,out,scores[p],i);
								if(ns.getScore() > minSequenceScore){
									next.add(ns);
								}
							}
						}
					}
					if(next.size() == 0){
						for (p = 0; p < scores.length; ++p) {	
							out = this.chunkmodel.getOutcome(p);
							if(validator.validSequenceForChunk(numSeq,posTree.get(i),tmpOutcomes,out)){
								ns = new SyntacticAnalysisSequenceForChunk(top,out,scores[p],i);
								if(ns.getScore() > minSequenceScore){
									next.add(ns);
								}
							}						
						}
					}
				}
				prev.clear();
				PriorityQueue<SyntacticAnalysisSequenceForChunk> tmp = prev;
				prev = next;
				next = tmp;
			}
			for (seqIndex = 0; seqIndex < prev.size(); ++seqIndex) {
				kRes.add(prev.remove());
			}
		}
		SyntacticAnalysisSequenceForChunk[] result = new SyntacticAnalysisSequenceForChunk[num];
		for (int j = 0; j < num; j++) {
			result[j] = kRes.remove();
		}
		return result;
	}

	/**
	 * 得到最好的num个结果
	 * @param num 最好的num个序列
	 * @param posTree pos步得到的最好的K棵
	 * @param ac 额外的信息
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	@Override
	public SyntacticAnalysisSequenceForChunk[] bestSequencesForChunk(int num,List<List<TreeNode>> posTree, Object[] ac,
			SyntacticAnalysisContextGenerator generator, SyntacticAnalysisSequenceValidator validator) {
		
		return this.bestSequencesForChunk(num, posTree, ac, -1000.0D ,generator,validator);
	}

	/**
	 * 得到所有的结果
	 * @return
	 */
	@Override
	public String[] getOutcomes() {
//		String[] outcomes = new String[this.model.getNumOutcomes()];
//
//		for (int i = 0; i < this.model.getNumOutcomes(); ++i) {
//			outcomes[i] = this.model.getOutcome(i);
//		}
//
//		return outcomes;
		return null;
	}

	/**
	 * 得到最好的BuildAndCheck结果
	 * @param comnineChunkTree chunk步得到的最好的K棵树合并之后
	 * @param ac 额外的信息
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	@Override
	public SyntacticAnalysisSequenceForBuildAndCheck bestSequenceForBuildAndCheck(List<List<TreeNode>> comnineChunkTree, Object[] ac,
			SyntacticAnalysisContextGenerator generator, SyntacticAnalysisSequenceValidator validator) {
		SyntacticAnalysisSequenceForBuildAndCheck[] sequences = this.bestSequencesForBuildAndCheck(1,comnineChunkTree,ac,generator,validator);
		return sequences.length > 0 ? sequences[0] : null;
	}

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
	@Override
	public SyntacticAnalysisSequenceForBuildAndCheck[] bestSequencesForBuildAndCheck(int num, List<List<TreeNode>> comnineChunkTree,
			Object[] ac, double minSequenceScore, SyntacticAnalysisContextGenerator generator,
			SyntacticAnalysisSequenceValidator validator) {
		PriorityQueue<SyntacticAnalysisSequenceForBuildAndCheck> kRes = new PriorityQueue<>(this.size);
		//遍历K个结果
		for (int i = 0; i < comnineChunkTree.size(); i++) {
			PriorityQueue<SyntacticAnalysisSequenceForBuildAndCheck> prev = new PriorityQueue<>(this.size);
			PriorityQueue<SyntacticAnalysisSequenceForBuildAndCheck> next = new PriorityQueue<>(this.size);
			prev.add(new SyntacticAnalysisSequenceForBuildAndCheck(comnineChunkTree.get(i)));
			if (ac == null) {
				ac = EMPTY_ADDITIONAL_CONTEXT;
			}
			int numSeq;
			int seqIndex;
			int topSequences = Math.min(this.size, prev.size());
			int continueCount = 0;
			boolean flagAll = false;
			boolean flag = false;
			//遍历前topSequences个序列
			for (seqIndex = 0; prev.size() > 0 && seqIndex < topSequences; ++seqIndex) {
				
				if(prev.peek().getTree().size() != 1){//不是一颗完整的树的时候需要处理
					continueCount = 0;
					flag = false;
					flagAll = false;
					SyntacticAnalysisSequenceForBuildAndCheck top = prev.remove();//取出beam size个结果中的第一个
					List<String> tmpOutcomes = top.getOutcomes();//取出beam size个结果中的第一个中的结果序列
					List<String> temOutcomescheck = top.getOutcomesCheck();
					double temScore = top.getScore();
					numSeq = top.getBegin();//要处理的树的编号
					String[] contextsForBuild = generator.getContextForBuildForTest(numSeq, top.getTree(), tmpOutcomes, ac);
//					for (int j = 0; j < contextsForBuild.length; j++) {
//						System.out.println(contextsForBuild[j]);
//					}
					
					double[] scoresForBuild;
					//得到每个类别的分数
					if (this.contextsCache != null) {
						scoresForBuild = (double[]) this.contextsCache.get(contextsForBuild);
						if (scoresForBuild == null) {
							scoresForBuild = this.buildmodel.eval(contextsForBuild, this.buildprobs);
							this.contextsCache.put(contextsForBuild, scoresForBuild);
						}
					} else {
						scoresForBuild = this.buildmodel.eval(contextsForBuild, this.buildprobs);
					}
					//temp_scores的作用就是取出第前beam size个的分数的界限
					double[] temp_scoresscoresForBuild = new double[scoresForBuild.length];
					System.arraycopy(scoresForBuild, 0, temp_scoresscoresForBuild, 0, scoresForBuild.length);//数组的复制
					Arrays.sort(temp_scoresscoresForBuild);//排序
					//取beam size位置的值，保证取的分数值在前beam size个
					double min = temp_scoresscoresForBuild[Math.max(0, scoresForBuild.length-this.size)];
					
					int p;
					String out;
					SyntacticAnalysisSequenceForBuildAndCheck ns = null;
					for (p = 0; p < scoresForBuild.length; ++p) {
						if(scoresForBuild[p] >= min){
							
							out = this.buildmodel.getOutcome(p);
							if(validator.validSequenceForBuildAndCheck(numSeq,top.getTree(),tmpOutcomes,temOutcomescheck,out)){
								
								List<TreeNode> copy = new ArrayList<>(top.getTree());
								for (int j = 0; j < copy.size(); j++) {
									System.out.print(copy.get(j)+"   ");
								}
								System.out.println();
								System.out.println(seqIndex+":"+topSequences);
								System.out.println(out);
								String[] contextsForCheck = generator.getContextForCheckForTest(numSeq, top.getTree(), tmpOutcomes, out, ac);
//								for (int j = 0; j < contextsForCheck.length; j++) {
//									System.out.println(contextsForCheck[j]);
//								}
								double[] scoresForCheck = this.checkmodel.eval(contextsForCheck);
								//排序
								double[] temp_scoresForCheck = new double[scoresForCheck.length];
								System.arraycopy(scoresForCheck, 0, temp_scoresForCheck, 0, scoresForCheck.length);//数组的复制
								Arrays.sort(temp_scoresForCheck);//排序
								double minCheck = temp_scoresForCheck[Math.max(0, scoresForCheck.length-this.size)];
								//找到yes no的概率
								double yes = 0;
								double no = 0;
								for (int j = 0; j < scoresForCheck.length; j++) {
									if(scoresForCheck[j] >= minCheck){
										String outCheck = this.checkmodel.getOutcome(j);
										if(outCheck.equals("yes")){
											yes = scoresForCheck[j];
										}else if(outCheck.equals("no")){
											no = scoresForCheck[j];
										}
									}
								}
								System.out.println("yes:"+yes+"  "+"no:"+no);
								System.out.println();
								if(yes >= no){
									if(temScore + scoresForBuild[p] + yes> minSequenceScore){
										List<String>  tmpOutcomesCopy = new ArrayList<String>(tmpOutcomes);
										List<String> temOutcomescheckCopy = new ArrayList<String>(temOutcomescheck);
										tmpOutcomesCopy.add(out);
										temOutcomescheckCopy.add("yes");
										//新出的动作，加入树
										TreeNode outnode = new TreeNode(out);
										outnode.setFlag(true);
										outnode.addChild(copy.get(numSeq));
										outnode.setHeadWords(copy.get(numSeq).getHeadWords());
										copy.get(numSeq).setParent(outnode);
										copy.set(numSeq, outnode);
										
										int record = -1;
										//下面开始合并
										//如果标记为start就要合并
										if(out.split("_")[0].equals("start")){
											TreeNode combine = new TreeNode(out.split("_")[1]);
											combine.setFlag(true);
											combine.setHeadWords(copy.get(numSeq).getHeadWords());
											combine.addChild(copy.get(numSeq).getChildren().get(0));
											copy.get(numSeq).getChildren().get(0).setParent(combine);
											copy.set(numSeq, combine);
											ns = new SyntacticAnalysisSequenceForBuildAndCheck(top,copy, tmpOutcomesCopy,temOutcomescheckCopy,scoresForBuild[p],yes,numSeq,i);
											next.add(ns);
										}else {
											for (int k = numSeq-1;k >= 0; k--) {
												if(copy.get(k).getNodeName().split("_")[0].equals("start")){
													record = k;
												}
											}
											TreeNode combine = new TreeNode(out.split("_")[1]);
											
											combine.setFlag(true);
											for (int k = record; k <= numSeq; k++) {
												combine.addChild(copy.get(k).getChildren().get(0));
												copy.get(k).getChildren().get(0).setParent(combine);
											}
											//设置头结点
											combine.setHeadWords(GenerateHeadWords.getHeadWords(combine));
											copy.set(record,combine);
											//删除用于合并的那些位置上的
											for (int k = numSeq; k >= record+1; k--) {
												copy.remove(k);
											}
											numSeq = record - 1;
											ns = new SyntacticAnalysisSequenceForBuildAndCheck(top,copy, tmpOutcomes,temOutcomescheck,scoresForBuild[p],yes,numSeq,i);
											next.add(ns);
										}
									}
								}else if(yes < no){
									if(temScore + scoresForBuild[p] + no> minSequenceScore){
										List<String>  tmpOutcomesCopy = new ArrayList<String>(tmpOutcomes);
										List<String> temOutcomescheckCopy = new ArrayList<String>(temOutcomescheck);
										tmpOutcomesCopy.add(out);
										temOutcomescheckCopy.add("no");
										//为yes的时候要进行合并，合并的过程就是更改comnineChunkTree.get(i)
										TreeNode outnode = new TreeNode(out);
										outnode.setFlag(true);
										outnode.setHeadWords(copy.get(numSeq).getHeadWords());
										outnode.addChild(copy.get(numSeq));
										copy.get(numSeq).setParent(outnode);
										copy.set(numSeq, outnode);
										ns = new SyntacticAnalysisSequenceForBuildAndCheck(top,copy, tmpOutcomesCopy,temOutcomescheckCopy,scoresForBuild[p],no,numSeq+1,i);
										next.add(ns);
									}
								}
							}
						}
					}
				}else if(prev.peek().getTree().size() == 1){
					SyntacticAnalysisSequenceForBuildAndCheck n = new SyntacticAnalysisSequenceForBuildAndCheck(prev.peek());
					next.add(n);
					kRes.add(prev.peek());
					continueCount++;
					flag = true;
					flagAll = true;
					continue;
				}
				
				
				if(seqIndex == topSequences-1){
					if(continueCount == topSequences-1 && flagAll){
						break;
					}else if(flag && continueCount == num){
						break;
					}
					prev.clear();
					PriorityQueue<SyntacticAnalysisSequenceForBuildAndCheck> tmp = prev;
					prev = next;
					next = tmp;
					seqIndex = -1;
					topSequences = Math.min(this.size, prev.size());
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
				}
			}
		}
		return kRes.toArray(new SyntacticAnalysisSequenceForBuildAndCheck[kRes.size()]);
	}

	/**
	 * 得到最好的num个BuildAndCheck结果
	 * @param num 最好的num个序列
	 * @param comnineChunkTree chunk步得到的最好的K棵树合并之后
	 * @param ac 额外的信息
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	@Override
	public SyntacticAnalysisSequenceForBuildAndCheck[] bestSequencesForBuildAndCheck(int num, List<List<TreeNode>> comnineChunkTree,
			Object[] ac, SyntacticAnalysisContextGenerator generator, SyntacticAnalysisSequenceValidator validator) {
		return this.bestSequencesForBuildAndCheck(num, comnineChunkTree,ac,-1000.0D,generator,validator);
	}


}
