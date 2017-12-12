package com.wxw.evaluate;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tool.EvaluationTools;
import com.wxw.tree.PhraseGenerateTree;
import com.wxw.tree.TreeNode;
import com.wxw.tree.TreeToNonTerminal;

/**
 * 测试将一颗完整的树转成 nonterminal begin end 的形式
 * @author 王馨苇
 *
 */
public class TestTreeToNonterminal {

	private PhraseGenerateTree pgt;
	private TreeNode tree1;
	private List<EvaluationTools> pre1;
	private List<EvaluationTools> result1;
	private TreeToNonTerminal ttn1;
	private TreeToNonTerminal ttn2;
	private TreeNode tree2;
	private List<EvaluationTools> result2;
	private List<EvaluationTools> pre2;

	@Before
	public void setUp(){
		pgt = new PhraseGenerateTree();
		ttn1 = new TreeToNonTerminal();
		ttn2 = new TreeToNonTerminal();
		result1 = new ArrayList<>();
		result2 = new ArrayList<>();
		tree1 = pgt.generateTree("((S(NP(PRP I))(VP(VP(VBD saw)(NP(DT the)(NN man)))(PP(IN with)(NP(DT the)(NN telescope))))))");
		tree2 = pgt.generateTree("((S(NP(EX There))(VP(VBZ is)(NP(DT no)(NN box))(PP(IN in)(NP(PRP$ our)(NNS box)))(ADVP (RB now)))(. .)('' '') ))");
		pre1 = ttn1.getTreeToNonterminal(tree1);
		pre2 = ttn2.getTreeToNonterminal(tree2);
		result1.add(new EvaluationTools("NP", 0, 1));
		result1.add(new EvaluationTools("NP", 2, 4));
		result1.add(new EvaluationTools("VP", 1, 4));
		result1.add(new EvaluationTools("NP", 5, 7));
		result1.add(new EvaluationTools("PP", 4, 7));
		result1.add(new EvaluationTools("VP", 1, 7));
		result1.add(new EvaluationTools("S", 0, 7));
		result2.add(new EvaluationTools("NP", 0,1));
		result2.add(new EvaluationTools("NP", 2, 4));
		result2.add(new EvaluationTools("NP", 5, 7));
		result2.add(new EvaluationTools("PP", 4, 7));
		result2.add(new EvaluationTools("ADVP", 7, 8));
		result2.add(new EvaluationTools("VP", 1, 8));
		result2.add(new EvaluationTools("S", 0, 10));
	}
	
	@Test
	public void testTreeToNonterminal(){
		assertEquals(pre1.toString(),result1.toString());
		assertEquals(pre2.toString(),result2.toString());
	}
}
