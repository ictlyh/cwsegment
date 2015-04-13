/**
 * Project Name:CWSegment
 * File Name:MyUtil.java
 * Package Name:ac.ucas.cwsegment
 * Date:2015-4-11下午4:56:39
 * Copyright (c) 2015, luoyuanhao@software.ict.ac.cn All Rights Reserved.
 */
/**
 * ClassName: MyUtil
 * Function: TODO ADD FUNCTION.
 * @author yhluo
 * @version 
 */

package ac.ucas.cwsegment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class MyUtil {
	
	/* 根据测试语句得到观察序列
	 * param sentence：测试语句，不包括空格
	 * param dict：汉字字典(包括标点符号)
	 * return O：观察序列
	 */
	public static void genSequence(String sentence, HashMap<Character, Integer> dict, int[] O) {
		sentence = delSpace(sentence);
		for(int i = 0; i < sentence.length(); i++) {
			/* 如果字库中不存在该字，从句中删除 */
        	if(!dict.containsKey(sentence.charAt(i))) {
        		String tmp = sentence.substring(0, i) + sentence.substring(i + 1);
        		sentence = tmp;
        		i--;
        		continue;
        	}
			int index = dict.get(sentence.charAt(i));
			O[i + 1] = index;
		}
	}
	
	/* 删除测试语句中的空格
	 * param sentence： 测试语句
	 * return ：删除空格后的语句
	 */
	public static String delSpace(String sentence) {
		String res = "";
		for(int i = 0; i < sentence.length(); i++) {
			if(sentence.charAt(i) == ' ')
				continue;
			res += sentence.charAt(i);
		}
		return res;
	}
	
	/* 根据状态序列切分测试语句
	 * param sentence： 测试语句，不包括空格
	 * param q：状态序列
	 * param dict: 汉字哈希表
	 * return line: 分词结果
	 */
	public static String printSegment(String sentence, int[] q, HashMap<Character, Integer> dict) {
		String line = "";
		int j = 1;
		for(int i = 0; i < sentence.length(); i++) {
			if(!dict.containsKey(sentence.charAt(i))) {
				line += " " + sentence.charAt(i) + " ";
			} else {
				if(q[j] == 3 || q[j] == 4) {// 状态序列是E或者S
					line += sentence.charAt(i) + " ";
				} else
					line += sentence.charAt(i);
				j++;
			}
		}
		return line;
	}
	
	/* 从文件中读入汉字构建汉字哈希表
	 * param file: 汉字字典，包括标点符号
	 * param charSet: 文件编码
	 * return dict：汉字哈希表
	 */
	public static void readDict(String file, String charSet, HashMap<Character, Integer> dict) {
		try {
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file),charSet));
            String line = null;
            while ((line = br.readLine()) != null) {
            	/* UTF8文件BOM处理 */
            	if(line.length() > 0) {
            		if((int)line.charAt(0) == 65279) {
            			line = line.substring(1);
            		}
            	}
            	if(line.length() == 0) {// 空行
            		continue;
            	}
            	if(!dict.containsKey(line.charAt(0))) {
            		dict.put(line.charAt(0), dict.size() + 1);
            	}
            }
            br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}