package com.wxw.evaluate;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.wxw.evalstructure.EvalStructure;
import com.wxw.tree.PhraseGenerateTree;
import com.wxw.tree.TreeNode;
import com.wxw.tree.TreeToEvalStructure;

/**
 * 测试将一颗完整的树转成 nonterminal begin end 的形式
 * @author 王馨苇
 *
 */
public class TestTreeToNonterminalAndSpan {

	private PhraseGenerateTree pgt;
	private TreeNode tree1;
	private List<EvalStructure> pre1;
	private List<EvalStructure> result1;
	private TreeToEvalStructure ttn1;
	private TreeToEvalStructure ttn2;
	private TreeNode tree2;
	private List<EvalStructure> result2;
	private List<EvalStructure> pre2;

	@Before
	public void setUp(){
		pgt = new PhraseGenerateTree();
		ttn1 = new TreeToEvalStructure();
		ttn2 = new TreeToEvalStructure();
		result1 = new ArrayList<>();
		result2 = new ArrayList<>();
		tree1 = pgt.generateTree("((S(NP(PRP I))(VP(VP(VBD saw)(NP(DT the)(NN man)))(PP(IN with)(NP(DT the)(NN telescope))))))");
		tree2 = pgt.generateTree("((S(NP(EX There))(VP(VBZ is)(NP(DT no)(NN box))(PP(IN in)(NP(PRP$ our)(NNS box)))(ADVP (RB now)))(. .)('' '') ))");
		pre1 = ttn1.getNonterminalAndSpan(tree1);
		pre2 = ttn2.getNonterminalAndSpan(tree2);
		result1.add(new EvalStructure("NP", 0, 1));
		result1.add(new EvalStructure("NP", 2, 4));
		result1.add(new EvalStructure("VP", 1, 4));
		result1.add(new EvalStructure("NP", 5, 7));
		result1.add(new EvalStructure("PP", 4, 7));
		result1.add(new EvalStructure("VP", 1, 7));
		result1.add(new EvalStructure("S", 0, 7));
		result2.add(new EvalStructure("NP", 0,1));
		result2.add(new EvalStructure("NP", 2, 4));
		result2.add(new EvalStructure("NP", 5, 7));
		result2.add(new EvalStructure("PP", 4, 7));
		result2.add(new EvalStructure("ADVP", 7, 8));
		result2.add(new EvalStructure("VP", 1, 8));
		result2.add(new EvalStructure("S", 0, 10));
	}
	
	@Test
	public void testTreeToNonterminal(){
		assertEquals(pre1.toString(),result1.toString());
		assertEquals(pre2.toString(),result2.toString());
	}
}
