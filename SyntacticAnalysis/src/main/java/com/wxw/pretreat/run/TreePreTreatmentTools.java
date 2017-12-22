package com.wxw.pretreat.run;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.wxw.tree.TreePreTreatment;

/**
 * 树预处理运行工具类
 * @author 王馨苇
 *
 */
public class TreePreTreatmentTools {

	public static void main(String[] args) throws UnsupportedOperationException, FileNotFoundException, IOException {
		String cmd = args[0];
		if(cmd.equals("-pretrain")){
			String path = args[1];
			TreePreTreatment.pretreatment(path);
			System.out.println("success");
		}
	}
}
