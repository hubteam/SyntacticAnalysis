package com.wxw.evaluate;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.wxw.evalstructure.EvalStructure;
import com.wxw.tree.PhraseGenerateHeadTree;
import com.wxw.tree.TreeNode;

/**
 * 测试计算指标是否正确
 * @author 王馨苇
 *
 */
public class TestEvaluateCount {

	private PhraseGenerateHeadTree pgt;
	private TreeNode treeRef;
	private List<EvalStructure> etRef;
	private TreeNode treePre;
	private List<EvalStructure> etPre;
	private SyntacticAnalysisMeasure measure;
	
	@Before
	public void setUp(){
		measure = new SyntacticAnalysisMeasure();
		pgt = new PhraseGenerateHeadTree();
		treeRef = pgt.generateTree("((S(NP(NN Measuring)(NNS cups))(VP(MD may)(ADVP(RB soon))(VP(VB be)(VP(VBN replaced)(PP(IN by)(NP(NNS tablespoons)))(PP(IN in)(NP(DT the)(NN laundry)(NN room))))))(. .)))");
		treePre = pgt.generateTree("((S(NP(VBG Measuring)(NNS cups))(VP(MD may)(ADVP(RB soon))(VP(VB be)(VP(VBN replaced)(PP(IN by)(NP(NP(NNS tablespoons))(PP(IN in)(NP(DT the)(NN laundry)(NN room))))))))(. .)))");
		measure.update(treeRef, treePre);
	}
	
	@Test
	public void test(){
		assertEquals(measure.getPrecisionScore(),0.8181,0.001);
		assertEquals(measure.getRecallScore(),0.9,0.001);
		assertEquals(measure.getMeasure(),0.8570,0.001);
	}
}
