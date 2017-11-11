package com.wxw.feature;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 为词性标注特征的生成，生成的工具类
 * @author 王馨苇
 *
 */
public class FeatureForPosTools {

	private HashMap<String,Integer> dict;
	
	static HashSet<Character> hsalbdigit = new HashSet<>();
	
	public FeatureForPosTools(){
		
	}
	
	public FeatureForPosTools(HashMap<String,Integer> dict){
		this.dict = dict;
	}
	static{
		//罗列了半角和全角的情况
		String albdigits = "０１２３４５６７８９0123456789";
		for (int i = 0; i < albdigits.length(); i++) {
			hsalbdigit.add(albdigits.charAt(i));
		}
	}
	
	public static boolean isAlbDigit(char c){
		if(hsalbdigit.contains(c)){
			return true;
		}else{
			return false;
		}	
	}
	
	//判断是否为应为字母（大写，全角半角）【全角半角的差别在于ASCII码】
	public static boolean isLetter(char c){
		if((c>=65 && c<=90) || (c>=65313 && c<=65338)){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isAlbDigit(String w){
		for (int i = 0; i < w.length(); i++) {
			char c = w.charAt(i);
			if(isAlbDigit(c)){
				return true;
			}
		}
		return false;
	}
	
	//判断是否为应为字母（大写，全角半角）【全角半角的差别在于ASCII码】
	public static boolean isLetter(String w){
		for (int i = 0; i < w.length(); i++) {
			char c = w.charAt(i);
			if(isLetter(c)){
				return true;
			}
		}
		return false;
	}

	public HashMap<String,Integer> getDict(){
		return this.dict;
	}
}
