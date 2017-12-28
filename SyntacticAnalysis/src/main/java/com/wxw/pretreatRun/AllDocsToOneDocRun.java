package com.wxw.pretreatRun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 将199个文件转成一个文件
 * @author 王馨苇
 *
 */
public class AllDocsToOneDocRun {
	
	/**
	 * 将199个树库文档，转成一个
	 * @param frompath 原199个树库文档
	 * @param topath 得到的那个树库文档
	 * @throws IOException
	 */
	public static void allDocsToOneDocRun(String frompath, String topath) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(topath)));
		BufferedReader br = null;
		String filename = "";
		for (int i = 1; i < 200; i++) {
			if(i < 10){
				filename = "000" + i;
			}else if(i < 100){
				filename = "00" + i;
			}else{
				filename = "0" + i;
			}
			br = new BufferedReader(new FileReader(new File(frompath+"\\wsj_"+filename+".mrg")));
			String line = "";
			while((line = br.readLine()) != null){
				if(!line .equals("")){
					bw.write(line);
					bw.newLine();
				}
			}
		}
		System.out.println("success");
		bw.close();
		br.close();
	}
	
	public static void main(String[] args) throws IOException {
		String cmd = args[0];
		if(cmd.equals("-combine")){
			String frompath = args[1];//读文件的路径
			String topath = args[2];//输出文件的路径
			AllDocsToOneDocRun.allDocsToOneDocRun(frompath,topath);
		}
	}
}
