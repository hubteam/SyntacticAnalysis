package com.wxw.depRun;

import java.io.File;
import java.io.IOException;

import com.wxw.evaluate.SyntacticAnalysisEvaluateMonitor;
import com.wxw.evaluate.SyntacticAnalysisMeasure;
import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.feature.SyntacticAnalysisContextGeneratorConf;
import com.wxw.modelBystep.SyntacticAnalysisEvaluatorForChina;
import com.wxw.modelBystep.SyntacticAnalysisMEForBuildAndCheck;
import com.wxw.modelBystep.SyntacticAnalysisMEForChunk;
import com.wxw.modelBystep.SyntacticAnalysisModelForBuildAndCheck;
import com.wxw.modelBystep.SyntacticAnalysisModelForChunk;
import com.wxw.stream.FileInputStreamFactory;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.stream.SyntacticAnalysisSampleStream;
import com.wxw.tree.HeadTreeNode;
import com.wxw.wordsegandpos.feature.WordSegAndPosContextGenerator;
import com.wxw.wordsegandpos.feature.WordSegAndPosContextGeneratorConfExtend;
import com.wxw.wordsegandpos.model.WordSegAndPosME;
import com.wxw.wordsegandpos.model.WordSegAndPosModel;
import com.wxw.wordsegandpos.model.WordSegAndPosModelLoader;

import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.eval.CrossValidationPartitioner;
/**
 * 中文句法分析交叉验证
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisForChinaCrossValidationRun {

	private final String languageCode;

    private final TrainingParameters params;

    private SyntacticAnalysisEvaluateMonitor[] listeners;
    
    public SyntacticAnalysisForChinaCrossValidationRun(String languageCode,TrainingParameters trainParam,SyntacticAnalysisEvaluateMonitor... listeners){
    	this.languageCode = languageCode;
        this.params = trainParam;
        this.listeners = listeners;
    }
    
    public void evaluate(ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> samples, int nFolds, SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen) throws IOException{
    	CrossValidationPartitioner<SyntacticAnalysisSample<HeadTreeNode>> partitioner = new CrossValidationPartitioner<SyntacticAnalysisSample<HeadTreeNode>>(samples, nFolds);
		int run = 1;
		//小于折数的时候
		while(partitioner.hasNext()){
			System.out.println("Run"+run+"...");
			CrossValidationPartitioner.TrainingSampleStream<SyntacticAnalysisSample<HeadTreeNode>> trainingSampleStream = partitioner.next();
			WordSegAndPosModel posmodel = new WordSegAndPosModelLoader().load(new File("data\\model\\pos\\posmodelbinary.txt"));
			SyntacticAnalysisModelForChunk chunkmodel= SyntacticAnalysisMEForChunk.train(languageCode, trainingSampleStream, params, contextGen);
			trainingSampleStream.reset();
			SyntacticAnalysisModelForBuildAndCheck buildandcheckmodel = SyntacticAnalysisMEForBuildAndCheck.train(languageCode, trainingSampleStream, params, contextGen);

			WordSegAndPosContextGenerator generator = new WordSegAndPosContextGeneratorConfExtend();
			WordSegAndPosME postagger = new WordSegAndPosME(posmodel, generator);	
	        SyntacticAnalysisMEForChunk chunktagger = new SyntacticAnalysisMEForChunk(chunkmodel,contextGen);
	        SyntacticAnalysisMEForBuildAndCheck buildandchecktagger = new SyntacticAnalysisMEForBuildAndCheck(buildandcheckmodel,contextGen);
	        
	        SyntacticAnalysisEvaluatorForChina evaluator = new SyntacticAnalysisEvaluatorForChina(postagger,chunktagger,buildandchecktagger, listeners);
			SyntacticAnalysisMeasure measure = new SyntacticAnalysisMeasure();
			
			evaluator.setMeasure(measure);
	        //设置测试集（在测试集上进行评价）
	        evaluator.evaluate(trainingSampleStream.getTestSampleStream());
	        
	        System.out.println(measure);
	        run++;
		}
    }
    
    private static void usage(){
    	System.out.println(SyntacticAnalysisForChinaCrossValidationRun.class.getName() + " -data <corpusFile> -encoding <encoding> -type<algorithm>" + "[-cutoff <num>] [-iters <num>] [-folds <nFolds>] ");
    }
    
    public static void main(String[] args) throws IOException {
    	if (args.length < 1)
        {
            usage();
            return;
        }

        int cutoff = 3;
        int iters = 100;
        int folds = 10;
        File corpusFile = null;
        String encoding = "UTF-8";
        String type = "MAXENT";
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-data"))
            {
                corpusFile = new File(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-encoding"))
            {
                encoding = args[i + 1];
                i++;
            }
            else if (args[i].equals("-type"))
            {
                type = args[i + 1];
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
            else if (args[i].equals("-folds"))
            {
                folds = Integer.parseInt(args[i + 1]);
                i++;
            }
        }

        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));
        params.put(TrainingParameters.ALGORITHM_PARAM, type.toUpperCase());
        
        SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen = new SyntacticAnalysisContextGeneratorConf();
        System.out.println(contextGen);
        ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStreamFactory(corpusFile), encoding);       
        ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(lineStream);
        SyntacticAnalysisForChinaCrossValidationRun run = new SyntacticAnalysisForChinaCrossValidationRun("zh",params);
        run.evaluate(sampleStream,folds,contextGen);
	}
}
