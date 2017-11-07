package com.wxw.tree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.wxw.sample.FileInputStreamFactory;
import com.wxw.sample.PlainTextByTreeStream;

import junit.framework.TestCase;

public class Tree2Action2TreeTest extends TestCase{

	public void testLoadTree() throws FileNotFoundException, IOException, CloneNotSupportedException{
		PlainTextByTreeStream lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File("data\\train\\wsj_0076.mrg")), "utf8");
		String txt = lineStream.read();
//		System.out.println(txt);

		PhraseGenerateTree pgt = new PhraseGenerateTree();
		TreeNode tree = pgt.generateTree(txt);

		TreeToActions tta = new TreeToActions();
		tta.treeToAction(tree);
		List<String> words = tta.getWords();
		List<String> actions = tta.getActions();

		ActionsToTree att = new ActionsToTree();
		TreeNode newTree = att.actionsToTree(words, actions);
		System.out.println(newTree.toString());
		assertEquals(tree, newTree);
	}	
}
