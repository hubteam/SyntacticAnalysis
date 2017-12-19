package com.wxw.tree;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.wxw.actions.ActionsToHeadTree;
import com.wxw.actions.HeadTreeToActions;
import com.wxw.stream.FileInputStreamFactory;
import com.wxw.stream.PlainTextByTreeStream;
import com.wxw.stream.SyntacticAnalysisSample;

/**
 * 测试树到动作再到树是否合法
 * @author 王馨苇
 *
 */
public class Tree2Action2TreeTest{

	private URL is;
	private PlainTextByTreeStream lineStream ;
	private String txt ;
	private PhraseGenerateHeadTree pgt ;
	private HeadTreeNode tree ;

	private HeadTreeToActions tta ;
	private SyntacticAnalysisSample<HeadTreeNode> sample ;
	private List<String> words ;
	private List<String> actions ;

	private ActionsToHeadTree att ;
	private TreeNode newTree ;
	
	@Before
	public void setUP() throws UnsupportedOperationException, FileNotFoundException, IOException, CloneNotSupportedException{
		is = Tree2Action2TreeTest.class.getClassLoader().getResource("com/wxw/test/wsj_0076.mrg");
		lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File(is.getFile())), "utf8");
		txt = lineStream.read();
		pgt = new PhraseGenerateHeadTree();
		tree = pgt.generateTree(txt);

		tta = new HeadTreeToActions();
		sample = tta.treeToAction(tree);
		words = sample.getWords();
		actions = sample.getActions();

		att = new ActionsToHeadTree();
		newTree = att.actionsToTree(words, actions);
	}
	
	/**
	 * 测试由句法树到动作序列，再从动作序列到句法树的过程
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws CloneNotSupportedException
	 */
	@Test
	public void testLoadTree() throws FileNotFoundException, IOException, CloneNotSupportedException{
		assertEquals(tree, newTree);
	}	
	
}
