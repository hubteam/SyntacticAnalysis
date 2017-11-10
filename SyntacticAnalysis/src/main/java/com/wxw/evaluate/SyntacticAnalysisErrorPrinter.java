package com.wxw.evaluate;

import java.io.OutputStream;
import java.io.PrintStream;

import com.wxw.stream.SyntacticAnalysisSample;

public class SyntacticAnalysisErrorPrinter extends SyntacticAnalysisEvaluateMonitor{

private PrintStream errOut;
	
	public SyntacticAnalysisErrorPrinter(OutputStream out){
		errOut = new PrintStream(out);
	}
	
	/**
	 * 样本和预测的不一样的时候进行输出
	 * @param reference 参考的样本
	 * @param predict 预测的结果
	 */
	@Override
	public void missclassified(SyntacticAnalysisSample reference, SyntacticAnalysisSample predict) {
		 errOut.println("样本的结果：");
		 
		 errOut.println();
		 errOut.println("预测的结果：");
		
		 errOut.println();
	}
}
