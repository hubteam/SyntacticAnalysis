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
	private TreeNode treeRef1;
	private TreeNode treePre1;
	private SyntacticAnalysisMeasure measure1;
	private TreeNode treeRef2;
	private TreeNode treePre2;
	private SyntacticAnalysisMeasure measure2;
	
	@Before
	public void setUp() throws CloneNotSupportedException{
		pgt = new PhraseGenerateTree();
		measure1 = new SyntacticAnalysisMeasure();
		//两棵树不同
		treeRef1 = pgt.generateTree("((S(NP(NN Measuring)(NNS cups))(VP(MD may)(ADVP(RB soon))(VP(VB be)(VP(VBN replaced)(PP(IN by)(NP(NNS tablespoons)))(PP(IN in)(NP(DT the)(NN laundry)(NN room))))))(. .)))");
		treePre1 = pgt.generateTree("((S(NP(VBG Measuring)(NNS cups))(VP(MD may)(ADVP(RB soon))(VP(VB be)(VP(VBN replaced)(PP(IN by)(NP(NP(NNS tablespoons))(PP(IN in)(NP(DT the)(NN laundry)(NN room))))))))(. .)))");
		measure1.update(treeRef1, treePre1);
		
		measure2 = new SyntacticAnalysisMeasure();	
		//两颗树相同的
		treeRef2 = pgt.generateTree("((S(NP(PRP I))(VP(VP(VBD saw)(NP(DT the)(NN man)))(PP(IN with)(NP(DT the)(NN telescope))))))");
		treePre2 = pgt.generateTree("((S(NP(PRP I))(VP(VP(VBD saw)(NP(DT the)(NN man)))(PP(IN with)(NP(DT the)(NN telescope))))))");
		measure2.update(treeRef2, treePre2);
	}
	
	@Test
	public void test(){
		assertEquals(measure1.getPrecisionScore(),0.8181,0.001);
		assertEquals(measure1.getRecallScore(),0.9,0.001);
		assertEquals(measure1.getMeasure(),0.8570,0.001);
		assertEquals(measure1.getCBs(),1,0.001);
		assertEquals(measure1.getCBs_0(),0,0.001);
		assertEquals(measure1.getCBs_2(),1,0.001);
		assertEquals(measure1.getSentenceAccuracy(),0,0.001);
		
		assertEquals(measure2.getPrecisionScore(),1.0,0.001);
		assertEquals(measure2.getRecallScore(),1.0,0.001);
		assertEquals(measure2.getMeasure(),1.0,0.001);
		assertEquals(measure2.getCBs(),0,0.001);
		assertEquals(measure2.getCBs_0(),1,0.001);
		assertEquals(measure2.getCBs_2(),1,0.001);
		assertEquals(measure2.getSentenceAccuracy(),1,0.001);
	}
}
