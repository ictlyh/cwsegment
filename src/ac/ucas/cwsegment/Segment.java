/**
 * Project Name:CWSegment
 * File Name:Segment.java
 * Package Name:ac.ucas.cwsegment
 * Date:2015-4-12下午12:58:20
 * Copyright (c) 2015, luoyuanhao@software.ict.ac.cn All Rights Reserved.
 */
/**
 * ClassName: Segment
 * Function: TODO ADD FUNCTION.
 * @author yhluo
 * @version 
 */

package ac.ucas.cwsegment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;

public class Segment {
	private String trainFile;
	private String trainCharset;
	private String cwLib;
	private String cwLibCharset;

	public Segment() {
		trainFile = "lib/pku_training.utf8";
		trainCharset = "UTF-8";
		cwLib = "lib/cwdict-3785.txt";
		cwLibCharset = "UTF-8";
	}
	public Segment(String trainFile, String trainCharset, String cwLib, String cwLibCharset) {
		super();
		this.trainFile = trainFile;
		this.trainCharset = trainCharset;
		this.cwLib = cwLib;
		this.cwLibCharset = cwLibCharset;
	}

	public String segment(String sentence) {
		sentence = MyUtil.delSpace(sentence);
		HMM hMM = new HMM(5, 3790);
		//hMM.buildPiAndMatrixA(trainFile, trainCharset);
		//hMM.buildMatrixB(trainFile, trainCharset, cwLib, cwLibCharset);
		hMM.readHMM("hmm.txt", "UTF-8");
		//hMM.printHMM("hmm.txt", "UTF-8");
		int T = sentence.length();							// 测试语句长度即为时间
		int[] O = new int[T + 1];							// 观察序列
		int[] q = new int[T + 1];							// 状态序列
		double pprob = 0.0;
		HashMap<Character, Integer> dict = new HashMap<Character, Integer>();
		MyUtil.readDict(cwLib, cwLibCharset, dict);
		MyUtil.genSequence(sentence, dict, O);
		System.out.println("The symbol sequence is : " + Arrays.toString(O));
		hMM.viterbi(T, O, q, pprob);
		System.out.println("The state sequence is : " + Arrays.toString(q));
		return MyUtil.printSegment(sentence, q, dict);
	}
	
	public void segment(String testFile, String resultFile) {
		
		HMM hMM = new HMM(5, 3790);
		//hMM.buildPiAndMatrixA(trainFile, trainCharset);
		//hMM.buildMatrixB(trainFile, trainCharset, cwLib, cwLibCharset);
		hMM.readHMM("hmm.txt", "UTF-8");
		//hMM.printHMM("hmm.txt", "UTF-8");
		
		HashMap<Character, Integer> dict = new HashMap<Character, Integer>();
		MyUtil.readDict(cwLib, cwLibCharset, dict);
		String sentence = null;
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFile), "UTF-8"));
			String line = null;
			while((line = br.readLine()) != null) {
				sentence = MyUtil.delSpace(line);
				if(sentence.length() == 0)
					continue;
				int T = sentence.length();							// 测试语句长度即为时间
				int[] O = new int[T + 1];							// 观察序列
				int[] q = new int[T + 1];							// 状态序列
				double pprob = 0.0;
				
				MyUtil.genSequence(sentence, dict, O);
				//System.out.println("The symbol sequence is : " + Arrays.toString(O));
				hMM.viterbi(T, O, q, pprob);
				//System.out.println("The state sequence is : " + Arrays.toString(q));
				bw.write(MyUtil.printSegment(sentence, q, dict));
				bw.newLine();	
			}
			br.close();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}