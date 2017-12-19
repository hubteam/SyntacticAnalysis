package com.wxw.evalstructure;

/**
 * 评估需要的工具类
 * @author 王馨苇
 *
 */
public class EvalStructure {

	private String nonterminal;
	private int begin;
	private int end;
	
	public EvalStructure(String nonterminal,int begin,int end){
		this.nonterminal = nonterminal;
		this.begin = begin;
		this.end = end;
	}

	/**
	 * 获得非终结符
	 * @return
	 */
	public String getNonTerminal(){
		return this.nonterminal;
	}
	
	/**
	 * 获得开始的序号
	 * @return
	 */
	public int getBegin(){
		return this.begin;
	}
	
	/**
	 * 获得结束序号
	 * @return
	 */
	public int getEnd(){
		return this.end;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        } else if (obj instanceof EvalStructure) {
        	EvalStructure et = (EvalStructure) obj;
        	if(getNonTerminal().equals(et.getNonTerminal()) && getBegin()== et.getBegin() && getEnd() == et.getEnd()){
    			return true;
    		}else{
    			return false;
    		}
        } else {
            return false;
        }
	}

	@Override
	public String toString() {
		return this.nonterminal+"-("+this.begin+":"+this.end+")";
	}
}
