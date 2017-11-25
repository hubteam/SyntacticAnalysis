package com.wxw.tree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import com.wxw.stream.FileInputStreamFactory;
import com.wxw.stream.PlainTextByTreeStream;
import com.wxw.stream.SyntacticAnalysisSample;

import junit.framework.TestCase;

/**
 * 测试树到动作再到树是否合法
 * @author 王馨苇
 *
 */
public class Tree2Action2TreeTest extends TestCase{

	/**
	 * 测试由句法树到动作序列，再从动作序列到句法树的过程
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws CloneNotSupportedException
	 */
	public void testLoadTree() throws FileNotFoundException, IOException, CloneNotSupportedException{
		URL is = Tree2Action2TreeTest.class.getClassLoader().getResource("com/wxw/test/wsj_0076.mrg");
		PlainTextByTreeStream lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File(is.getFile())), "utf8");
		String txt = lineStream.read();
//		BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf8"));
		// 一次读取n行,jie
		
		PhraseGenerateTree pgt = new PhraseGenerateTree();
		TreeNode tree = pgt.generateTree(txt);

		TreeToActions tta = new TreeToActions();
		SyntacticAnalysisSample sample = tta.treeToAction(tree);
		List<String> words = sample.getWords();
		List<String> actions = sample.getActions();

		ActionsToTree att = new ActionsToTree();
		TreeNode newTree = att.actionsToTree(words, actions);
		System.out.println(newTree.toString());
		assertEquals(tree, newTree);
	}	
	
}
