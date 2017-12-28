package com.wxw.beamsearch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.feature.SyntacticAnalysisContextGeneratorConf;
import com.wxw.modelBystep.POSTaggerMEExtend;
import com.wxw.modelBystep.SyntacticAnalysisMEForBuildAndCheck;
import com.wxw.modelBystep.SyntacticAnalysisMEForChunk;
import com.wxw.modelBystep.SyntacticAnalysisModelForBuildAndCheck;
import com.wxw.modelBystep.SyntacticAnalysisModelForChunk;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.HeadTreeNode;
import com.wxw.tree.HeadTreeToActions;
import com.wxw.tree.PhraseGenerateTree;
import com.wxw.tree.TreeNode;
import com.wxw.tree.TreeToHeadTree;

import junit.framework.TestCase;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.util.TrainingParameters;

/**
 * 解码测试类
 * @author 王馨苇
 *
 */
public class TestBeamSearch extends TestCase{

	/**
	 * 测试能不能解码成一颗树
	 * @throws IOException
	 * @throws CloneNotSupportedException
	 */
	public void testBeamSearch() throws IOException, CloneNotSupportedException{
		TrainingParameters params = TrainingParameters.defaultParams();
		params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(3));
		Properties prop = new Properties();
		InputStream is = TestBeamSearch.class.getClassLoader().getResourceAsStream("com/wxw/run/corpus.properties");
		prop.load(is);
		SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen = new SyntacticAnalysisContextGeneratorConf();
		SyntacticAnalysisModelForChunk chunkmodel = new SyntacticAnalysisModelForChunk(new File(prop.getProperty("tree.corpus.chunkmodeltxt.file")));
        SyntacticAnalysisMEForChunk chunktagger = new SyntacticAnalysisMEForChunk(chunkmodel,contextGen);
		POSModel model = new POSModelLoader().load(new File(prop.getProperty("tree.corpus.posenglish.file")));
		POSTaggerMEExtend postagger = new POSTaggerMEExtend(model);		
//		String[] words = {"I","saw","the","man","with","the","telescope","."};//yes
//		String[] words = {"Mr.","Vinken","is","chairman","of","Elsevier","N.V.",",","the","Dutch","publishing","group","."};//no
//		String[] words = {"It","has","no","bearing","on","our","work","force","today","."};//yes
//      String[] words = {"The","average","seven-day","simple","yield","of","the","400","funds","was","8.12","%",",","down","from","8.14","%","."};//yes
//		String[] words = {"The","man","buys","fast","cars","with","big","tires","."};//yes
//		String[] words = {"Mr.","Allen","'s","Pittsburgh","firm",",","Advanced","Investment","Management","Inc.",",","executes","program","trades","for","institutions","."};//yes
//      String[] words = {"But","we","are","not","going","back","to","1970",".","''"};//yes
//      String[] words = {"$","130","million","of","general","obligation","distributable","state","aid","bonds","due","1991-2000","and","2009",",",
//        		"tentatively","priced","by","a","Chemical","Securities","Inc.","group","to","yield","from","6.20","%","in","1991",
//        		"to","7.272","%","in","2009"};
//      String[] words = {"4",".","buy","a","diamond","necklace","."};
//      String[] words = {"ASLACTON",",","England"};
//      String[] words = {"``","We","'ve","tried","to","train","the","youngsters",",","they","have","their","discos","and","their","dances",",","and","they","just","drift","away",".","''"};
//		String[] words = {"ENERGY",":"};
		String[] words = {"``","You","either","believe","Seymour","can","do","it","again","or","you","do","n't",".","''"};
//		String[] words = {"``","Who","'s","really","lying","?","''","asks","a","female","voice","."};
//		String[] words = {"Commonwealth","Edison","said","the","ruling","could","force","it","to","slash","its","1989","earnings","by","$","1.55","a","share","."};
//      String[] words = {"We","must","be","very","cautious","about","labeling","investors","as","``","long-term","''","or","``","short-term",".","''"};
		List<List<HeadTreeNode>> posTree = postagger.tagKpos(20,words);
		List<List<HeadTreeNode>> chunkTree = chunktagger.tagKChunk(20,posTree, null);
		SyntacticAnalysisModelForBuildAndCheck buildandcheckmodel = new SyntacticAnalysisModelForBuildAndCheck(new File(prop.getProperty("tree.corpus.buildmodeltxt.file")));
        SyntacticAnalysisMEForBuildAndCheck buildandchecktagger = new SyntacticAnalysisMEForBuildAndCheck(buildandcheckmodel,contextGen);
        HeadTreeNode buildAndCheckTree = buildandchecktagger.tagBuildAndCheck(chunkTree, null);
        if(buildAndCheckTree == null){
        	System.out.println("error");
        }else{
        	System.out.println(buildAndCheckTree);
     		HeadTreeToActions tta = new HeadTreeToActions();
     		PhraseGenerateTree pgt = new PhraseGenerateTree();
     		TreeToHeadTree ttht = new TreeToHeadTree();
     		String bra = buildAndCheckTree.toBracket();
     		TreeNode node = pgt.generateTree("("+bra+")");
     		HeadTreeNode headTree = ttht.treeToHeadTree(node);
     		SyntacticAnalysisSample<HeadTreeNode> samplePre = tta.treeToAction(headTree);
     		List<String> actionsPre = samplePre.getActions();
     		for (int i = 0; i < actionsPre.size(); i++) {
     			System.out.println(actionsPre.get(i));
     		}
        }   
	}
}
