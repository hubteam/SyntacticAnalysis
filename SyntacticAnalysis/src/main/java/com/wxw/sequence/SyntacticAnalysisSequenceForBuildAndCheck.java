package com.wxw.sequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.wxw.tree.TreeNode;

public class SyntacticAnalysisSequenceForBuildAndCheck implements Comparable<SyntacticAnalysisSequenceForBuildAndCheck> {
	private double score;
	private double scorecheck;
	private List<String> outcomes;
	private List<String> outcomescheck;
	private List<Double> probs;
	private List<Double> probscheck;
	private List<TreeNode> tree;
	private int begin;
	private int lable;//标记是输入K个结果中的第几个
	private static final Double ONE = Double.valueOf(1.0D);

	public SyntacticAnalysisSequenceForBuildAndCheck() {
		this.outcomes = new ArrayList<>(1);
		this.outcomescheck = new ArrayList<>(1);
		this.probs = new ArrayList<>(1);
		this.probscheck = new ArrayList<>(1);
		this.tree = new ArrayList<>(1);
		this.score = 0.0D;
		this.scorecheck = 0.0D;
		this.begin = 0;
		this.lable = -1;
	}

	public SyntacticAnalysisSequenceForBuildAndCheck(List<TreeNode> tree){
		this.outcomes = new ArrayList<>(1);
		this.outcomescheck = new ArrayList<>(1);
		this.probs = new ArrayList<>(1);
		this.probscheck = new ArrayList<>(1);
		this.tree = tree;
		this.score = 0.0D;
		this.scorecheck = 0.0D;
		this.begin = 0;
		this.lable = -1;
	}
	
	public SyntacticAnalysisSequenceForBuildAndCheck(SyntacticAnalysisSequenceForBuildAndCheck s) {
		this.outcomes = new ArrayList<>(s.outcomes.size() + 1);
		this.outcomes.addAll(s.outcomes);
		this.outcomescheck = new ArrayList<>(s.outcomescheck.size() + 1);
		this.outcomescheck.addAll(s.outcomescheck);
		this.probs = new ArrayList<>(s.probs.size() + 1);
		this.probs.addAll(s.probs);
		this.probscheck = new ArrayList<>(s.probscheck.size() + 1);
		this.probscheck.addAll(s.probscheck);
		this.score = s.score;
		this.scorecheck = s.scorecheck;
		this.lable = s.lable;
		this.tree = s.tree;
		this.begin = s.begin;
	}

	public SyntacticAnalysisSequenceForBuildAndCheck(SyntacticAnalysisSequenceForBuildAndCheck s, String outcome,String checkoutcome, double p,double checkp, int lable) {
		this.outcomes = new ArrayList<>(s.outcomes.size() + 1);
		this.outcomes.addAll(s.outcomes);
		this.outcomes.add(outcome);
		this.outcomescheck = new ArrayList<>(s.outcomescheck.size() + 1);
		this.outcomescheck.addAll(s.outcomescheck);
		this.outcomescheck.add(checkoutcome);
		this.probs = new ArrayList<>(s.probs.size() + 1);
		this.probs.addAll(s.probs);
		this.probs.add(Double.valueOf(p));
		this.probscheck = new ArrayList<>(s.probscheck.size() + 1);
		this.probscheck.addAll(s.probscheck);
		this.probscheck.add(Double.valueOf(checkp));
		this.score = s.score + Math.log(p) + Math.log(checkp);
		this.lable = lable;
		this.tree = s.tree;
		this.begin = s.begin;
	}

	public SyntacticAnalysisSequenceForBuildAndCheck(List<TreeNode> tree, List<String> outcomes,List<String> outcomescheck, int begin, int lable) {
		this.tree = tree;
		this.outcomes = outcomes;
		this.outcomescheck = outcomescheck;
		this.probs = Collections.nCopies(outcomes.size(), ONE);
		this.probscheck = Collections.nCopies(outcomescheck.size(), ONE);
		this.begin = begin;
		this.lable = lable;
	}

	public SyntacticAnalysisSequenceForBuildAndCheck(SyntacticAnalysisSequenceForBuildAndCheck s,List<TreeNode> tree,List<String> outcomes,List<String> outcomescheck, double p, double pcheck, int begin, int lable) {
		this.outcomes = outcomes;
		this.outcomescheck = outcomescheck;
		this.tree = tree;
		this.probs = new ArrayList<>(s.probs.size() + 1);
		this.probs.addAll(s.probs);
		this.probs.add(Double.valueOf(p));
		this.probscheck = new ArrayList<>(s.probscheck.size() + 1);
		this.probscheck.addAll(s.probscheck);
		this.probscheck.add(Double.valueOf(pcheck));
		this.score = s.score + Math.log(p) + Math.log(pcheck);
		this.lable = lable;
		this.begin = begin;
	}
	
	public SyntacticAnalysisSequenceForBuildAndCheck(SyntacticAnalysisSequenceForBuildAndCheck top,
			List<TreeNode> tree2, List<String> tmpOutcomes, List<String> temOutcomescheck, double temScore, int index,
			int label) {
		
	}

	public int compareTo(SyntacticAnalysisSequenceForBuildAndCheck s) {
		return this.score < s.score ? 1 : (this.score > s.score ? -1 : 0);
	}

	public void add(String outcome, String outcomecheck, double p, double pcheck) {
		this.outcomes.add(outcome);
		this.outcomescheck.add(outcomecheck);
		this.probs.add(Double.valueOf(p));
		this.probscheck.add(Double.valueOf(pcheck));
		this.score += Math.log(p) + Math.log(pcheck);
	}

	public List<String> getOutcomes() {
		return this.outcomes;
	}

	public List<String> getOutcomesCheck(){
		return this.outcomescheck;
	}
	
	public double[] getProbs() {
		double[] ps = new double[this.probs.size()];
		this.getProbs(ps);
		return ps;
	}

	public double[] getProbsCheck() {
		double[] ps = new double[this.probscheck.size()];
		this.getProbsCheck(ps);
		return ps;
	}
	
	public double getScore() {
		return this.score;
	}

	public int getLabel(){
		return this.lable;
	}
	
	public List<TreeNode> getTree(){
		return this.tree;
	}
	
	public int getBegin(){
		return this.begin;
	}
	
	public void getProbs(double[] ps) {
		int pi = 0;
		for (int pl = this.probs.size(); pi < pl; ++pi) {
			ps[pi] = ((Double) this.probs.get(pi)).doubleValue();
		}
	}

	public void getProbsCheck(double[] ps){
		int pi = 0;
		for (int pl = this.probscheck.size(); pi < pl; ++pi) {
			ps[pi] = ((Double) this.probscheck.get(pi)).doubleValue();
		}
	}

	public String toString() {
		String str = "";
		for (int i = 0; i < this.tree.size(); i++) {
			str += this.tree.get(i).toString();
		}
		return str;
	}
}