package com.wxw.event;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.wxw.actions.HeadTreeToActions;
import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.feature.SyntacticAnalysisContextGeneratorConf;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.HeadTreeNode;
import com.wxw.tree.PhraseGenerateHeadTree;

import opennlp.tools.ml.model.Event;

/**
 * 测试事件的生成
 * @author 王馨苇
 *
 */
public class TestEvent {

	private PhraseGenerateHeadTree pgt;
	private HeadTreeNode tree;

	private HeadTreeToActions tta;
	private SyntacticAnalysisSample<HeadTreeNode> sample;
	private List<String> words;
	private List<HeadTreeNode> chunkTree;
	private List<List<HeadTreeNode>> buildAndCheckTree;
	private List<String> actions;
	private String[][] ac;
	private SyntacticAnalysisContextGenerator<HeadTreeNode> generator;
	private List<Event> events = new ArrayList<Event>();	
	private List<Event> event1 = new ArrayList<>();
	private List<Event> event2 = new ArrayList<>();
	private List<Event> event3 = new ArrayList<>();
	private List<Event> event4 = new ArrayList<>();
	private List<Event> event5 = new ArrayList<>();
	private List<Event> event6 = new ArrayList<>();
	private List<Event> event7 = new ArrayList<>();
	private List<Event> event8 = new ArrayList<>();
	private List<Event> event9 = new ArrayList<>();
	private List<Event> event10 = new ArrayList<>();
	private List<Event> event11 = new ArrayList<>();
	private List<Event> event12 = new ArrayList<>();
	private List<Event> event13 = new ArrayList<>();
	private List<Event> event14 = new ArrayList<>();
	private List<Event> event15 = new ArrayList<>();
	private List<Event> event16 = new ArrayList<>();
	private List<Event> event17 = new ArrayList<>();
	private List<Event> event18 = new ArrayList<>();
	private List<Event> event19 = new ArrayList<>();
	private List<Event> event20 = new ArrayList<>();
	private List<Event> event21 = new ArrayList<>();
	private List<Event> event22 = new ArrayList<>();
	private List<Event> event23 = new ArrayList<>();
	
	private String[] chunk0 = {"chunkandpostag0=PRP|I","chunkandpostag0*=PRP","chunkandpostag1=VBD|saw","chunkandpostag1*=VBD","chunkandpostag2=DT|the",
			"chunkandpostag2*=DT","chunkandpostag01=PRP|I;VBD|saw","chunkandpostag01*=PRP|I;VBD","chunkandpostag0*1=PRP;VBD|saw","chunkandpostag0*1*=PRP;VBD"};
	private String[] chunk1 = {"chunkandpostag0=VBD|saw","chunkandpostag0*=VBD","chunkandpostag_1=start_NP|PRP|I","chunkandpostag_1*=start_NP|PRP","chunkandpostag1=DT|the",
			"chunkandpostag1*=DT","chunkandpostag2=NN|man","chunkandpostag2*=NN","chunkandpostag_10=start_NP|PRP|I;VBD|saw","chunkandpostag_10*=start_NP|PRP|I;VBD",
			"chunkandpostag_1*0=start_NP|PRP;VBD|saw","chunkandpostag_1*0*=start_NP|PRP;VBD","chunkandpostag01=VBD|saw;DT|the",
			"chunkandpostag01*=VBD|saw;DT","chunkandpostag0*1=VBD;DT|the","chunkandpostag0*1*=VBD;DT"};
	private String[] chunk2 = {"chunkandpostag0=DT|the","chunkandpostag0*=DT","chunkandpostag_1=other|VBD|saw","chunkandpostag_1*=other|VBD","chunkandpostag_2=start_NP|PRP|I",
			"chunkandpostag_2*=start_NP|PRP","chunkandpostag1=NN|man","chunkandpostag1*=NN","chunkandpostag2=IN|with","chunkandpostag2*=IN",
			"chunkandpostag_10=other|VBD|saw;DT|the","chunkandpostag_10*=other|VBD|saw;DT","chunkandpostag_1*0=other|VBD;DT|the","chunkandpostag_1*0*=other|VBD;DT",
			"chunkandpostag01=DT|the;NN|man","chunkandpostag01*=DT|the;NN","chunkandpostag0*1=DT;NN|man","chunkandpostag0*1*=DT;NN"};
	private String[] chunk5 = {"chunkandpostag0=DT|the","chunkandpostag0*=DT","chunkandpostag_1=other|IN|with","chunkandpostag_1*=other|IN","chunkandpostag_2=join_NP|NN|man",
			"chunkandpostag_2*=join_NP|NN","chunkandpostag1=NN|telescope","chunkandpostag1*=NN","chunkandpostag_10=other|IN|with;DT|the","chunkandpostag_10*=other|IN|with;DT",
			"chunkandpostag_1*0=other|IN;DT|the","chunkandpostag_1*0*=other|IN;DT","chunkandpostag01=DT|the;NN|telescope",
			"chunkandpostag01*=DT|the;NN","chunkandpostag0*1=DT;NN|telescope","chunkandpostag0*1*=DT;NN"};
	private String[] chunk4 = {"chunkandpostag0=IN|with","chunkandpostag0*=IN","chunkandpostag_1=join_NP|NN|man","chunkandpostag_1*=join_NP|NN","chunkandpostag_2=start_NP|DT|the",
			"chunkandpostag_2*=start_NP|DT","chunkandpostag1=DT|the","chunkandpostag1*=DT","chunkandpostag2=NN|telescope","chunkandpostag2*=NN",
			"chunkandpostag_10=join_NP|NN|man;IN|with","chunkandpostag_10*=join_NP|NN|man;IN","chunkandpostag_1*0=join_NP|NN;IN|with","chunkandpostag_1*0*=join_NP|NN;IN",
			"chunkandpostag01=IN|with;DT|the","chunkandpostag01*=IN|with;DT","chunkandpostag0*1=IN;DT|the","chunkandpostag0*1*=IN;DT"};
	private String[] chunk3 = {"chunkandpostag0=NN|man","chunkandpostag0*=NN","chunkandpostag_1=start_NP|DT|the","chunkandpostag_1*=start_NP|DT",
			"chunkandpostag_2=other|VBD|saw","chunkandpostag_2*=other|VBD","chunkandpostag1=IN|with","chunkandpostag1*=IN",
			"chunkandpostag2=DT|the","chunkandpostag2*=DT",
			"chunkandpostag_10=start_NP|DT|the;NN|man","chunkandpostag_10*=start_NP|DT|the;NN","chunkandpostag_1*0=start_NP|DT;NN|man","chunkandpostag_1*0*=start_NP|DT;NN",
			"chunkandpostag01=NN|man;IN|with","chunkandpostag01*=NN|man;IN","chunkandpostag0*1=NN;IN|with","chunkandpostag0*1*=NN;IN"};
	private String[] chunk6 = {"chunkandpostag0=NN|telescope","chunkandpostag0*=NN","chunkandpostag_1=start_NP|DT|the","chunkandpostag_1*=start_NP|DT","chunkandpostag_2=other|IN|with",
			"chunkandpostag_2*=other|IN","chunkandpostag_10=start_NP|DT|the;NN|telescope","chunkandpostag_10*=start_NP|DT|the;NN","chunkandpostag_1*0=start_NP|DT;NN|telescope","chunkandpostag_1*0*=start_NP|DT;NN"};
	
	private String[] build0 = {"cons0=NP|I","cons0*=NP","cons1=VBD|saw","cons1*=VBD","cons2=NP|man","cons2*=NP",
			"cons01=NP|I;VBD|saw","cons01*=NP|I;VBD","cons0*1=NP;VBD|saw","cons0*1*=NP;VBD",
			"cons012=NP|I;VBD|saw;NP|man","cons01*2*=NP|I;VBD;NP","cons01*2=NP|I;VBD;NP|man","cons012*=NP|I;VBD|saw;NP","cons0*1*2*=NP;VBD;NP"};
	private String[] build1 = {"cons0=VBD|saw","cons0*=VBD","cons_1=start_S|NP|I","cons_1*=start_S|NP","cons1=NP|man","cons1*=NP",
			"cons2=IN|with","cons2*=IN","cons_10=start_S|NP|I;VBD|saw","cons_10*=start_S|NP|I;VBD","cons_1*0=start_S|NP;VBD|saw","cons_1*0*=start_S|NP;VBD",
			"cons01=VBD|saw;NP|man","cons01*=VBD|saw;NP","cons0*1=VBD;NP|man","cons0*1*=VBD;NP",
			"cons012=VBD|saw;NP|man;IN|with","cons01*2*=VBD|saw;NP;IN","cons01*2=VBD|saw;NP;IN|with","cons012*=VBD|saw;NP|man;IN","cons0*1*2*=VBD;NP;IN",
			"cons_101=start_S|NP|I;VBD|saw;NP|man","cons_1*01*=start_S|NP;VBD|saw;NP","cons_1*01=start_S|NP;VBD|saw;NP|man","cons_101*=start_S|NP|I;VBD|saw;NP","cons_1*0*1*=start_S|NP;VBD;NP"};
	private String[] build2 = {"cons0=NP|man","cons0*=NP","cons_1=start_VP|VBD|saw","cons_1*=start_VP|VBD","cons_2=start_S|NP|I","cons_2*=start_S|NP",
			"cons1=IN|with","cons1*=IN","cons2=NP|telescope","cons2*=NP",
			"cons_10=start_VP|VBD|saw;NP|man","cons_10*=start_VP|VBD|saw;NP","cons_1*0=start_VP|VBD;NP|man","cons_1*0*=start_VP|VBD;NP",
			"cons01=NP|man;IN|with","cons01*=NP|man;IN","cons0*1=NP;IN|with","cons0*1*=NP;IN",
			"cons_2_10=start_S|NP|I;start_VP|VBD|saw;NP|man","cons_2*_1*0=start_S|NP;start_VP|VBD;NP|man","cons_2*_10=start_S|NP;start_VP|VBD|saw;NP|man","cons_2_1*0=start_S|NP|I;start_VP|VBD;NP|man","cons_2*_1*0*=start_S|NP;start_VP|VBD;NP",
			"cons012=NP|man;IN|with;NP|telescope","cons01*2*=NP|man;IN;NP","cons01*2=NP|man;IN;NP|telescope","cons012*=NP|man;IN|with;NP","cons0*1*2*=NP;IN;NP",
			"cons_101=start_VP|VBD|saw;NP|man;IN|with","cons_1*01*=start_VP|VBD;NP|man;IN","cons_1*01=start_VP|VBD;NP|man;IN|with","cons_101*=start_VP|VBD|saw;NP|man;IN","cons_1*0*1*=start_VP|VBD;NP;IN"};
	private String[] build3 = {"cons0=VP|saw","cons0*=VP","cons_1=start_S|NP|I","cons_1*=start_S|NP","cons1=IN|with","cons1*=IN",
			"cons2=NP|telescope","cons2*=NP","cons_10=start_S|NP|I;VP|saw","cons_10*=start_S|NP|I;VP","cons_1*0=start_S|NP;VP|saw","cons_1*0*=start_S|NP;VP",
			"cons01=VP|saw;IN|with","cons01*=VP|saw;IN","cons0*1=VP;IN|with","cons0*1*=VP;IN",
			"cons012=VP|saw;IN|with;NP|telescope","cons01*2*=VP|saw;IN;NP","cons01*2=VP|saw;IN;NP|telescope","cons012*=VP|saw;IN|with;NP","cons0*1*2*=VP;IN;NP",
			"cons_101=start_S|NP|I;VP|saw;IN|with","cons_1*01*=start_S|NP;VP|saw;IN","cons_1*01=start_S|NP;VP|saw;IN|with","cons_101*=start_S|NP|I;VP|saw;IN","cons_1*0*1*=start_S|NP;VP;IN"};
	private String[] build4 = {"cons0=IN|with","cons0*=IN","cons_1=start_VP|VP|saw","cons_1*=start_VP|VP","cons_2=start_S|NP|I","cons_2*=start_S|NP",
			"cons1=NP|telescope","cons1*=NP","cons_10=start_VP|VP|saw;IN|with","cons_10*=start_VP|VP|saw;IN","cons_1*0=start_VP|VP;IN|with","cons_1*0*=start_VP|VP;IN",
			"cons01=IN|with;NP|telescope","cons01*=IN|with;NP","cons0*1=IN;NP|telescope","cons0*1*=IN;NP",
			"cons_2_10=start_S|NP|I;start_VP|VP|saw;IN|with","cons_2*_1*0=start_S|NP;start_VP|VP;IN|with","cons_2*_10=start_S|NP;start_VP|VP|saw;IN|with","cons_2_1*0=start_S|NP|I;start_VP|VP;IN|with","cons_2*_1*0*=start_S|NP;start_VP|VP;IN",
			"cons_101=start_VP|VP|saw;IN|with;NP|telescope","cons_1*01*=start_VP|VP;IN|with;NP","cons_1*01=start_VP|VP;IN|with;NP|telescope","cons_101*=start_VP|VP|saw;IN|with;NP","cons_1*0*1*=start_VP|VP;IN;NP"};
	private String[] build5 = {"cons0=NP|telescope","cons0*=NP","cons_1=start_PP|IN|with","cons_1*=start_PP|IN","cons_2=start_VP|VP|saw","cons_2*=start_VP|VP",
			"cons_10=start_PP|IN|with;NP|telescope","cons_10*=start_PP|IN|with;NP","cons_1*0=start_PP|IN;NP|telescope","cons_1*0*=start_PP|IN;NP","cons_2_10=start_VP|VP|saw;start_PP|IN|with;NP|telescope",
			"cons_2*_1*0=start_VP|VP;start_PP|IN;NP|telescope","cons_2*_10=start_VP|VP;start_PP|IN|with;NP|telescope","cons_2_1*0=start_VP|VP|saw;start_PP|IN;NP|telescope","cons_2*_1*0*=start_VP|VP;start_PP|IN;NP"};
	private String[] build6 = {"cons0=PP|with","cons0*=PP","cons_1=start_VP|VP|saw","cons_1*=start_VP|VP","cons_2=start_S|NP|I","cons_2*=start_S|NP",
			"cons_10=start_VP|VP|saw;PP|with","cons_10*=start_VP|VP|saw;PP","cons_1*0=start_VP|VP;PP|with","cons_1*0*=start_VP|VP;PP",
			"cons_2_10=start_S|NP|I;start_VP|VP|saw;PP|with","cons_2*_1*0=start_S|NP;start_VP|VP;PP|with","cons_2*_10=start_S|NP;start_VP|VP|saw;PP|with","cons_2_1*0=start_S|NP|I;start_VP|VP;PP|with","cons_2*_1*0*=start_S|NP;start_VP|VP;PP"};
	private String[] build7 = {"cons0=VP|saw","cons0*=VP","cons_1=start_S|NP|I","cons_1*=start_S|NP","cons_10=start_S|NP|I;VP|saw",
			"cons_10*=start_S|NP|I;VP","cons_1*0=start_S|NP;VP|saw","cons_1*0*=start_S|NP;VP"};
	
	private String[] check0 = {"checkcons_begin=S|NP|I","checkcons_begin*=S|NP","checkcons_last=S|NP|I","checkcons_last*=S|NP",
			"production=S→NP","surround1=VBD|saw","surround1*=VBD","surround2=DT|the;NN|man","surround2*=DT;NN"};
	private String[] check1 = {"checkcons_begin=VP|VBD|saw","checkcons_begin*=VP|VBD","checkcons_last=VP|VBD|saw","checkcons_last*=VP|VBD","production=VP→VBD",
			"surround_1=PRP|I","surround_1*=PRP","surround1=DT|the;NN|man","surround1*=DT;NN","surround2=IN|with","surround2*=IN"};
	private String[] check2 = {"checkcons_begin=VP|VBD|saw","checkcons_begin*=VP|VBD","checkcons_last=VP|NP|man","checkcons_last*=VP|NP","checkcons_0last=VP|VBD|saw;VP|NP|man",
			"production=VP→VBD,NP","surround_1=PRP|I","surround_1*=PRP","surround1=IN|with","surround1*=IN",
			"surround2=DT|the;NN|telescope","surround2*=DT;NN"};
	private String[] check3 = {"checkcons_begin=VP|VP|saw","checkcons_begin*=VP|VP","checkcons_last=VP|VP|saw","checkcons_last*=VP|VP","production=VP→VP",
			"surround_1=PRP|I","surround_1*=PRP","surround1=IN|with","surround1*=IN","surround2=DT|the;NN|telescope","surround2*=DT;NN"};
	private String[] check4 = {"checkcons_begin=PP|IN|with","checkcons_begin*=PP|IN","checkcons_last=PP|IN|with","checkcons_last*=PP|IN","production=PP→IN",
			"surround_1=VBD|saw;DT|the;NN|man","surround_1*=VBD;DT;NN","surround_2=PRP|I","surround_2*=PRP","surround1=DT|the;NN|telescope","surround1*=DT;NN"};
	private String[] check5 = {"checkcons_begin=PP|IN|with","checkcons_begin*=PP|IN","checkcons_last=PP|NP|telescope","checkcons_last*=PP|NP",
			"checkcons_0last=PP|IN|with;PP|NP|telescope","production=PP→IN,NP","surround_1=VBD|saw;DT|the;NN|man","surround_1*=VBD;DT;NN",
			"surround_2=PRP|I","surround_2*=PRP"};
	private String[] check6 = {"checkcons_begin=VP|VP|saw","checkcons_begin*=VP|VP","checkcons_last=VP|PP|with","checkcons_last*=VP|PP",
			"checkcons_0last=VP|VP|saw;VP|PP|with","production=VP→VP,PP","surround_1=PRP|I","surround_1*=PRP"};
	private String[] check7 = {"checkcons_begin=S|NP|I","checkcons_begin*=S|NP","checkcons_last=S|VP|saw","checkcons_last*=S|VP","checkcons_0last=S|NP|I;S|VP|saw",
			"production=S→NP,VP"};
	
	@Before
	public void setUp() throws CloneNotSupportedException, IOException{
		pgt = new PhraseGenerateHeadTree();
		tree = pgt.generateTree("((S(NP(PRP I))(VP(VP(VBD saw)(NP(DT the)(NN man)))(PP(IN with)(NP(DT the)(NN telescope))))))");
		tta = new HeadTreeToActions();
		sample = tta.treeToAction(tree);
		words = sample.getWords();
		chunkTree = sample.getChunkTree();
		buildAndCheckTree = sample.getBuildAndCheckTree();
		actions = sample.getActions();
		ac = sample.getAdditionalContext();
		generator = new SyntacticAnalysisContextGeneratorConf();
		//chunk
		for (int i = words.size(); i < 2*words.size(); i++) {		
			String[] context = generator.getContextForChunk(i-words.size(),chunkTree, actions, ac);
            events.add(new Event(actions.get(i), context));
		}		
		//buildAndCheck 两个变量i j   i控制第几个list  j控制list中的第几个
		int j = 0;
		for (int i = 2*words.size(); i < actions.size(); i=i+2) {
			String[] buildContext = generator.getContextForBuild(j,buildAndCheckTree.get(i-2*words.size()), actions, ac);
			events.add(new Event(actions.get(i), buildContext));
			if(actions.get(i+1).equals("yes")){
				int record = j-1;
				for (int k = record; k >= 0; k--) {
					if(buildAndCheckTree.get(i-2*words.size()).get(k).getNodeName().split("_")[0].equals("start")){
						j = k;
						break;
					}
				}
			}else if(actions.get(i+1).equals("no")){            	
				j++;
			}  
		}
		//buildAndCheck 两个变量i j   i控制第几个list  j控制list中的第几个
		j = 0;
		for (int i = 2*words.size(); i < actions.size(); i=i+2) {    
			if(actions.get(i+1).equals("yes")){
				String[] checkContext = generator.getContextForCheck(j,buildAndCheckTree.get(i+1-2*words.size()), actions, ac);
				int record = j-1;
				for (int k = record; k >= 0; k--) {		    
					if(buildAndCheckTree.get(i-2*words.size()).get(k).getNodeName().split("_")[0].equals("start")){			    
						j = k;
						break;
						}
				}
				events.add(new Event(actions.get(i+1), checkContext));
			}else if(actions.get(i+1).equals("no")){            	
				String[] checkContext = generator.getContextForCheck(j,buildAndCheckTree.get(i+1-2*words.size()), actions, ac);
				events.add(new Event(actions.get(i+1), checkContext));
				j++;
			}  
		}
		
		event1.add(new Event("start_NP",chunk0));
		event2.add(new Event("other",chunk1));
		event3.add(new Event("start_NP",chunk2));
		event4.add(new Event("join_NP",chunk3));
		event5.add(new Event("other",chunk4));
		event6.add(new Event("start_NP",chunk5));
		event7.add(new Event("join_NP",chunk6));
		event8.add(new Event("start_S",build0));
		event9.add(new Event("start_VP",build1));
		event10.add(new Event("join_VP",build2));
		event11.add(new Event("start_VP",build3));
		event12.add(new Event("start_PP",build4));
		event13.add(new Event("join_PP",build5));
		event14.add(new Event("join_VP",build6));
		event15.add(new Event("join_S",build7));
		event16.add(new Event("no",check0));
		event17.add(new Event("no",check1));
		event18.add(new Event("yes",check2));
		event19.add(new Event("no",check3));
		event20.add(new Event("no",check4));
		event21.add(new Event("yes",check5));
		event22.add(new Event("yes",check6));
		event23.add(new Event("yes",check7));
	}
	
	@Test
	public void test(){
		assertEquals(events.get(0).toString(),event1.get(0).toString());
		assertEquals(events.get(1).toString(),event2.get(0).toString());
		assertEquals(events.get(2).toString(),event3.get(0).toString());
		assertEquals(events.get(3).toString(),event4.get(0).toString());
		assertEquals(events.get(4).toString(),event5.get(0).toString());
		assertEquals(events.get(5).toString(),event6.get(0).toString());
		assertEquals(events.get(6).toString(),event7.get(0).toString());
		assertEquals(events.get(7).toString(),event8.get(0).toString());
		assertEquals(events.get(8).toString(),event9.get(0).toString());
		assertEquals(events.get(9).toString(),event10.get(0).toString());
		assertEquals(events.get(10).toString(),event11.get(0).toString());
		assertEquals(events.get(11).toString(),event12.get(0).toString());
		assertEquals(events.get(12).toString(),event13.get(0).toString());
		assertEquals(events.get(13).toString(),event14.get(0).toString());
		assertEquals(events.get(14).toString(),event15.get(0).toString());
		assertEquals(events.get(15).toString(),event16.get(0).toString());
		assertEquals(events.get(16).toString(),event17.get(0).toString());
		assertEquals(events.get(17).toString(),event18.get(0).toString());
		assertEquals(events.get(18).toString(),event19.get(0).toString());
		assertEquals(events.get(19).toString(),event20.get(0).toString());
		assertEquals(events.get(20).toString(),event21.get(0).toString());
		assertEquals(events.get(21).toString(),event22.get(0).toString());
		assertEquals(events.get(22).toString(),event23.get(0).toString());
	}
}
