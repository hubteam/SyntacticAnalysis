package com.wxw.feature;

import java.util.ArrayList;
import java.util.List;

/**
 * 英文词性标注模型特征
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisContextGeneratorConfForPos implements SyntacticAnalysisContextGeneratorForPos{

	@Override
	public String[] getContext(int index, String[] words, String[] poses, Object[] arg3) {
		String w1, w2, w0, w_1, w_2;
        w1 = w2 = w0 = w_1 = w_2 = null;
        String t_1 = null;
        String t_2 = null;

        w0 = words[index];
        if (words.length > index + 1) {
            w1 = words[index+1];
            if (words.length > index + 2) {
                w2 = words[index+2];
            }
        }

        if (index - 1 >= 0) {
            w_1 = words[index-1];
            t_1 = poses[index-1];
            if (index - 2 >= 0) {
                w_2 = words[index-2];
                t_2 = poses[index-2];
            }
        }
        List<String> features = new ArrayList<String>();
      //这里特征有两种，当前词的个数小于，与大于等于5
        //下面这部分特征是共有的
        if(w_1 != null){

        	features.add("w_1="+w_1);

        }
        if(w_2 != null){

        	features.add("w_2="+w_2);
        }
        if(w1 != null){

        	features.add("w1="+w1);

        }
        if(w2 != null){
        	
        	features.add("w2="+w2);
        	
        }
        if(t_1 != null){
        	
        	features.add("t_1="+t_1);
        	
        }
        if(t_2 != null && t_1 != null){
        	
        	features.add("t_2t_1="+t_2+t_1);
        	
        }
        //下面部分的特征根据词语出现的次数进行选择
        if(FeatureForPosTools.overFive(w0)){
        	
        	features.add("w0="+w0);
        	
        }else{
        	
        	features.add("prefix1="+w0.charAt(0));
        	
        	
        	features.add("prefix2="+w0.charAt(1));
        	
        	
        	features.add("prefix3="+w0.charAt(2));
        	
        	
        	features.add("prefix4="+w0.charAt(3));
        	
        	
        	features.add("suffix1="+w0.charAt(w0.length()-1));
        	
        	
        	features.add("suffix2="+w0.charAt(w0.length()-2));
        	
        	
        	features.add("suffix3="+w0.charAt(w0.length()-3));
        	
        
        	features.add("suffix4="+w0.charAt(w0.length()-4));
        	
        	
        	if(w0.contains("-")){
        		features.add("hypen="+1);
        	}else{
        		features.add("hypen="+0);
        	}
        	
        	
        	if(FeatureForPosTools.isAlbDigit(w0)){
        		features.add("number="+1);
        	}else{
        		features.add("number="+0);
        	}
        	
        	
        	if(FeatureForPosTools.isLetter(w0)){
        		features.add("uppercase="+1);
        	}else{
        		features.add("uppercase="+0);
        	}
        	
        }
        String[] contexts = features.toArray(new String[features.size()]);
        return contexts;
	}

}
