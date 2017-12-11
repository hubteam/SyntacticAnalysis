package com.wxw.beamsearch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.feature.SyntacticAnalysisContextGeneratorConf;
import com.wxw.model.bystep.SyntacticAnalysisMEForBuildAndCheck;
import com.wxw.model.bystep.SyntacticAnalysisMEForChunk;
import com.wxw.model.bystep.SyntacticAnalysisMEForPos;
import com.wxw.model.bystep.SyntacticAnalysisModelForBuildAndCheck;
import com.wxw.model.bystep.SyntacticAnalysisModelForChunk;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.PhraseGenerateTree;
import com.wxw.tree.TreeNode;
import com.wxw.tree.TreeToActions;

import junit.framework.TestCase;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.util.TrainingParameters;

public class TestBeamSearch extends TestCase{

	public void testBeamSearch() throws IOException, CloneNotSupportedException{
		TrainingParameters params = TrainingParameters.defaultParams();
		params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(3));
		Properties prop = new Properties();
		InputStream is = TestBeamSearch.class.getClassLoader().getResourceAsStream("com/wxw/run/corpus.properties");
		prop.load(is);
		SyntacticAnalysisContextGenerator contextGen = new SyntacticAnalysisContextGeneratorConf();
		SyntacticAnalysisModelForChunk chunkmodel = SyntacticAnalysisMEForChunk.readModel(new File(prop.getProperty("tree.corpus.chunkmodeltxt.file")), params, contextGen, "utf8");	
        SyntacticAnalysisMEForChunk chunktagger = new SyntacticAnalysisMEForChunk(chunkmodel,contextGen);
		POSModel model = new POSModelLoader().load(new File(prop.getProperty("tree.corpus.posenglish.file")));
		SyntacticAnalysisMEForPos postagger = new SyntacticAnalysisMEForPos(model);
		
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
//		String[] words = {"``","You","either","believe","Seymour","can","do","it","again","or","you","do","n't",".","''"};
//		String[] words = {"``","Who","'s","really","lying","?","''","asks","a","female","voice","."};
		String[] words = {"Commonwealth","Edison","said","the","ruling","could","force","it","to","slash","its","1989","earnings","by","$","1.55","a","share","."};
        List<List<TreeNode>> posTree = postagger.tagKpos(20,words);
		List<List<TreeNode>> chunkTree = chunktagger.tagKChunk(20,posTree, null);
		SyntacticAnalysisModelForBuildAndCheck buildandcheckmodel = SyntacticAnalysisMEForBuildAndCheck.readModel(new File(prop.getProperty("tree.corpus.buildmodeltxt.file")), 
				new File(prop.getProperty("tree.corpus.checkmodeltxt.file")), params, contextGen, "utf8");	
        SyntacticAnalysisMEForBuildAndCheck buildandchecktagger = new SyntacticAnalysisMEForBuildAndCheck(buildandcheckmodel,contextGen);
        TreeNode buildAndCheckTree = buildandchecktagger.tagBuildAndCheck(chunkTree, null);
        if(buildAndCheckTree == null){
        	System.out.println("error");
        }else{
        	System.out.println(buildAndCheckTree);
     		TreeToActions tta = new TreeToActions();
     		PhraseGenerateTree pgt = new PhraseGenerateTree();
     		String bra = buildAndCheckTree.toBracket();
     		TreeNode node = pgt.generateTree("("+bra+")");
     		SyntacticAnalysisSample samplePre = tta.treeToAction(node);
     		List<String> actionsPre = samplePre.getActions();
     		for (int i = 0; i < actionsPre.size(); i++) {
     			System.out.println(actionsPre.get(i));
     		}
        }   
	}
}
