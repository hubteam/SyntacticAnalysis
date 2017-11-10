package com.wxw.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.wxw.stream.FileInputStreamFactory;
import com.wxw.stream.PlainTextByTreeStream;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.PhraseGenerateTree;
import com.wxw.tree.TreeNode;
import com.wxw.tree.TreeToActions;

import opennlp.tools.util.PlainTextByLineStream;

/**
 * 训练模型
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisME {

	/**
	 * 统计词语出现的个数
	 * @param file 训练语料
	 * @param encoding 编码
	 * @return
	 * @throws IOException
	 * @throws CloneNotSupportedException 
	 */
	public static HashMap<String,Integer> buildDictionary(File file, String encoding) throws IOException, CloneNotSupportedException{
		HashMap<String,Integer> dict = new HashMap<String,Integer>();
		PlainTextByLineStream lineStream = new PlainTextByLineStream(new FileInputStreamFactory(file), "utf8");
		PhraseGenerateTree pgt = new PhraseGenerateTree();
		TreeToActions tta = new TreeToActions();
		String txt = "";
		while((txt = lineStream.read())!= null){
			TreeNode tree = pgt.generateTree(txt);
			SyntacticAnalysisSample sample = tta.treeToAction(tree);
			List<String> words = sample.getWords();
			for (int i = 0; i < words.size(); i++) {
				if(dict.containsKey(words.get(i))){
					Integer count = dict.get(words.get(i));
					count++;
					dict.put(words.get(i), count);
				}else{
					dict.put(words.get(i), 1);
				}
			}
		}
		lineStream.close();
		return dict;
	}
}
