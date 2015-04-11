/**
 * Project Name:CWSegment
 * File Name:HMM.java
 * Package Name:ac.ucas.cwsegment
 * Date:2015-4-11下午4:33:40
 * Copyright (c) 2015, luoyuanhao@software.ict.ac.cn All Rights Reserved.
 */
/**
 * ClassName: HMM
 * Function: TODO ADD FUNCTION.
 * @author yhluo
 * @version 
 */

package ac.ucas.cwsegment;

public class HMM {
	
	private int N;				// 模型中状态的数目
	private int M;				// 每个状态可能输出的观察符号数目
	private double[][] A;		// 状态转移概率矩阵
	private double[][] B;		// 观察符号概率矩阵
	private double[] pi;		// 初始状态概率分布
	
	
	//Creates a new instance of HMM.
	public HMM(int n, int m) {
		super();
		N = n;
		M = m;
	}
	
	//get n
	public int getN() {
		return N;
	}

	//set n to n
	public void setN(int n) {
		N = n;
	}

	//get m
	public int getM() {
		return M;
	}

	//set m to m
	public void setM(int m) {
		M = m;
	}

	//get a
	public double getA(int i, int j) {
		return A[i][j];
	}

	//set a to a
	public void setA(double a, int i, int j) {
		A[i][j] = a;
	}

	//get b
	public double getB(int i, int j) {
		return B[i][j];
	}

	//set b to b
	public void setB(double b, int i, int j) {
		B[i][j] = b;
	}

	//get p
	public double getPi(int i) {
		return pi[i];
	}

	//set p to p
	public void setPi(double pi, int i) {
		this.pi[i] = pi;
	}

	// 从文件中读入HMM数据，即N，M，A，B，pi
	public void readHMM(String file, String charSet) {
		
	}
	
	// 写HMM数据到文件
	public void printHMM(String file, String charSet) {
		
	}
	
	// 从训练文件构建状态转移矩阵和初始概率分布
	public void buildPiAndMatrixA(String file, String charSet) {
		
	}
	
	// 从训练文件构建观察符号矩阵
	public void buildMatrixB(String file, String charSet) {
		
	}
	
	// 前向算法求解观察序列的概率
	public void forward(int T, int[] O, double[][] alpha, double[] pprob) {
		
	}
	
	// 后向算法求解观察序列的概率
	public void backward(int T, int[] O, double[][] beta, double[] pprob) {
		
	}
	
	// Viterbi算法求解最优状态序列
	public void viterbi(int T, int[] O, double[][] delta, int[][] psi,int[] q, double[] pprob) {
		
	}
}
