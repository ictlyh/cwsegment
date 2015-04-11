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
	
	// 根据测试语句得到观察序列
	public static void genSequence(String sentence, HashMap<Character, Integer> dict, int[] O) {
		for(int i = 0; i < sentence.length(); i++) {
			int index = dict.get(sentence.charAt(i));
			O[i] = index;
		}
	}
	
	// 根据状态序列切分测试语句
	public static void printSegment(String sentence, int[] p) {
		String line = null;
		for(int i = 0; i < p.length; i++) {
			if(p[i] == 2 || p[i] == 3) {
				line += sentence.charAt(i) + " ";
			} else
				line += sentence.charAt(i);
		}
		System.out.println(line);
	}
	
	// 从文件中读入中文字符构建中文字符哈希表
	public static void readDict(String file, String charSet, HashMap<Character, Integer> dict) {
		try {
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file),charSet));
            String line = null;
            dict = new HashMap<Character, Integer>();
            while ((line = br.readLine()) != null) {
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