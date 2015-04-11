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
		MyUtil myUtil = new MyUtil();
		HMM hMM = new HMM(4, 5168);
		hMM.buildPiAndMatrixA("dic.utf8", "utf8");
		hMM.buildMatrixB("dic.utf8", "utf8");
		//hMM.readHMM("hmm.txt", "utf8");
		hMM.printHMM("hmm.txt", "utf8");
		int T = 4;
		int[] O = null;
		double[][] delta = null;
		int[][] psi = null;
		int[] q = null;
		double[] pprob = null;
		String sentence = "这是一个测试语句，测试分词效果";
		myUtil.genSequence(sentence, O);
		hMM.viterbi(T, O, delta, psi, q, pprob);
		myUtil.printSegment(sentence, q);
	}
}
