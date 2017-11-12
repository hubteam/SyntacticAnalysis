package com.wxw.feature;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.wxw.tree.TreeNode;

/**
 * 根据配置文件生成特征
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisContextGeneratorConf implements SyntacticAnalysisContextGenerator{

	
	//pos
	private boolean w_2Set;
    private boolean w_1Set;
    private boolean w0Set;
    private boolean w1Set;
    private boolean w2Set;
    private boolean t_1Set;
    private boolean t_2t_1Set;
    private boolean prefix1Set;
    private boolean prefix2Set;
    private boolean prefix3Set;
    private boolean prefix4Set;
    private boolean suffix1Set;
    private boolean suffix2Set;
    private boolean suffix3Set;
    private boolean suffix4Set;
    private boolean numberSet;
    private boolean uppercaseSet;
    private boolean hypenSet;
	//chunk
	private boolean chunkandpostag0Set;
    private boolean chunkandpostag_1Set;
    private boolean chunkandpostag_2Set;
    private boolean chunkandpostag1Set;
    private boolean chunkandpostag2Set;
    private boolean chunkandpostag0ASet;
    private boolean chunkandpostag_1ASet;
    private boolean chunkandpostag_2ASet;
    private boolean chunkandpostag1ASet;
    private boolean chunkandpostag2ASet;
    private boolean chunkandpostag_10Set;
    private boolean chunkandpostag_1A0Set;
    private boolean chunkandpostag_1A0ASet;
    private boolean chunkandpostag_10ASet;
    private boolean chunkandpostag01Set;
    private boolean chunkandpostag0A1Set;
    private boolean chunkandpostag0A1ASet;
    private boolean chunkandpostag01ASet;
    private boolean chunkdefaultSet;
    
    //build
    private boolean cons0Set;
    private boolean cons_1Set;
    private boolean cons_2Set;
    private boolean cons1Set;
    private boolean cons2Set;
    private boolean cons0ASet;
    private boolean cons_1ASet;
    private boolean cons_2ASet;
    private boolean cons1ASet;
    private boolean cons2ASet;

    private boolean cons_10Set;
    private boolean cons_1A0Set;
    private boolean cons_1A0ASet;
    private boolean cons_10ASet;

    private boolean cons01Set;
    private boolean cons0A1Set;
    private boolean cons0A1ASet;
    private boolean cons01ASet;

    private boolean cons_2_10Set;
    private boolean cons_2A_1A0ASet;
    private boolean cons_2A_1A0Set;
    private boolean cons_2A_10Set;
    private boolean cons_2_1A0Set;

    private boolean cons012Set;
    private boolean cons0A1A2ASet;
    private boolean cons01A2ASet;
    private boolean cons01A2Set;
    private boolean cons012ASet;

    private boolean cons_101Set;
    private boolean cons_1A0A1ASet;
    private boolean cons_1A01ASet;
    private boolean cons_101ASet;
    private boolean cons_1A01Set;

    private boolean punctuationSet;

    private boolean builddefaultSet;
    
    //check
    private boolean checkcons_lastSet;
    private boolean checkcons_lastASet;
    private boolean checkcons_beginSet;
    private boolean checkcons_beginASet;

    private boolean checkcons_ilastSet;
    private boolean checkcons_iAlastSet;
    private boolean checkcons_ilastASet;
    private boolean checkcons_iAlastASet;

    private boolean productionSet;

    private boolean surround1Set;
    private boolean surround1ASet;
    private boolean surround2Set;
    private boolean surround2ASet;
    private boolean surround_1Set;
    private boolean surround_1ASet;
    private boolean surround_2Set;
    private boolean surround_2ASet;

    private boolean checkdefaultSet;
	
	/**
	 * 无参构造
	 * @throws IOException 
	 */
	public SyntacticAnalysisContextGeneratorConf() throws IOException{

		Properties featureConf = new Properties();
		InputStream featureStream = SyntacticAnalysisContextGeneratorConf.class.getClassLoader().getResourceAsStream("com/wxw/run/feature.properties");
		featureConf.load(featureStream);
		init(featureConf);
	}
	
	/**
	 * 有参构造
	 * @param properties 配置文件
	 */
	public SyntacticAnalysisContextGeneratorConf(Properties properties){
		init(properties);
	}

	/**
	 * 根据配置文件中的信息初始化变量
	 * @param properties
	 */
	private void init(Properties config) {
		//pos
		w0Set = (config.getProperty("pos.w0", "true").equals("true"));
		w1Set = (config.getProperty("pos.w1", "true").equals("true"));
		w2Set = (config.getProperty("pos.w2", "true").equals("true"));
		w_1Set = (config.getProperty("pos.w_1", "true").equals("true"));
		w_2Set = (config.getProperty("pos.w_2", "true").equals("true"));
		t_1Set = (config.getProperty("pos.t_1", "true").equals("true"));
		t_2t_1Set = (config.getProperty("pos.t_2t_1", "true").equals("true"));
		prefix1Set = (config.getProperty("pos.prefix1", "true").equals("true"));
		prefix2Set = (config.getProperty("pos.prefix2", "true").equals("true"));
		prefix3Set = (config.getProperty("pos.prefix3", "true").equals("true"));
		prefix4Set = (config.getProperty("pos.prefix4", "true").equals("true"));
		suffix1Set = (config.getProperty("pos.suffix1", "true").equals("true"));
		suffix2Set = (config.getProperty("pos.suffix2", "true").equals("true"));
		suffix3Set = (config.getProperty("pos.suffix3", "true").equals("true"));
		suffix4Set = (config.getProperty("pos.suffix4", "true").equals("true"));
		numberSet = (config.getProperty("pos.number", "true").equals("true"));
		uppercaseSet = (config.getProperty("pos.uppercase", "true").equals("true"));
		hypenSet = (config.getProperty("pos.hypen", "true").equals("true"));
		
		//chunk
		chunkandpostag0Set = (config.getProperty("tree.chunkandpostag0", "true").equals("true"));
		chunkandpostag1Set = (config.getProperty("tree.chunkandpostag1", "true").equals("true"));
		chunkandpostag2Set = (config.getProperty("tree.chunkandpostag2", "true").equals("true"));
		chunkandpostag_1Set = (config.getProperty("tree.chunkandpostag_1", "true").equals("true"));
		chunkandpostag_2Set = (config.getProperty("tree.chunkandpostag_2", "true").equals("true"));
		chunkandpostag0ASet = (config.getProperty("tree.chunkandpostag0*", "true").equals("true"));
		chunkandpostag1ASet = (config.getProperty("tree.chunkandpostag1*", "true").equals("true"));
		chunkandpostag2ASet = (config.getProperty("tree.chunkandpostag2*", "true").equals("true"));
		chunkandpostag_1ASet = (config.getProperty("tree.chunkandpostag_1*", "true").equals("true"));
		chunkandpostag_2ASet = (config.getProperty("tree.chunkandpostag_2*", "true").equals("true"));
		
		chunkandpostag_10Set = (config.getProperty("tree.chunkandpostag_10", "true").equals("true"));
		chunkandpostag_1A0Set = (config.getProperty("tree.chunkandpostag_1*0", "true").equals("true"));
		chunkandpostag_1A0ASet = (config.getProperty("tree.chunkandpostag_1*0*", "true").equals("true"));
		chunkandpostag_10ASet = (config.getProperty("tree.chunkandpostag_10*", "true").equals("true"));
		
		chunkandpostag01Set = (config.getProperty("tree.chunkandpostag01", "true").equals("true"));
		chunkandpostag0A1Set = (config.getProperty("tree.chunkandpostag0*1", "true").equals("true"));
		chunkandpostag0A1ASet = (config.getProperty("tree.chunkandpostag0*1*", "true").equals("true"));
		chunkandpostag01ASet = (config.getProperty("tree.chunkandpostag0*1", "true").equals("true"));
		
		chunkdefaultSet = (config.getProperty("tree.chunkdefault", "true").equals("true"));
		
		//build
		cons0Set = (config.getProperty("tree.cons0", "true").equals("true"));
		cons_1Set = (config.getProperty("tree.cons_1", "true").equals("true"));
		cons_2Set = (config.getProperty("tree.cons_2", "true").equals("true"));
		cons1Set = (config.getProperty("tree.cons1", "true").equals("true"));
		cons2Set = (config.getProperty("tree.cons2", "true").equals("true"));
		
		cons0ASet = (config.getProperty("tree.cons0*", "true").equals("true"));
		cons_1ASet = (config.getProperty("tree.cons_1*", "true").equals("true"));
		cons_2ASet = (config.getProperty("tree.cons_2*", "true").equals("true"));
		cons1ASet = (config.getProperty("tree.cons1*", "true").equals("true"));
		cons2ASet = (config.getProperty("tree.cons2*", "true").equals("true"));
		
		cons_10Set = (config.getProperty("tree.cons_10", "true").equals("true"));
		cons_1A0Set = (config.getProperty("tree.cons_1*0", "true").equals("true"));
		cons_1A0ASet = (config.getProperty("tree.cons_1*0*", "true").equals("true"));
		cons_10ASet = (config.getProperty("tree.cons_10*", "true").equals("true"));
		
		cons01Set = (config.getProperty("tree.cons01", "true").equals("true"));
		cons0A1Set = (config.getProperty("tree.cons0*1", "true").equals("true"));
		cons0A1ASet = (config.getProperty("tree.cons0*1*", "true").equals("true"));
		cons01ASet = (config.getProperty("tree.cons01*", "true").equals("true"));
		
		cons_2_10Set = (config.getProperty("tree.cons_2_10", "true").equals("true"));
		cons_2A_1A0ASet = (config.getProperty("tree.cons_2*_1*0*", "true").equals("true"));
		cons_2A_1A0Set = (config.getProperty("tree.cons_2*_1*0", "true").equals("true"));
		cons_2A_10Set = (config.getProperty("tree.cons_2*_10", "true").equals("true"));
		cons_2_1A0Set = (config.getProperty("tree.cons_2_1*0", "true").equals("true"));
		
		cons012Set = (config.getProperty("tree.cons012", "true").equals("true"));
		cons0A1A2ASet = (config.getProperty("tree.cons0*1*2*", "true").equals("true"));
		cons01A2ASet = (config.getProperty("tree.cons01*2*", "true").equals("true"));
		cons01A2Set = (config.getProperty("tree.cons01*2", "true").equals("true"));
		cons012ASet = (config.getProperty("tree.cons012*", "true").equals("true"));
		
		cons_101Set = (config.getProperty("tree.cons_101", "true").equals("true"));
		cons_1A0A1ASet = (config.getProperty("tree.cons_1*0*1*", "true").equals("true"));
		cons_1A01ASet = (config.getProperty("tree.cons_1*01*", "true").equals("true"));
		cons_101ASet = (config.getProperty("tree.cons_101*", "true").equals("true"));
		cons_1A01Set = (config.getProperty("tree.cons_1*01", "true").equals("true"));
		
		punctuationSet = (config.getProperty("tree.punctuation", "true").equals("true"));
		builddefaultSet = (config.getProperty("tree.builddefault", "true").equals("true"));
		
		//check
		checkcons_lastSet = (config.getProperty("tree.checkcons_last", "true").equals("true"));
		checkcons_lastASet = (config.getProperty("tree.checkcons_last*", "true").equals("true"));
		checkcons_beginSet = (config.getProperty("tree.checkcons_begin", "true").equals("true"));
		checkcons_beginASet = (config.getProperty("tree.checkcons_begin*", "true").equals("true"));
		checkcons_ilastSet = (config.getProperty("tree.checkcons_ilast", "true").equals("true"));
		productionSet = (config.getProperty("tree.production", "true").equals("true"));
		
		surround1Set = (config.getProperty("tree.surround1", "true").equals("true"));
		surround2Set = (config.getProperty("tree.surround2", "true").equals("true"));
		surround1ASet = (config.getProperty("tree.surround1*", "true").equals("true"));
		surround2ASet = (config.getProperty("tree.surround2*", "true").equals("true"));
		surround_1Set = (config.getProperty("tree.surround_1", "true").equals("true"));
		surround_2Set = (config.getProperty("tree.surround_2", "true").equals("true"));
		surround_1ASet = (config.getProperty("tree.surround_1*", "true").equals("true"));
		surround_2ASet = (config.getProperty("tree.surround_2*", "true").equals("true"));
		
		checkdefaultSet = (config.getProperty("tree.checkdefault", "true").equals("true"));
	}

	/**
	 * 生成词性标注的上下文特征
	 * @param index 当前位置
	 * @param words 词语
	 * @param poses 词性
	 * @return
	 */
	public String[] getContextForPos(int index, List<String> words, List<String> poses) {
		String w1, w2, w0, w_1, w_2;
        w1 = w2 = w0 = w_1 = w_2 = null;
        String t_1 = null;
        String t_2 = null;

        w0 = words.get(index);
        if (words.size() > index + 1) {
            w1 = words.get(index+1);
            if (words.size() > index + 2) {
                w2 = words.get(index+2);
            }
        }

        if (index - 1 >= 0) {
            w_1 = words.get(index-1);
            t_1 = poses.get(index-1);
            if (index - 2 >= 0) {
                w_2 = words.get(index-2);
                t_2 = poses.get(index-2);
            }
        }
        List<String> features = new ArrayList<String>();
      //这里特征有两种，当前词的个数小于，与大于等于5
        //下面这部分特征是共有的
        if(w_1 != null){
        	if(w_1Set){
        		features.add("w_1="+w_1);
        	}
        }
        if(w_2 != null){
        	if(w_2Set){
        		features.add("w_2="+w_2);
        	}
        }
        if(w1 != null){
        	if(w1Set){
        		features.add("w1="+w1);
        	}
        }
        if(w2 != null){
        	if(w2Set){
        		features.add("w2="+w2);
        	}
        }
        if(t_1 != null){
        	if(t_1Set){
        		features.add("t_1="+t_1);
        	}
        }
        if(t_2 != null && t_1 != null){
        	if(t_2t_1Set){
        		features.add("t_2t_1="+t_2+t_1);
        	}
        }
        //下面部分的特征根据词语出现的次数进行选择
        if(FeatureForPosTools.overFive(w0)){
        	if(w0Set){
        		features.add("w0="+w0);
        	}
        }else{
        	if(prefix1Set){
        		features.add("prefix1="+w0.charAt(0));
        	}
        	if(prefix2Set){
        		features.add("prefix2="+w0.charAt(1));
        	}
        	if(prefix3Set){
        		features.add("prefix3="+w0.charAt(2));
        	}
        	if(prefix4Set){
        		features.add("prefix4="+w0.charAt(3));
        	}
        	if(suffix1Set){
        		features.add("suffix1="+w0.charAt(w0.length()-1));
        	}
        	if(suffix2Set){
        		features.add("suffix2="+w0.charAt(w0.length()-2));
        	}
        	if(suffix3Set){
        		features.add("suffix3="+w0.charAt(w0.length()-3));
        	}
        	if(suffix4Set){
        		features.add("suffix4="+w0.charAt(w0.length()-4));
        	}
        	if(hypenSet){
        		if(w0.contains("-")){
        			features.add("hypen="+1);
        		}else{
        			features.add("hypen="+0);
        		}
        	}
        	if(numberSet){
        		if(FeatureForPosTools.isAlbDigit(w0)){
        			features.add("number="+1);
        		}else{
        			features.add("number="+0);
        		}
        	}
        	if(uppercaseSet){
        		if(FeatureForPosTools.isLetter(w0)){
        			features.add("uppercase="+1);
        		}else{
        			features.add("uppercase="+0);
        		}
        	}
        }
        String[] contexts = features.toArray(new String[features.size()]);
        return contexts;
	}

	/**
	 * chunk步的上下文特征
	 * @param index 当前位置
	 * @param chunkTree 子树序列
	 * @param actions 动作序列
	 * @return
	 */
	public String[] getContextForChunk(int index, List<TreeNode> chunkTree, List<String> actions) {
		List<String> features = new ArrayList<String>();
		TreeNode tree0,tree1,tree2,tree_1,tree_2;
		tree0 = tree1 = tree2 = tree_1 = tree_2 = null;
		tree0 = chunkTree.get(index);
		if (chunkTree.size() > index + 1) {
            tree1 = chunkTree.get(index+1);
            if (chunkTree.size() > index + 2) {
                tree2 = chunkTree.get(index+2);
            }
        }

        if (index - 1 >= 0) {
            tree_1 = chunkTree.get(index-1);
            if (index - 2 >= 0) {
                tree_2 = chunkTree.get(index-2);
            }
        }
		//这里的特征是word pos chunk组成的
		//当前位置的时候是没有chunk标记的
        if(tree0 != null){
			if(chunkandpostag0Set){
				features.add("chunkandpostag0="+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getChildren().get(0).getNodeName());
			}
			if(chunkandpostag0ASet){
				features.add("chunkandpostag0*="+tree0.getChildren().get(0).getNodeName());
			}
		}
        //当前位置之前的时候有chunk标记
        if(tree_1 != null){
			if(chunkandpostag_1Set){
				features.add("chunkandpostag_1="+tree_1.getNodeName()+"|"
			+tree_1.getChildren().get(0).getNodeName()+"|"+tree_1.getChildren().get(0).getChildren().get(0).getNodeName());
			}
			if(chunkandpostag_1ASet){
				features.add("chunkandpostag_1*="+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName());
			}
		}
        if(tree_2 != null){
			if(chunkandpostag_2Set){
				features.add("chunkandpostag_2="+tree_2.getNodeName()+"|"
			+tree_2.getChildren().get(0).getNodeName()+"|"+tree_2.getChildren().get(0).getChildren().get(0).getNodeName());
			}
			if(chunkandpostag_2ASet){
				features.add("chunkandpostag_2*="+tree_2.getNodeName()+"|"+tree_2.getChildren().get(0).getNodeName());
			}
		}
        //当前位置之后的也没有chunk标记
        if(tree1 != null){
			if(chunkandpostag1Set){
				features.add("chunkandpostag1="+tree1.getChildren().get(0).getNodeName()+"|"+tree1.getChildren().get(0).getChildren().get(0).getNodeName());
			}
			if(chunkandpostag1ASet){
				features.add("chunkandpostag1*="+tree1.getChildren().get(0).getNodeName());
			}
		}
        if(tree2 != null){
			if(chunkandpostag2Set){
				features.add("chunkandpostag2="+tree2.getChildren().get(0).getNodeName()+"|"+tree2.getChildren().get(0).getChildren().get(0).getNodeName());
			}
			if(chunkandpostag2ASet){
				features.add("chunkandpostag2*="+tree2.getChildren().get(0).getNodeName());
			}
		}
        
        if(tree_1 != null && tree0 != null){
        	if(chunkandpostag_10Set){
        		features.add("chunkandpostag_10="+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName()+"|"+tree_1.getChildren().get(0).getChildren().get(0).getNodeName()
        				+";"+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getChildren().get(0).getNodeName());
        	}
        	if(chunkandpostag_10ASet){
        		features.add("chunkandpostag_10*="+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName()+"|"+tree_1.getChildren().get(0).getChildren().get(0).getNodeName()
        				+";"+tree0.getChildren().get(0).getNodeName());
        	}
        	if(chunkandpostag_1A0Set){
        		features.add("chunkandpostag_1*0="+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName()
        				+";"+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getChildren().get(0).getNodeName());
        	}
        	if(chunkandpostag_1A0ASet){
        		features.add("chunkandpostag_1*0*="+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName()
        				+";"+tree0.getChildren().get(0).getNodeName());
        	}
        }
        
        if(tree0 != null && tree1 != null){
        	if(chunkandpostag01Set){
        		features.add("chunkandpostag01="+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getChildren().get(0).getNodeName()
        				+";"+tree1.getChildren().get(0).getNodeName()+"|"+tree1.getChildren().get(0).getChildren().get(0).getNodeName());
        	}
        	if(chunkandpostag01ASet){
        		features.add("chunkandpostag01*="+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getChildren().get(0).getNodeName()
        				+";"+tree1.getChildren().get(0).getNodeName());
        	}
        	if(chunkandpostag0A1Set){
        		features.add("chunkandpostag0*1="+tree0.getChildren().get(0).getNodeName()
        				+";"+tree1.getChildren().get(0).getNodeName()+"|"+tree1.getChildren().get(0).getChildren().get(0).getNodeName());
        	}
        	if(chunkandpostag0A1ASet){
        		features.add("chunkandpostag0*1*="+tree0.getChildren().get(0).getNodeName()
        				+";"+tree1.getChildren().get(0).getNodeName());
        	}
        }
		String[] contexts = features.toArray(new String[features.size()]);
        return contexts;
	}

	/**
	 * build步的上下文特征
	 * @param index 当前位置
	 * @param buildAndCheckTree 子树序列
	 * @param actions 动作序列
	 * @return
	 */
	public String[] getContextForBuild(int index, List<TreeNode> buildAndCheckTree, List<String> actions) {
		List<String> features = new ArrayList<String>();
		TreeNode tree0,tree1,tree2,tree_1,tree_2;
		tree0 = tree1 = tree2 = tree_1 = tree_2 = null;
		tree0 = buildAndCheckTree.get(index);
		if (buildAndCheckTree.size() > index + 1) {
            tree1 = buildAndCheckTree.get(index+1);
            if (buildAndCheckTree.size() > index + 2) {
                tree2 = buildAndCheckTree.get(index+2);
            }
        }

        if (index - 1 >= 0) {
            tree_1 = buildAndCheckTree.get(index-1);
            if (index - 2 >= 0) {
                tree_2 = buildAndCheckTree.get(index-2);
            }
        }
        //这里的标记由head words , constituent, build
		//当前位置没有build标记
        if(tree0 != null){
			if(cons0Set){
				features.add("cons0="+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getHeadWords());
			}
			if(cons0ASet){
				features.add("cons0*="+tree0.getChildren().get(0).getNodeName());
			}
		}
        //当前位置之前的有build标记
        if(tree_1 != null){
			if(cons_1Set){
				features.add("cons_1="+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName()+"|"+tree_1.getChildren().get(0).getHeadWords());
			}
			if(cons_1ASet){
				features.add("cons_1*="+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName());
			}
		}
        if(tree_2 != null){
			if(cons_2Set){
				features.add("cons_2="+tree_2.getNodeName()+"|"+tree_2.getChildren().get(0).getNodeName()+"|"+tree_2.getChildren().get(0).getHeadWords());
			}
			if(cons_2ASet){
				features.add("cons_2*="+tree_2.getNodeName()+"|"+tree_2.getChildren().get(0).getNodeName());
			}
		}
        //当前位置之后的也没有build标记
        if(tree1 != null){
			if(cons1Set){
				features.add("cons1="+tree1.getChildren().get(0).getNodeName()+"|"+tree1.getChildren().get(0).getHeadWords());
			}
			if(cons1ASet){
				features.add("cons1*="+tree1.getChildren().get(0).getNodeName());
			}
		}
        if(tree2 != null){
			if(cons2Set){
				features.add("cons2="+tree2.getChildren().get(0).getNodeName()+"|"+tree2.getChildren().get(0).getHeadWords());
			}
			if(cons2ASet){
				features.add("cons2*="+tree2.getChildren().get(0).getNodeName());
			}
		}
        
        if(tree_1 != null && tree0 != null){
        	if(cons_10Set){
        		features.add("cons_10="+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName()+"|"+tree_1.getChildren().get(0).getHeadWords()
        				+";"+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getHeadWords());
        	}
        	if(cons_10ASet){
        		features.add("cons_10*="+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName()+"|"+tree_1.getChildren().get(0).getHeadWords()
        				+";"+tree0.getChildren().get(0).getNodeName());
        	}
        	if(cons_1A0Set){
        		features.add("cons_1*0="+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName()
        				+";"+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getHeadWords());
        	}
        	if(cons_1A0ASet){
        		features.add("cons_1*0*="+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName()
        				+";"+tree0.getChildren().get(0).getNodeName());
        	}
        }
        
        if(tree0 != null && tree1 != null){
        	if(cons01Set){
        		features.add("cons01="+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getHeadWords()
        				+";"+tree1.getChildren().get(0).getNodeName()+"|"+tree1.getChildren().get(0).getHeadWords());
        	}
        	if(cons01ASet){
        		features.add("cons01*="+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getHeadWords()
        				+";"+tree1.getChildren().get(0).getNodeName());
        	}
        	if(cons0A1Set){
        		features.add("cons0*1="+tree0.getChildren().get(0).getNodeName()
        				+";"+tree1.getChildren().get(0).getNodeName()+"|"+tree1.getChildren().get(0).getHeadWords());
        	}
        	if(cons0A1ASet){
        		features.add("cons0*1*="+tree0.getChildren().get(0).getNodeName()
        				+";"+tree1.getChildren().get(0).getNodeName());
        	}
        }
        
        if(tree_2 != null && tree_1 != null && tree0 != null){
        	if(cons_2_10Set){
        		features.add("cons_2_10="+tree_2.getNodeName()+"|"+tree_2.getChildren().get(0).getNodeName()+"|"+tree_2.getChildren().get(0).getHeadWords()
        				+";"+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName()+"|"+tree_1.getChildren().get(0).getHeadWords()
        				+";"+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getHeadWords());
        	}
        	if(cons_2A_1A0Set){
        		features.add("cons_2*_1*0="+tree_2.getNodeName()+"|"+tree_2.getChildren().get(0).getNodeName()
        				+";"+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName()
        				+";"+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getHeadWords());
        	}
        	if(cons_2A_10Set){
        		features.add("cons_2*_10="+tree_2.getNodeName()+"|"+tree_2.getChildren().get(0).getNodeName()
        				+";"+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName()+"|"+tree_1.getChildren().get(0).getHeadWords()
        				+";"+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getHeadWords());
        	}
        	if(cons_2_1A0Set){
        		features.add("cons_2_1*0="+tree_2.getNodeName()+"|"+tree_2.getChildren().get(0).getNodeName()+"|"+tree_2.getChildren().get(0).getHeadWords()
        				+";"+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName()
        				+";"+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getHeadWords());
        	}
        	if(cons_2A_1A0ASet){
        		features.add("cons_2*_1*0*="+tree_2.getNodeName()+"|"+tree_2.getChildren().get(0).getNodeName()
        				+";"+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName()
        				+";"+tree0.getChildren().get(0).getNodeName());
        	}
        }     

        if(tree0 != null && tree1 != null && tree2 != null){
        	if(cons012Set){
        		features.add("cons012="+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getHeadWords()
        				+";"+tree1.getChildren().get(0).getNodeName()+"|"+tree1.getChildren().get(0).getHeadWords()
        				+";"+tree2.getChildren().get(0).getNodeName()+"|"+tree2.getChildren().get(0).getHeadWords());
        	}
        	if(cons01A2ASet){
        		features.add("cons01*2*="+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getHeadWords()
        				+";"+tree1.getChildren().get(0).getNodeName()
        				+";"+tree2.getChildren().get(0).getNodeName());
        	}
        	if(cons01A2Set){
        		features.add("cons01*2="+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getHeadWords()
        				+";"+tree1.getChildren().get(0).getNodeName()
        				+";"+tree2.getChildren().get(0).getNodeName()+"|"+tree2.getChildren().get(0).getHeadWords());
        	}
        	if(cons012ASet){
        		features.add("cons012*="+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getHeadWords()
        				+";"+tree1.getChildren().get(0).getNodeName()+"|"+tree1.getChildren().get(0).getHeadWords()
        				+";"+tree2.getChildren().get(0).getNodeName());
        	}
        	if(cons0A1A2ASet){
        		features.add("cons0*1*2*="+tree0.getChildren().get(0).getNodeName()
        				+";"+tree1.getChildren().get(0).getNodeName()
        				+";"+tree2.getChildren().get(0).getNodeName());
        	}
        }

        if(tree_1 != null && tree0 != null && tree1 != null){
        	if(cons_101Set){
        		features.add("cons_101="+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName()+"|"+tree_1.getChildren().get(0).getHeadWords()
        				+";"+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getHeadWords()
        				+";"+tree1.getChildren().get(0).getNodeName()+"|"+tree1.getChildren().get(0).getHeadWords());
        	}
        	if(cons_1A01ASet){
        		features.add("cons_1*01*="+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName()
        				+";"+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getHeadWords()
        				+";"+tree1.getChildren().get(0).getNodeName());
        	}
        	if(cons_1A01Set){
        		features.add("cons_1*01="+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName()
        				+";"+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getHeadWords()
        				+";"+tree1.getChildren().get(0).getNodeName()+"|"+tree1.getChildren().get(0).getHeadWords());
        	}
        	if(cons_101ASet){
        		features.add("cons_101*="+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName()+"|"+tree_1.getChildren().get(0).getHeadWords()
        				+";"+tree0.getChildren().get(0).getNodeName()+"|"+tree0.getChildren().get(0).getHeadWords()
        				+";"+tree1.getChildren().get(0).getNodeName());
        	}
        	if(cons_1A0A1ASet){
        		features.add("cons_1*0*1*="+tree_1.getNodeName()+"|"+tree_1.getChildren().get(0).getNodeName()
        				+";"+tree0.getChildren().get(0).getNodeName()
        				+";"+tree1.getChildren().get(0).getNodeName());
        	}
        }
        
        //标点符号
        for (int i = index-1; i >= 0; i--) {
			if(buildAndCheckTree.get(i).getChildren().get(0).getNodeName().equals("[")){
				if(tree0.getChildren().get(0).getNodeName().equals("]")){
					if(punctuationSet){
						features.add("punctuation="+"bracketsmatch");
					}
				}
			}
		}
        if(tree0.getChildren().get(0).getNodeName().equals(",")){
			if(punctuationSet){
				features.add("punctuation="+"comma");
			}
		}
        if(tree0.getChildren().get(0).getNodeName().equals(".")){
			if(punctuationSet){
				features.add("punctuation="+"endofsentence");
			}
		}
        
		String[] contexts = features.toArray(new String[features.size()]);
        return contexts;
	}

	/**
	 * build步的上下文特征
	 * @param index 当前位置
	 * @param buildAndCheckTree 子树序列
	 * @param actions 动作序列
	 * @return
	 */
	public String[] getContextForCheck(int index, List<TreeNode> buildAndCheckTree, List<String> actions) {
		List<String> features = new ArrayList<String>();
		TreeNode tree0 = null;
		tree0 = buildAndCheckTree.get(index);
		if(checkcons_beginSet){
			features.add("checkcons_begin=");
		}
		if(checkcons_beginASet){
			features.add("checkcons_begin*=");
		}
		
		if(checkcons_lastSet){
			features.add("checkcons_last=");
		}
		if(checkcons_lastASet){
			features.add("checkcons_last*=");
		}
		
		for (int i = index-1; i >= 0; i--) {
			if(checkcons_ilastSet){
				features.add("checkcons_"+i+"last=");
			}
			if(checkcons_iAlastSet){
				features.add("checkcons_"+i+"*last=");
			}
			if(checkcons_ilastASet){
				features.add("checkcons_"+i+"last*=");
			}
			if(checkcons_iAlastASet){
				features.add("checkcons_"+i+"*last*=");
			}
		}
		
		if(productionSet){
			features.add("production=");
		}
		
		if(surround1Set){
			features.add("surround1=");
		}
		if(surround1ASet){
			features.add("surround1*=");
		}
		if(surround2Set){
			features.add("surround2=");
		}
		if(surround2ASet){
			features.add("surround2*=");
		}
		
		if(surround_1Set){
			features.add("surround_1=");
		}
		if(surround_1ASet){
			features.add("surround_1*=");
		}
		if(surround_2Set){
			features.add("surround_2=");
		}
		if(surround_2ASet){
			features.add("surround_2*=");
		}
		
		String[] contexts = features.toArray(new String[features.size()]);
        return contexts;
	}

	/**
	 * 生成词性标注的上下文特征
	 * @param index 当前位置
	 * @param words 词语
	 * @param poses 词性
	 * @param ac 
	 * @return
	 */
	@Override
	public String[] getContextForPos(int index, List<String> words, List<String> poses, Object[] ac) {

		return getContextForPos(index,words,poses);
	}

	/**
	 * chunk步的上下文特征
	 * @param index 当前位置
	 * @param chunkTree 子树序列
	 * @param actions 动作序列
	 * @param ac 
	 * @return
	 */
	@Override
	public String[] getContextForChunk(int index, List<TreeNode> chunkTree, List<String> actions, Object[] ac) {

		return getContextForChunk(index,chunkTree,actions);
	}

	/**
	 * build步的上下文特征
	 * @param index 当前位置
	 * @param buildAndCheckTree 子树序列
	 * @param actions 动作序列
	 * @param ac 
	 * @return
	 */
	@Override
	public String[] getContextForBuild(int index, List<TreeNode> buildAndCheckTree, List<String> actions, Object[] ac) {

		return getContextForBuild(index,buildAndCheckTree,actions);
	}

	/**
	 * build步的上下文特征
	 * @param index 当前位置
	 * @param buildAndCheckTree 子树序列
	 * @param actions 动作序列
	 * @param ac 
	 * @return
	 */
	@Override
	public String[] getContextForCheck(int index, List<TreeNode> buildAndCheckTree, List<String> actions, Object[] ac) {

		return getContextForCheck(index,buildAndCheckTree,actions);
	}

	/**
	 * 用于训练句法树模型的特征
	 */
	@Override
	public String toString() {
		return "SyntacticAnalysisContextGeneratorConf{" + "chunkandpostag0Set=" + chunkandpostag0Set + 
                ", chunkandpostag_1Set=" + chunkandpostag_1Set + ", chunkandpostag_2Set=" + chunkandpostag_2Set + 
                ", chunkandpostag1Set=" + chunkandpostag1Set + ", chunkandpostag2Set=" + chunkandpostag2Set +  
                ", chunkandpostag0*Set=" + chunkandpostag0ASet + 
                ", chunkandpostag_1*Set=" + chunkandpostag_1ASet + ", chunkandpostag_2*Set=" + chunkandpostag_2ASet + 
                ", chunkandpostag1*Set=" + chunkandpostag1ASet + ", chunkandpostag2*Set=" + chunkandpostag2ASet +  
                ", chunkandpostag_10Set=" + chunkandpostag_10Set + ", chunkandpostag_1*0Set=" + chunkandpostag_1A0Set +  
                ", chunkandpostag_1*0ASet=" + chunkandpostag_1A0ASet + ", chunkandpostag_10*Set=" + chunkandpostag_10ASet + 
                ", chunkandpostag01Set=" + chunkandpostag01Set + ", chunkandpostag0*1Set=" + chunkandpostag0A1Set +  
                ", chunkandpostag0*1ASet=" + chunkandpostag0A1ASet + ", chunkandpostag01*Set=" + chunkandpostag01ASet +
                ", chunkdefaultSet=" + chunkdefaultSet + 
                ", cons0Set=" + cons0Set + 
                ", cons_1Set=" + cons_1Set + ", cons_2Set=" + cons_2Set + 
                ", cons1Set=" + cons1Set + ", cons2Set=" + cons2Set +  
                ", cons0*Set=" + cons0ASet + 
                ", cons_1*Set=" + cons_1ASet + ", cons_2*Set=" + cons_2ASet + 
                ", cons1*Set=" + cons1ASet + ", cons2*Set=" + cons2ASet + 
                ", cons_10Set=" + cons_10Set + ", cons_1*0Set=" + cons_1A0Set +  
                ", cons_1*0*Set=" + cons_1A0ASet + ", cons_10*Set=" + cons_10ASet + 
                ", cons01Set=" + cons01Set + ", cons0*1Set=" + cons0A1Set +  
                ", cons0*1*Set=" + cons0A1ASet + ", cons01*Set=" + cons01ASet + 
                ", cons_2_10Set=" + cons_2_10Set + 
                ", cons_2*_1*0*Set=" + cons_2A_1A0ASet + ", cons_2*_1*0Set=" + cons_2A_1A0Set + 
                ", cons_2*_10Set=" + cons_2A_10Set + ", cons_2_1*0Set=" + cons_2_1A0Set +  
                ", cons012Set=" + cons012Set + 
                ", cons0*1*2*Set=" + cons0A1A2ASet + ", cons01*2*Set=" + cons01A2ASet + 
                ", cons01*2Set=" + cons01A2Set + ", cons012*Set=" + cons012ASet +  
                ", cons_101Set=" + cons_101Set + 
                ", cons_1*0*1*Set=" + cons_1A0A1ASet + ", cons_1*01*Set=" + cons_1A01ASet + 
                ", cons_101*Set=" + cons_101ASet + ", cons_1*01Set=" + cons_1A01Set +
                ", punctuationSet=" + punctuationSet + ", builddefaultSet=" + builddefaultSet +
                ", checkcons_lastSet=" + checkcons_lastSet + ", checkcons_last*Set=" + checkcons_lastASet + 
                ", checkcons_beginSet=" + checkcons_beginSet + ", checkcons_begin*Set=" + checkcons_beginASet +  
                ", checkcons_ilastSet=" + checkcons_ilastSet + ", checkcons_i*lastSet=" + checkcons_iAlastSet + 
                ", checkcons_ilast*Set=" + checkcons_ilastASet + ", checkcons_i*last*Set=" + checkcons_iAlastASet + 
                ", productionSet=" + productionSet + 
                ", surround1Set=" + surround1Set + ", surround2Set=" + surround2Set + 
                ", surround_1Set=" + surround_1Set + ", surround_2Set=" + surround_2Set + 
                ", surround1*Set=" + surround1ASet + ", surround2*Set=" + surround2ASet + 
                ", surround_1*Set=" + surround_1ASet + ", surround_2*Set=" + surround_2ASet + 
                ", checkdefaultSet=" + checkdefaultSet + 
                '}';
	}	
	
	/**
	 * 用于训练词性标记模型的特征
	 * @return
	 */
	public String toPosString() {
		return "SyntacticAnalysisContextGeneratorConf{" + "w_2Set=" + w_2Set + ", w_1Set=" + w_1Set + 
                ", w0Set=" + w0Set + ", w1Set=" + w1Set + ", w2Set=" + w2Set + 
                ", t_1Set=" + t_1Set + ", t_2t_1Set=" + t_2t_1Set + 
                ", prefix1Set=" + prefix1Set + ", prefix2Set=" + prefix2Set +  
                ", prefix3Set=" + prefix3Set + ", prefix4Set=" + prefix4Set + 
                ", suffix1Set=" + suffix1Set + ", suffix2Set=" + suffix2Set +  
                ", suffix3Set=" + suffix3Set + ", suffix4Set=" + suffix4Set + 
                ", numberSet=" + numberSet + ", uppercaseSet=" + uppercaseSet +  ", hypenSet=" + hypenSet + 
                '}';
	}	
}
