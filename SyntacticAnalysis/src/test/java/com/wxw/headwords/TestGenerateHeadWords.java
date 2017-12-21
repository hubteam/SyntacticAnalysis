package com.wxw.headwords;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.wxw.tree.HeadTreeNode;
import com.wxw.tree.PhraseGenerateTree;
import com.wxw.tree.TreeNode;
import com.wxw.tree.TreeToHeadTree;

/**
 * 测试生成头结点的方法
 * @author 王馨苇
 *
 */
public class TestGenerateHeadWords {

	private PhraseGenerateTree pgt;
	private TreeToHeadTree ttht;
	private TreeNode tree1;
	private HeadTreeNode headTree1;
	private String result1;
	private TreeNode tree2;
	private HeadTreeNode headTree2;
	private String result2;
	
	@Before
	public void setUP() throws CloneNotSupportedException{
		pgt = new PhraseGenerateTree();
		ttht = new TreeToHeadTree();
		tree1 = pgt.generateTree("((S(NP(PRP I))(VP(VP(VBD saw)(NP(DT the)(NN man)))(PP(IN with)(NP(DT the)(NN telescope))))))");
		headTree1 = ttht.treeToHeadTree(tree1);
		result1 = "(S{saw}(NP{I}(PRP{I} I))(VP{saw}(VP{saw}(VBD{saw} saw)(NP{man}(DT{the} the)(NN{man} man)))(PP{with}(IN{with} with)(NP{telescope}(DT{the} the)(NN{telescope} telescope)))))";
		tree2 = pgt.generateTree("((S(NP(EX There))(VP(VBZ is)(NP(DT no)(NN asbestos))(PP(IN in)(NP(PRP$ our)(NNS products)))(ADVP (RB now)))(. .)('' '') ))");
		headTree2 = ttht.treeToHeadTree(tree2);
		result2 = "(S{is}(NP{There}(EX{There} There))(VP{is}(VBZ{is} is)(NP{asbestos}(DT{no} no)(NN{asbestos} asbestos))(PP{in}(IN{in} in)(NP{products}(PRP${our} our)(NNS{products} products)))(ADVP{now}(RB{now} now)))(.{.} .)(''{''} ''))";
	}
	
	@Test
	public void testGenerateHeadWords(){
		assertEquals(headTree1.toString(),result1);
		assertEquals(headTree2.toString(),result2);
	}
}
