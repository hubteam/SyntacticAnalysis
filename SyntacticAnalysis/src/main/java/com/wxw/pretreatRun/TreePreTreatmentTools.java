package com.wxw.pretreatRun;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 树预处理运行工具类
 * @author 王馨苇
 *
 */
public class TreePreTreatmentTools {

	public static void main(String[] args) throws UnsupportedOperationException, FileNotFoundException, IOException {
		String cmd = args[0];
		if(cmd.equals("-pretrain")){
			String frompath = args[1];
			String topath = args[2];
			TreePreTreatment.pretreatment(frompath,topath);
			System.out.println("success");
		}
	}
}
