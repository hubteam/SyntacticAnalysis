package com.wxw.beamsearch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.wxw.feature.FeatureForPosTools;
import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.feature.SyntacticAnalysisContextGeneratorConf;
import com.wxw.feature.SyntacticAnalysisContextGeneratorConfForPos;
import com.wxw.feature.SyntacticAnalysisContextGeneratorForPos;
import com.wxw.model.all.SyntacticAnalysisME;
import com.wxw.model.bystep.SyntacticAnalysisMEForBuildAndCheck;
import com.wxw.model.bystep.SyntacticAnalysisMEForChunk;
import com.wxw.model.bystep.SyntacticAnalysisModelForBuildAndCheck;
import com.wxw.model.bystep.SyntacticAnalysisModelForChunk;
import com.wxw.model.pos.unused.SyntacticAnalysisMEForPos;
import com.wxw.model.pos.unused.SyntacticAnalysisModelForPos;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.TreeNode;
import com.wxw.tree.TreeToActions;

import junit.framework.TestCase;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
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
		POSTaggerME postagger = new POSTaggerME(model);
		SyntacticAnalysisContextGeneratorForPos contextGenpos = new SyntacticAnalysisContextGeneratorConfForPos();
		
		String[] words = {"I","saw","the","man","with","the","telescope","."};
//		String[] words = {"Mr.","Vinken","is","chairman","of","Elsevier","N.V.",",","the","Dutch","publishing","group","."};
//		String[] words = {"It","has","no","bearing","on","our","work","force","today","."};

		String[][] poses = postagger.tag(5, words);
		
		List<List<TreeNode>> posTree = SyntacticAnalysisSample.toPosTree(words, poses);
		
		List<List<TreeNode>> chunkTree = chunktagger.tagChunk(10, posTree, null);
		TreeToActions tta = new TreeToActions();
		List<List<TreeNode>> combineChunk = new ArrayList<List<TreeNode>>();
		
		for (int i = 0; i < chunkTree.size(); i++) {
			List<TreeNode> tempnode = tta.combine(chunkTree.get(i));
			combineChunk.add(tempnode);
		}
		SyntacticAnalysisModelForBuildAndCheck buildandcheckmodel = SyntacticAnalysisMEForBuildAndCheck.readModel(new File(prop.getProperty("tree.corpus.buildmodeltxt.file")), 
				new File(prop.getProperty("tree.corpus.checkmodeltxt.file")), params, contextGen, "utf8");	
        SyntacticAnalysisMEForBuildAndCheck buildandchecktagger = new SyntacticAnalysisMEForBuildAndCheck(buildandcheckmodel,contextGen);
		List<List<TreeNode>> buildAndCheckTree = buildandchecktagger.tagBuildAndCheck(5, combineChunk, null);
		TreeNode tree = buildAndCheckTree.get(0).get(0);
		System.out.println(tree.toString());
	}
}
