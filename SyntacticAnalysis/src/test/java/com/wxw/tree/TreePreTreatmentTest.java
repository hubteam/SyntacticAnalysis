package com.wxw.tree;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.wxw.pretreattools.TreePreTreatment;
import com.wxw.stream.FileInputStreamFactory;
import com.wxw.stream.PlainTextByTreeStream;

/**
 * 预处理操作的测试
 * @author 王馨苇
 *
 */
public class TreePreTreatmentTest{

	private PlainTextByTreeStream lineStream = null;
	private PlainTextByTreeStream lineStreamnew = null;
	private URL url1;
	private URL url2 ;
	private String tree = "";
	private PhraseGenerateTree pgt ;
	private String after = "";
	private String begin = "";
	private String line = "";
	
	@Before
	public void setUP() throws UnsupportedOperationException, FileNotFoundException, IOException{
		pgt = new PhraseGenerateTree();	
		url1 = TreePreTreatmentTest.class.getClassLoader().getResource("com/wxw/test/wsj_0015.mrg");
		url2 = TreePreTreatmentTest.class.getClassLoader().getResource("com/wxw/test/wsj_0015new.mrg");
		lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File(url1.getFile())), "utf8");
		lineStreamnew = new PlainTextByTreeStream(new FileInputStreamFactory(new File(url2.getFile())), "utf8");
	}
	
	/**
	 * 验证去除空节点后输出的带换行的括号表达式是否正确
	 * @throws UnsupportedOperationException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testPreTreatment() throws UnsupportedOperationException, FileNotFoundException, IOException{
		
		while((tree = lineStream.read()) != ""){
			TreeNode node = pgt.generateTree(tree);
			//对树进行遍历
			TreePreTreatment.travelTree(node);
			String newStr = node.toNoNoneSample();
			TreeNode newTree = pgt.generateTree("("+newStr+")");
			String oneTree = "";
			String[] str = ("("+TreeNode.printTree(newTree, 1)+")").split("\n");
			for (int i = 0; i < str.length; i++) {
				oneTree += str[i].trim();
			}
			begin += pgt.format(oneTree)+"\n";
		}
		while((line = lineStreamnew.read()) != ""){
			line = pgt.format(line)+"\n";
			after += line;
		}
		assertEquals(begin,after);
	}
	
	@After
	public void tearDown() throws IOException{
		lineStream.close();
		lineStreamnew.close();
	}
}
