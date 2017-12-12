package com.wxw.evaluate;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.wxw.tool.EvaluationTools;
import com.wxw.tree.PhraseGenerateTree;
import com.wxw.tree.TreeNode;
import com.wxw.tree.TreeToNonTerminal;

/**
 * 测试计算指标是否正确
 * @author 王馨苇
 *
 */
public class TestEvaluateCount {

	private PhraseGenerateTree pgt;
	private TreeNode treeRef;
	private List<EvaluationTools> etRef;
	private TreeToNonTerminal ttn1;
	private TreeToNonTerminal ttn2;
	private TreeNode treePre;
	private List<EvaluationTools> etPre;
	private SyntacticAnalysisMeasure measure;
	
	@Before
	public void setUp(){
		measure = new SyntacticAnalysisMeasure();
		pgt = new PhraseGenerateTree();
		ttn1 = new TreeToNonTerminal();
		ttn2 = new TreeToNonTerminal();
		treeRef = pgt.generateTree("((S(NP(NN Measuring)(NNS cups))(VP(MD may)(ADVP(RB soon))(VP(VB be)(VP(VBN replaced)(PP(IN by)(NP(NNS tablespoons)))(PP(IN in)(NP(DT the)(NN laundry)(NN room))))))(. .)))");
		treePre = pgt.generateTree("((S(NP(VBG Measuring)(NNS cups))(VP(MD may)(ADVP(RB soon))(VP(VB be)(VP(VBN replaced)(PP(IN by)(NP(NP(NNS tablespoons))(PP(IN in)(NP(DT the)(NN laundry)(NN room))))))))(. .)))");
		etRef = ttn1.getTreeToNonterminal(treeRef);
		etPre = ttn2.getTreeToNonterminal(treePre);
		measure.update(etRef, etPre);
	}
	
	@Test
	public void test(){
		assertEquals(measure.getPrecisionScore(),0.8181,0.001);
		assertEquals(measure.getRecallScore(),0.9,0.001);
		assertEquals(measure.getMeasure(),0.8570,0.001);
	}
}
