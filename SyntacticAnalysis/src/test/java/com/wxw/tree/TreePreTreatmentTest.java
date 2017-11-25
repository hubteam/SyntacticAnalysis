package com.wxw.tree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import com.wxw.stream.FileInputStreamFactory;
import com.wxw.stream.PlainTextByTreeStream;

import junit.framework.TestCase;

/**
 * 预处理操作的测试
 * @author 王馨苇
 *
 */
public class TreePreTreatmentTest extends TestCase{

	public void testPreTreatment() throws UnsupportedOperationException, FileNotFoundException, IOException{
		PlainTextByTreeStream lineStream = null;
		PlainTextByTreeStream lineStream1 = null;
		PhraseGenerateTree pgt = new PhraseGenerateTree();	
		TreePreTreatment tpt = new TreePreTreatment();
		URL url1 = TreePreTreatmentTest.class.getClassLoader().getResource("com/wxw/test/wsj_0015.mrg");
		URL url2 = TreePreTreatmentTest.class.getClassLoader().getResource("com/wxw/test/wsj_0015new.mrg");
		lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File(url1.getFile())), "utf8");
		lineStream1 = new PlainTextByTreeStream(new FileInputStreamFactory(new File(url2.getFile())), "utf8");
		String tree = "";
		String tree1 = "";
		while((tree = lineStream.read()) != "" && (tree1 = lineStream1.read())!=""){
			String treeStr = tpt.format(tree);
			TreeNode node = pgt.generateTreeForPreTreatment(tree);
			TreeNode node1 = pgt.generateTreeForPreTreatment(tree1);
			//对树进行遍历
			tpt.travelTree(node);
			System.out.println(node.toString());
			System.out.println(node1.toString());
			assertEquals(node.toNewSample(), node1.toNewSample());
		}	
		lineStream.close();
	}
	
}
