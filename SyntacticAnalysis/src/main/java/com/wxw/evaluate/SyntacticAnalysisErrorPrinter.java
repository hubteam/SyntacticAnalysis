package com.wxw.evaluate;

import java.io.OutputStream;
import java.io.PrintStream;

import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.tree.HeadTreeNode;

/**
 * 错误打印类
 * @author 王馨苇
 *
 */
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
	public void missclassified(SyntacticAnalysisSample<HeadTreeNode> reference, SyntacticAnalysisSample<HeadTreeNode> predict) {
		 errOut.println("样本的结果：");
		 errOut.println(SyntacticAnalysisSample.toSample(reference.getWords(), reference.getActions()).toBracket());
		 errOut.println();
		 errOut.println("预测的结果：");
		 errOut.println(SyntacticAnalysisSample.toSample(predict.getWords(), predict.getActions()).toBracket());
		 errOut.println();
	}
}
