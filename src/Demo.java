/**
 * Project Name:CWSegment
 * File Name:Demo.java
 * Package Name:
 * Date:2015-4-11下午4:58:24
 * Copyright (c) 2015, luoyuanhao@software.ict.ac.cn All Rights Reserved.
 */

/**
 * ClassName: Demo
 * Function: TODO ADD FUNCTION.
 * @author yhluo
 * @version 
 */
import ac.ucas.cwsegment.*;

public class Demo {
	public static void main(String[] args) {
		// HMM语句测试
		/*String test = "这是  一个训练文本，训练 分词效果";
		Segment seg = new Segment();
		System.out.println(seg.segment(test));*/
		
		// HMM文件测试
		/*Segment seg = new Segment("lib/pku_training.utf8", "UTF-8");
		seg.hMMSegment("lib/pku_test.utf8", "UTF-8", "seg_result.txt");
		System.out.println("Finished");*/
		
		// 反向最大匹配文件测试
		Segment seg = new Segment("lib/pku_training_words.utf8", "UTF-8");
		seg.backwardMaximiumMatchSegment("lib/pku_test.utf8", "UTF-8", "bw_result.txt");
		System.out.println("Finished");
	}
}
