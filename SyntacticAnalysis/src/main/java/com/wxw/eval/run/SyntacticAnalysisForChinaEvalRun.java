package com.wxw.eval.run;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.wxw.evaluate.SyntacticAnalysisErrorPrinter;
import com.wxw.evaluate.SyntacticAnalysisMeasure;
import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.feature.SyntacticAnalysisContextGeneratorConf;
import com.wxw.model.bystep.POSTaggerMEExtend;
import com.wxw.model.bystep.SyntacticAnalysisEvaluatorForByStep;
import com.wxw.model.bystep.SyntacticAnalysisEvaluatorForChina;
import com.wxw.model.bystep.SyntacticAnalysisMEForBuildAndCheck;
import com.wxw.model.bystep.SyntacticAnalysisMEForChunk;
import com.wxw.model.bystep.SyntacticAnalysisModelForBuildAndCheck;
import com.wxw.model.bystep.SyntacticAnalysisModelForChunk;
import com.wxw.stream.FileInputStreamFactory;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.stream.SyntacticAnalysisSampleStream;
import com.wxw.tree.HeadTreeNode;
import com.wxw.wordsegandpos.feature.WordSegAndPosContextGenerator;
import com.wxw.wordsegandpos.feature.WordSegAndPosContextGeneratorConfExtend;
import com.wxw.wordsegandpos.model.WordSegAndPosME;
import com.wxw.wordsegandpos.model.WordSegAndPosModel;
import com.wxw.wordsegandpos.model.WordSegAndPosModelLoader;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 * 中文句法分析评估类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisForChinaEvalRun {

	private static void usage(){
		System.out.println(SyntacticAnalysisForChinaEvalRun.class.getName() + 
				"-data <corpusFile>"
				+ "-gold <goldFile> -error <errorFile> -encoding <encoding>" + " [-cutoff <num>] [-iters <num>]");
	}
	
	public static void eval(File trainFile, TrainingParameters params, File goldFile, String encoding, File errorFile) throws IOException{
		long start = System.currentTimeMillis();
		SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen = new SyntacticAnalysisContextGeneratorConf();
		WordSegAndPosModel posmodel = new WordSegAndPosModelLoader().load(new File("data\\model\\pos\\posmodelbinary.txt"));
		
		SyntacticAnalysisModelForChunk chunkmodel= SyntacticAnalysisMEForChunk.train(trainFile, params, contextGen, encoding);
		SyntacticAnalysisModelForBuildAndCheck buildandcheckmodel = SyntacticAnalysisMEForBuildAndCheck.train(trainFile, params, contextGen, encoding);
        System.out.println("训练时间： " + (System.currentTimeMillis() - start));
        
        WordSegAndPosContextGenerator generator = new WordSegAndPosContextGeneratorConfExtend();
		WordSegAndPosME postagger = new WordSegAndPosME(posmodel, generator);	
        SyntacticAnalysisMEForChunk chunktagger = new SyntacticAnalysisMEForChunk(chunkmodel,contextGen);
        SyntacticAnalysisMEForBuildAndCheck buildandchecktagger = new SyntacticAnalysisMEForBuildAndCheck(buildandcheckmodel,contextGen);
        
        SyntacticAnalysisMeasure measure = new SyntacticAnalysisMeasure();
        SyntacticAnalysisEvaluatorForChina evaluator = null;
        SyntacticAnalysisErrorPrinter printer = null;
        if(errorFile != null){
        	System.out.println("Print error to file " + errorFile);
        	printer = new SyntacticAnalysisErrorPrinter(new FileOutputStream(errorFile));    	
        	evaluator = new SyntacticAnalysisEvaluatorForChina(postagger,chunktagger,buildandchecktagger,printer);
        }else{
        	evaluator = new SyntacticAnalysisEvaluatorForChina(postagger,chunktagger,buildandchecktagger);
        }
        evaluator.setMeasure(measure);
        ObjectStream<String> linesStream = new PlainTextByLineStream(new FileInputStreamFactory(goldFile), encoding);
        ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(linesStream);
        evaluator.evaluate(sampleStream);
        SyntacticAnalysisMeasure measureRes = evaluator.getMeasure();
        System.out.println("标注时间： " + (System.currentTimeMillis() - start));
        System.out.println(measureRes);
        
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length < 1){
            usage();
            return;
        }
        String trainFile = null;
        String goldFile = null;
        String errorFile = null;
        String encoding = null;
        int cutoff = 3;
        int iters = 100;
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-data"))
            {
                trainFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-gold"))
            {
                goldFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-error"))
            {
                errorFile = args[i + 1];
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

        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));
        if (errorFile != null)
        {
            eval(new File(trainFile), params, new File(goldFile), encoding, new File(errorFile));
        }
        else
            eval(new File(trainFile), params, new File(goldFile), encoding, null);
	}
}
