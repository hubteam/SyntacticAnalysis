package com.wxw.feature;

import java.io.IOException;
import java.util.List;

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
		
//		HashMap<String,Integer> dict = SyntacticAnalysisME.buildDictionary(new File("data\\tree\\train.txt"), "utf-8");
//		FeatureForPosTools tools = new FeatureForPosTools(dict);

		PhraseGenerateTree pgt = new PhraseGenerateTree();
		TreeNode tree = pgt.generateTree("(S(NP(PRP I))(VP(VP(VBD saw)(NP(DT the)(NN man)))(PP(IN with)(NP(DT the)(NN telescope)))))");

		TreeToActions tta = new TreeToActions();
		SyntacticAnalysisSample sample = tta.treeToAction(tree);
		List<String> words = sample.getWords();
//		for (String string : words) {
//			System.out.println(string);
//		}
		System.out.println(words.size());
		List<String> poses = sample.getPoses();
		List<TreeNode> posTree = sample.getPosTree();
		List<TreeNode> chunkTree = sample.getChunkTree();
//		for (TreeNode treeNode : chunkTree) {
//			System.out.println(treeNode);
//		}
		List<List<TreeNode>> buildAndCheckTree = sample.getBuildAndCheckTree();
		for (List<TreeNode> list : buildAndCheckTree) {
			for (TreeNode treeNode : list) {
				System.out.println(treeNode.toString());
			}
			System.out.println();
		}
		List<String> actions = sample.getActions();
		String[][] ac = sample.getAdditionalContext();
		SyntacticAnalysisContextGenerator generator = new SyntacticAnalysisContextGeneratorConf();
		//词性标注的特征
//		for (int i = 0; i < words.size(); i++) {
//			String[] context = generator.getContextForPos(i,words, poses, ac);
//			for (int j = 0; j < context.length; j++) {
//				System.out.print(context[j]+" ");
//			}
//			System.out.println();
//		}
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
		
		//build步骤
		int j = 0;
		for (int i = 2*words.size(); i < actions.size(); i=i+2) {
			String[] buildContext = generator.getContextForBuild(j,buildAndCheckTree.get(i-2*words.size()), actions, ac);
		    for (int k = 0; k < buildContext.length; k++) {
				System.out.print(buildContext[k]+" ");
			}
		    System.out.println();
		    if(actions.get(i+1).equals("yes")){  
		        int record = j-1;
		        for (int k = record; k >= 0; k--) {
		        	if(buildAndCheckTree.get(i-2*words.size()).get(k).getNodeName().split("_")[0].equals("start")){
		        		j = k;
		        		break;
		        	}
				}
//		        String[] checkContext = generator.getContextForCheck(j,buildAndCheckTree.get(i+1-2*words.size()), actions, ac);
//			    for (int k = 0; k < checkContext.length; k++) {
//					System.out.print(checkContext[k]);
//				}
//			    System.out.println();
		    }else if(actions.get(i+1).equals("no")){            	
//		    	String[] checkContext = generator.getContextForCheck(j,buildAndCheckTree.get(i+1-2*words.size()), actions, ac);
		    	j++;
//			    for (int k = 0; k < checkContext.length; k++) {
//					System.out.print(checkContext[k]);
//				}
//		    	System.out.println();
		    }
		}
		
		//check步骤
		j = 0;
		for (int i = 2*words.size(); i < actions.size(); i=i+2) {
//			String[] buildContext = generator.getContextForBuild(j,buildAndCheckTree.get(i-2*words.size()), actions, ac);
//		    for (int k = 0; k < buildContext.length; k++) {
//				System.out.print(buildContext[k]+" ");
//			}
//		    System.out.println();
		    if(actions.get(i+1).equals("yes")){  
		        int record = j-1;
		        for (int k = record; k >= 0; k--) {
		        	if(buildAndCheckTree.get(i-2*words.size()).get(k).getNodeName().split("_")[0].equals("start")){
		        		j = k;
		        		break;
		        	}
				}
		        String[] checkContext = generator.getContextForCheck(j,buildAndCheckTree.get(i+1-2*words.size()), actions, ac);
			    for (int k = 0; k < checkContext.length; k++) {
					System.out.print(checkContext[k]);
				}
			    System.out.println();
		    }else if(actions.get(i+1).equals("no")){            	
		    	String[] checkContext = generator.getContextForCheck(j,buildAndCheckTree.get(i+1-2*words.size()), actions, ac);
		    	j++;
			    for (int k = 0; k < checkContext.length; k++) {
					System.out.print(checkContext[k]);
				}
		    	System.out.println();
		    }
		}
	}
}
