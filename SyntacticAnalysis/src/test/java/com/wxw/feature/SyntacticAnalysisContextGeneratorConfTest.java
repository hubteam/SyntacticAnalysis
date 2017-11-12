package com.wxw.feature;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.wxw.model.SyntacticAnalysisME;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.PhraseGenerateTree;
import com.wxw.tree.TreeNode;
import com.wxw.tree.TreeToActions;

import junit.framework.TestCase;

/**
 * 对特征生成类的测试
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisContextGeneratorConfTest extends TestCase{

	/**
	 * 对生成的特征进行测试
	 * @throws IOException
	 * @throws CloneNotSupportedException
	 */
	public void testFeature() throws IOException, CloneNotSupportedException{
		
		HashMap<String,Integer> dict = SyntacticAnalysisME.buildDictionary(new File("data\\tree\\train.txt"), "utf-8");
		FeatureForPosTools tools = new FeatureForPosTools(dict);

		PhraseGenerateTree pgt = new PhraseGenerateTree();
		TreeNode tree = pgt.generateTree("(S(NP(PRP I))(VP(VP(VBD saw)(NP(DT the)(NN man)))(PP(IN with)(NP(DT the)(NN board)))))");

		TreeToActions tta = new TreeToActions();
		SyntacticAnalysisSample sample = tta.treeToAction(tree);
		List<String> words = sample.getWords();
		List<String> poses = sample.getPoses();
		List<TreeNode> posTree = sample.getPosTree();
		List<TreeNode> chunkTree = sample.getChunkTree();
		List<List<TreeNode>> buildAndCheckTree = sample.getBuildAndCheckTree();
		List<String> actions = sample.getActions();
		String[][] ac = sample.getAdditionalContext();
		SyntacticAnalysisContextGenerator generator = new SyntacticAnalysisContextGeneratorConf();
		//词性标注的特征
		for (int i = 0; i < words.size(); i++) {
			String[] context = generator.getContextForPos(i,words, poses, ac);
			for (int j = 0; j < context.length; j++) {
				System.out.print(context[j]+" ");
			}
			System.out.println();
		}
		//chunk的特征
		for (int i = words.size(); i < 2*words.size(); i++) {		
			String[] context = generator.getContextForChunk(i-words.size(),chunkTree, actions, ac);
			for (int j = 0; j < context.length; j++) {
				System.out.print(context[j]+" ");
			}
			System.out.println();
		}
		//buildAndCheck
		//两个变量i j
		//i控制第几个list
		//j控制list中的第几个
		int j = 0;
		//计数变量
		int count = 0;
		for (int i = 2*words.size(); i < actions.size(); i=i+2) {

			if(actions.get(i).startsWith("join")){
				count++;
			}else if(actions.get(i).startsWith("start")){
				count = 0;
			}
			String[] buildContext = generator.getContextForBuild(j,buildAndCheckTree.get(i-2*words.size()), actions, ac);
		    for (int k = 0; k < buildContext.length; k++) {
				System.out.print(buildContext[k]+" ");
			}
		    System.out.println();
		    if(actions.get(i+1).equals("yes")){
		        j = j-count;
		        count = 0;
		        String[] checkContext = generator.getContextForCheck(j,buildAndCheckTree.get(i+1-2*words.size()), actions, ac);
			    for (int k = 0; k < checkContext.length; k++) {
					System.out.print(checkContext[k]);
				}
			    System.out.println();
		    }else if(actions.get(i+1).equals("no")){            	
		    	String[] checkContext = generator.getContextForCheck(j,buildAndCheckTree.get(i+1-2*words.size()), actions, ac);
			    for (int k = 0; k < checkContext.length; k++) {
					System.out.print(checkContext[k]);
				}
		    	j++;
		    	 System.out.println();
		    }
		}
	}
}
