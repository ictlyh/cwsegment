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
import java.util.Stack;

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
	public Segment(String trainFile, String trainCharset) {
		super();
		this.trainFile = trainFile;
		this.trainCharset = trainCharset;
		this.cwLib = "lib/cwdict-3785.txt";
		this.cwLibCharset = "UTF-8";
	}

	public String hMMSegment(String sentence) {
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
	
	public void hMMSegment(String testFile, String testCharset, String resultFile) {
		
		/* 使用训练文件的时候每次重新构建HMM文件，而不是从HMM文件读入 */
		HMM hMM = new HMM(5, 3790);
		hMM.buildPiAndMatrixA(trainFile, trainCharset);
		hMM.buildMatrixB(trainFile, trainCharset, cwLib, cwLibCharset);
		//hMM.readHMM("hmm.txt", "UTF-8");
		//hMM.printHMM("hmm.txt", "UTF-8");
		
		HashMap<Character, Integer> dict = new HashMap<Character, Integer>();
		MyUtil.readDict(cwLib, cwLibCharset, dict);
		String sentence = null;
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), testCharset));
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
	
	public void backwardMaximiumMatchSegment(String testFile, String testCharset, String resultFile) {
		HashMap<String, Integer> dict = new HashMap<String, Integer>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(trainFile), trainCharset));
			String line = null;
			while((line = br.readLine()) != null) {
				if(line.length() != 0) {
					if(!dict.containsKey(line)) {
						dict.put(line, dict.size());
					}
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Stack<String> st_word = new Stack<String>();
		Stack<String> st_sentence = new Stack<String>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), testCharset));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFile), "UTF-8"));
			String line = null;
			while((line = br.readLine()) != null) {
				if(line.length() == 0) {
					continue;
				}
				else if(line.length() == 1) {
					st_word.push("\n");
					st_word.push(line);
				} else {
					st_word.push("\n");
					String last = line.substring(0);
					while(last.length() > 0) {
						if(last.length() == 1) {
							st_word.push(last);
							break;
						} else {
							for(int i = 0; i < last.length(); i++) {
								String seg = last.substring(i);
								if(seg.length() == 1 || dict.containsKey(seg)) {
									st_word.push(seg);
									last = last.substring(0, i);
									break;
								}
							}
						}
					}
				}
			}
			
			line = "";
			while(!st_word.empty()) {
				String tmp = st_word.pop();
				if(tmp.equals("\n")) {
					st_sentence.push(line);
					line = "";
				} else {
					line = line + tmp + " "; 
				}
			}
			while(!st_sentence.empty()) {
				bw.write(st_sentence.pop());
				bw.newLine();
			}
			br.close();
			bw.close();
		}  catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}