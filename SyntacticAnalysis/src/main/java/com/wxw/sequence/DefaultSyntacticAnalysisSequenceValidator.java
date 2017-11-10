package com.wxw.sequence;

import java.util.List;

import com.wxw.tree.TreeNode;

/**
 * 序列验证实现类
 * @author 王馨苇
 *
 */
public class DefaultSyntacticAnalysisSequenceValidator implements SyntacticAnalysisSequenceValidator{

	/**
	 * 
	 * @param i 当前位置
	 * @param character 字序列
	 * @param tags 字的标记序列
	 * @param words 词语序列
	 * @param chunkTree chunk步得到的树
	 * @param buildAndCheckTree buildAndCheck步得到的树
	 * @param out 当前位置的结果
	 * @return
	 */
	@Override
	public boolean validSequence(int i, List<String> words, List<String> poses, List<String> tags,
			List<TreeNode> chunkTree, List<List<TreeNode>> buildAndCheckTree, String out) {
		if(i < words.size()){//词性标记的验证
			
		}else if(i < 2*words.size()){//chunk步验证
			
		}else{//buildAndCheck步验证
			
		}
		return false;
	}

}
