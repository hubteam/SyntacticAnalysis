package com.wxw.headwords;

import java.util.List;

/**
 * 存储规则集的右部和方向
 * @author 王馨苇
 *
 */
public class Rule {

	private List<String> rightRules;
	private String direction;
	
	public Rule(List<String> rightRules, String direction){
		this.direction = direction;
		this.rightRules = rightRules;
	}
	
	public List<String> getRightRules(){
		return this.rightRules;
	}
	
	public String getDirection(){
		return this.direction;
	}
}
