package com.wxw.evaluate;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.wxw.tree.PhraseGenerateTree;
import com.wxw.tree.TreeNode;

/**
 * 测试计算指标是否正确
 * @author 王馨苇
 *
 */
public class TestEvaluateCount {

	private PhraseGenerateTree pgt;
	private TreeNode treeRef;
	private TreeNode treePre;
	private SyntacticAnalysisMeasure measure;
	
	@Before
	public void setUp(){
		measure = new SyntacticAnalysisMeasure();
		pgt = new PhraseGenerateTree();
		treeRef = pgt.generateTree("((S(NP(NN Measuring)(NNS cups))(VP(MD may)(ADVP(RB soon))(VP(VB be)(VP(VBN replaced)(PP(IN by)(NP(NNS tablespoons)))(PP(IN in)(NP(DT the)(NN laundry)(NN room))))))(. .)))");
		treePre = pgt.generateTree("((S(NP(VBG Measuring)(NNS cups))(VP(MD may)(ADVP(RB soon))(VP(VB be)(VP(VBN replaced)(PP(IN by)(NP(NP(NNS tablespoons))(PP(IN in)(NP(DT the)(NN laundry)(NN room))))))))(. .)))");
		measure.update(treeRef, treePre);
	}
	
	@Test
	public void test(){
		assertEquals(measure.getPrecisionScore(),0.8181,0.001);
		assertEquals(measure.getRecallScore(),0.9,0.001);
		assertEquals(measure.getMeasure(),0.8570,0.001);
		assertEquals(measure.getCBs(),1,0.001);
		assertEquals(measure.getCBs_0(),0,0.001);
		assertEquals(measure.getCBs_2(),1,0.001);
		assertEquals(measure.getSentenceAccuracy(),0,0.001);
	}
}
