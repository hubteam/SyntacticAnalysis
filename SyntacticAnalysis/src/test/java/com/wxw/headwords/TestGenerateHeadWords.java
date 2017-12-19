package com.wxw.headwords;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.wxw.actions.HeadTreeToActions;
import com.wxw.tree.PhraseGenerateHeadTree;
import com.wxw.tree.TreeNode;

/**
 * 测试生成头结点的方法
 * @author 王馨苇
 *
 */
public class TestGenerateHeadWords {

	private PhraseGenerateHeadTree pgt;
	private TreeNode tree1;
	private String result1;
	private TreeNode tree2;
	private String result2;
	
	@Before
	public void setUP() throws CloneNotSupportedException{
		pgt = new PhraseGenerateHeadTree();
		tree1 = pgt.generateTree("((S(NP(PRP I))(VP(VP(VBD saw)(NP(DT the)(NN man)))(PP(IN with)(NP(DT the)(NN telescope))))))");
		result1 = "(S{saw}(NP{I}(PRP{I} I))(VP{saw}(VP{saw}(VBD{saw} saw)(NP{man}(DT{the} the)(NN{man} man)))(PP{with}(IN{with} with)(NP{telescope}(DT{the} the)(NN{telescope} telescope)))))";
		tree2 = pgt.generateTree("((S(NP(EX There))(VP(VBZ is)(NP(DT no)(NN asbestos))(PP(IN in)(NP(PRP$ our)(NNS products)))(ADVP (RB now)))(. .)('' '') ))");
		result2 = "(S{is}(NP{There}(EX{There} There))(VP{is}(VBZ{is} is)(NP{asbestos}(DT{no} no)(NN{asbestos} asbestos))(PP{in}(IN{in} in)(NP{products}(PRP${our} our)(NNS{products} products)))(ADVP{now}(RB{now} now)))(.{.} .)(''{''} ''))";
	}
	
	@Test
	public void testGenerateHeadWords(){
		assertEquals(tree1.toString(),result1);
		assertEquals(tree2.toString(),result2);
	}
}
