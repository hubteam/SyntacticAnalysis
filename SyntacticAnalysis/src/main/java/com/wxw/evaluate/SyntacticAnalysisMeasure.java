package com.wxw.evaluate;

import java.util.List;

import com.wxw.tree.TreeNode;

/**
 * 句法分析指标计算
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisMeasure {

	private long notDecodeTreeCount = 0;//统计不能解析成一颗完整的树的个数
	
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
	 * @param actionsRef 参考的动作序列
	 * @param actionsPre 预测的动作序列
	 */
	public void update(List<String> actionsRef,List<String> actionsPre){
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "不能解析的树的个数："+notDecodeTreeCount+"\n";
	}
	
	
}
