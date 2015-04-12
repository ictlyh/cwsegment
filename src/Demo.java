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
		String test = "这是  一个测试语句，测试 分词效果";
		Segment seg = new Segment();
		System.out.println(seg.segment(test));
	}
}
