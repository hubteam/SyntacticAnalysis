package com.wxw.feature;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 根据配置文件生成特征
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisContextGeneratorConf implements SyntacticAnalysisContextGenerator{

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
}
