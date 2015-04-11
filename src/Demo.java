/**
 * Project Name:CWSegment
 * File Name:Demo.java
 * Package Name:
 * Date:2015-4-11下午4:58:24
 * Copyright (c) 2015, luoyuanhao@software.ict.ac.cn All Rights Reserved.
 */
import java.util.HashMap;

/**
 * ClassName: Demo
 * Function: TODO ADD FUNCTION.
 * @author yhluo
 * @version 
 */
import ac.ucas.cwsegment.*;

public class Demo {
	public static void main(String[] args) {
		HMM hMM = new HMM(4, 5168);
		hMM.buildPiAndMatrixA("dic.utf8", "UTF-8");
		hMM.buildMatrixB("dic.utf8", "UTF-8", "cw.dic", "UTF-8");
		//hMM.readHMM("hmm.txt", "UTF-8");
		hMM.printHMM("hmm.txt", "UTF-8");
		int T = 4;
		int[] O = null;
		double[][] delta = null;
		int[][] psi = null;
		int[] q = null;
		double pprob = 0.0;
		String sentence = "这是一个测试语句，测试分词效果";
		HashMap<Character, Integer> dict = null;
		MyUtil.readDict("chinese.dic", "UTF-8", dict);
		MyUtil.genSequence(sentence, dict, O);
		hMM.viterbi(T, O, delta, psi, q, pprob);
		MyUtil.printSegment(sentence, q);
	}
}
