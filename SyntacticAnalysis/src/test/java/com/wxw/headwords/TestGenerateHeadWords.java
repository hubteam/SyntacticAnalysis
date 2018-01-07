package com.wxw.headwords;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.wxw.pretreatRun.TreePreTreatment;
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
		result1 = "(S{saw[VBD]}(NP{I[PRP]}(PRP{I[PRP]} I[0]))(VP{saw[VBD]}(VP{saw[VBD]}(VBD{saw[VBD]} saw[1])(NP{man[NN]}(DT{the[DT]} the[2])"
				+ "(NN{man[NN]} man[3])))(PP{with[IN]}(IN{with[IN]} with[4])(NP{telescope[NN]}(DT{the[DT]} the[5])(NN{telescope[NN]} telescope[6])))))";
		tree2 = pgt.generateTree("((S(NP(EX There))(VP(VBZ is)(NP(DT no)(NN asbestos))(PP(IN in)(NP(PRP$ our)(NNS products)))(ADVP (RB now)))(. .)('' '') ))");
		headTree2 = ttht.treeToHeadTree(tree2);
		result2 = "(S{is[VBZ]}(NP{There[EX]}(EX{There[EX]} There[0]))(VP{is[VBZ]}(VBZ{is[VBZ]} is[1])"
				+ "(NP{asbestos[NN]}(DT{no[DT]} no[2])(NN{asbestos[NN]} asbestos[3]))(PP{in[IN]}"
				+ "(IN{in[IN]} in[4])(NP{products[NNS]}(PRP${our[PRP$]} our[5])(NNS{products[NNS]} products[6])))"
				+ "(ADVP{now[RB]}(RB{now[RB]} now[7])))(.{.[.]} .[8])(''{''['']} ''[9]))";
	}
	
	@Test
	public void testGenerateHeadWords(){
		assertEquals(headTree1.toString(),result1);
		assertEquals(headTree2.toString(),result2);
	}
}
