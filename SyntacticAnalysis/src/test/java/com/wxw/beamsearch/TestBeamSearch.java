package com.wxw.beamsearch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.feature.SyntacticAnalysisContextGeneratorConf;
import com.wxw.feature.SyntacticAnalysisContextGeneratorConfForPos;
import com.wxw.feature.SyntacticAnalysisContextGeneratorForPos;
import com.wxw.model.bystep.SyntacticAnalysisMEForBuildAndCheck;
import com.wxw.model.bystep.SyntacticAnalysisMEForChunk;
import com.wxw.model.bystep.SyntacticAnalysisMEForPos;
import com.wxw.model.bystep.SyntacticAnalysisModelForBuildAndCheck;
import com.wxw.model.bystep.SyntacticAnalysisModelForChunk;
import com.wxw.tree.TreeNode;

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
		
//		String[] words = {"I","saw","the","man","with","the","telescope","."};
		String[] words = {"Mr.","Vinken","is","chairman","of","Elsevier","N.V.",",","the","Dutch","publishing","group","."};
//		String[] words = {"It","has","no","bearing","on","our","work","force","today","."};

		List<List<TreeNode>> posTree = postagger.tagKpos(5,words);
		List<List<TreeNode>> chunkTree = chunktagger.tagKChunk(3, posTree, null);
		for (List<TreeNode> list : chunkTree) {
			for (TreeNode treeNode : list) {
				System.out.print(treeNode+"  ");
			}
			System.out.println();
		}
		SyntacticAnalysisModelForBuildAndCheck buildandcheckmodel = SyntacticAnalysisMEForBuildAndCheck.readModel(new File(prop.getProperty("tree.corpus.buildmodeltxt.file")), 
				new File(prop.getProperty("tree.corpus.checkmodeltxt.file")), params, contextGen, "utf8");	
        SyntacticAnalysisMEForBuildAndCheck buildandchecktagger = new SyntacticAnalysisMEForBuildAndCheck(buildandcheckmodel,contextGen);
		List<List<TreeNode>> buildAndCheckTree = buildandchecktagger.tagBuildAndCheck(5, chunkTree, null);
		for (int i = 0; i < buildAndCheckTree.size(); i++) {
			System.out.println(buildAndCheckTree.get(i).get(0));
		}
//		TreeNode tree = buildAndCheckTree.get(0).get(0);
//		System.out.println(tree.toString());
	}
}
