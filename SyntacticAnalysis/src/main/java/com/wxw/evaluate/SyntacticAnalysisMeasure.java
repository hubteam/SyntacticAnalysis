package com.wxw.evaluate;

import java.util.List;

import com.wxw.tool.EvaluationTools;
import com.wxw.tree.TreeNode;

/**
 * 句法分析指标计算
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisMeasure {

	private long notDecodeTreeCount = 0;//统计不能解析成一颗完整的树的个数
    private long selected;
    private long target;
    private long truePositive;
    
	/**
	 * 统计不能解析成一颗完整的树的个数
	 * @param buildAndCheckTree 完整的树
	 */
	public void countNodeDecodeTrees(TreeNode buildAndCheckTree){
		if(buildAndCheckTree == null){
			notDecodeTreeCount++;
		}
	}
	
	/**
	 * 更新指标的计数
	 * @param etRef 参考的树
	 * @param etPre 预测的树
	 */
	public void update(List<EvaluationTools> etRef,List<EvaluationTools> etPre){
		for (int i = 0; i < etPre.size(); i++) {
			if(etRef.contains(etPre.get(i))){
				truePositive++;
			}
		}
		selected += etPre.size();
        target += etRef.size();
	}

	@Override
	public String toString() {
		return "不能解析的树的个数："+notDecodeTreeCount+"\n"
				+"Precision: " + Double.toString(getPrecisionScore()) + "\n"
                + "Recall: " + Double.toString(getRecallScore()) + "\n" 
        		+ "F-Measure: "
                + Double.toString(getMeasure()) + "\n";
	}
	
	/**
	 * 精确率
	 * @return
	 */
    public double getPrecisionScore() {
        return selected > 0 ? (double) truePositive / (double) selected : 0;
    }

    /**
     * 召回率
     * @return
     */
    public double getRecallScore() {
        return target > 0 ? (double) truePositive / (double) target : 0;
    }
    
    /**
     * F值
     * @return
     */
    public double getMeasure() {
        if (getPrecisionScore() + getRecallScore() > 0) {
            return 2 * (getPrecisionScore() * getRecallScore())
                    / (getPrecisionScore() + getRecallScore());
        } else {
            // cannot divide by zero, return error code
            return -1;
        }
    }
}
