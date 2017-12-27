package com.wxw.train.run;

import java.io.File;
import java.io.IOException;

import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.feature.SyntacticAnalysisContextGeneratorConf;
import com.wxw.model.bystep.SyntacticAnalysisMEForBuildAndCheck;
import com.wxw.model.bystep.SyntacticAnalysisMEForChunk;
import com.wxw.tree.HeadTreeNode;

import opennlp.tools.util.TrainingParameters;

/**
 * 句法分析训练类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisTrainerTool {

	private static void usage(){
		System.out.println(SyntacticAnalysisTrainerTool.class.getName()+"-data <corpusFile> -chunkmodel <chunkmodelFile> -buildandcheckmodel <buildandcheckmodelFile> -type <algorithom>"
				+ "-encoding"+"[-cutoff <num>] [-iters <num>]");
	}
	
	public static void main(String[] args) throws IOException {
		if(args.length < 1){
			usage();
			return;
		}
		int cutoff = 3;
		int iters = 100;
        File corpusFile = null;
        File chunkmodelFile = null;
        File buildandcheckmodelFile = null;
        String encoding = "UTF-8";
        String type = "MAXENT";
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-data"))
            {
                corpusFile = new File(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-chunkmodel"))
            {
                chunkmodelFile = new File(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-buildandcheckmodel"))
            {
            	buildandcheckmodelFile = new File(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-type"))
            {
                type = args[i + 1];
                i++;
            }
            else if (args[i].equals("-encoding"))
            {
                encoding = args[i + 1];
                i++;
            }
            else if (args[i].equals("-cutoff"))
            {
                cutoff = Integer.parseInt(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-iters"))
            {
                iters = Integer.parseInt(args[i + 1]);
                i++;
            }
        }
        
        SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen = new SyntacticAnalysisContextGeneratorConf();
        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));
        params.put(TrainingParameters.ALGORITHM_PARAM, type.toUpperCase());
        
        SyntacticAnalysisMEForChunk.train(corpusFile, chunkmodelFile,params, contextGen, encoding);
		SyntacticAnalysisMEForBuildAndCheck.train(corpusFile, buildandcheckmodelFile, params, contextGen, encoding);
	}
}
