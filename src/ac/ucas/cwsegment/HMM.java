/**
 * Project Name:CWSegment
 * File Name:HMM.java
 * Package Name:ac.ucas.cwsegment
 * Date:201N + 1-4-11下午4:33:40
 * Copyright (c) 201N + 1, luoyuanhao@software.ict.ac.cn All Rights Reserved.
 */
/**
 * ClassName: HMM
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

public class HMM {
	
	private int N;				// 模型中状态的数目
	private int M;				// 每个状态可能输出的观察符号数目
	private double[][] A;		// 状态转移概率矩阵
	private double[][] B;		// 观察符号概率矩阵
	private double[] pi;		// 初始状态概率分布
	
	public HMM() {
		super();
	}
	
	public HMM(int n, int m) {
		super();
		N = n;
		M = m;
		A = new double[N + 1][N + 1];
		B = new double[N + 1][M + 1];
		pi = new double[N + 1];
	}
	
	public int getN() {
		return N;
	}

	public void setN(int n) {
		N = n;
	}

	public int getM() {
		return M;
	}

	public void setM(int m) {
		M = m;
	}

	public double getA(int i, int j) {
		return A[i][j];
	}

	public void setA(double a, int i, int j) {
		A[i][j] = a;
	}

	public double getB(int i, int j) {
		return B[i][j];
	}

	public void setB(double b, int i, int j) {
		B[i][j] = b;
	}

	public double pi(int i) {
		return pi[i];
	}

	public void setPi(double pi, int i) {
		this.pi[i] = pi;
	}

	// 从文件中读入HMM数据，即N，M，A，B，pi
	/* HMM file format:
	---------------------------------------------
	N= <number of states>
	M= <number of symbols>
	A:
	a11 a12 ... a1N
	a21 a22 ... a2N
	 .   .   .   .
	aN1 aN2 ... aNN
	B:
	b11 b12 ... b1M
	b21 b22 ... b2M
	 .   .   .   .
	bN1 bN2 ... bNM
	Pi:
	pi1 pi2 ... piN
	 */
	public void readHMM(String file, String charSet) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charSet));
			String line = null;
			String[] words = null;

			// 读入N
			line = br.readLine();
			words = line.split(" ");	
			N = Integer.parseInt(words[1]);			
			
			// 读入M
			line = br.readLine();
			words = line.split(" ");
			M = Integer.parseInt(words[1]);	
			
			/* 为A B pi 分配空间，0地址不用 */
			A = new double[N + 1][N + 1];
			B = new double[N + 1][M + 1];
			pi = new double[N + 1];
			
			/* 读入A: */
			line = br.readLine();	
			/* 读入状态转移概率矩阵A */
			for(int i = 1; i <= N; i++) {
				line = br.readLine();
				String[] values = line.split(" ");
				for(int j = 1; j <= N; j++) {
					A[i][j] = Double.parseDouble(values[j - 1]);
				}
			}
			
			/* 读入B: */
			line = br.readLine();
			/* 读入观察符号概率矩阵B */
			for(int i = 1; i <= N; i++) {
				line = br.readLine();
				String[] values = line.split(" ");
				for(int j = 1; j <= M; j++) {
					B[i][j] = Double.parseDouble(values[j - 1]);
				}
			}
			
			/* 读入Pi: */
			line = br.readLine();
			/* 读入初始状态概率分布pi */
			line = br.readLine();
			String[] values = line.split(" ");
			for(int i = 1; i <= N; i++) {
				pi[i] =Double.parseDouble(values[i - 1]);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 写HMM数据到文件
	public void printHMM(String file, String charSet) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charSet));
			bw.write("N= " + N + "\n");		// 写入N
			bw.write("M= " + M + "\n");		// 写入M

			bw.write("A:\n");
			/* 写入状态转移概率矩阵A */
			for(int i = 1; i <= N; i++) {
				String line = "";
				for(int j = 1; j < N; j++) {
					line += Double.toString(A[i][j]) + " ";
				}
				line += Double.toString(A[i][N]) + "\n";
				bw.write(line);
			}
			
			bw.write("B:\n");
			/* 写入观察符号概率矩阵B */
			for(int i = 1; i <= N; i++) {
				String line = "";
				for(int j = 1; j < M; j++) {
					line += Double.toString(B[i][j]) + " ";
				}
				line += Double.toString(B[i][M]) + "\n";
				bw.write(line);
			}
			
			bw.write("Pi:\n");
			/* 写入初始状态概率分布 */
			String line = "";
			for(int i = 1; i < N; i++) {
				line += Double.toString(pi[i]) + " ";
			}
			line += Double.toString(pi[N]) + "\n";
			bw.write(line);
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 从训练文件构建状态转移矩阵A和初始概率分布pi
	public void buildPiAndMatrixA(String file, String charSet) {
		/**
         * count matrix:
         *    0   1 2 3 4 5
         *    ALL B M E S $
         * 0B *   * * * * *
         * 1M *   * * * * *
         * 2E *   * * * * *
         * 3S *   * * * * *
         * 4$ *	  * * * * *
         * NOTE:
         *  count[1][0] or count[2][0] is the total number of complex words
         *  count[3][0] is the total number of single words
         *  count[4][0] is the number of begin lines
         */
		long[][] count = new long[5][N + 1];
		try {
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file),charSet));
            String line = null;
            String last = null;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
            	lineNum++;
            	last = null;
                String[] words = line.split(" ");
                for (int i = 0; i < words.length; i++) {
                	/* 标点符号作为单字词处理  */
                    String word = words[i].trim();
                    
                    /*读文件时第一行行首会多一个空，需要去除 */
                    if(lineNum == 1 && i == 0)
                    	word = word.substring(1);
                    
                    int length = word.length();
                    
                    if (length < 1)// 词长度为0
                        continue;
                    if (length == 1) {// 词长度为1
                        count[3][0]++;				// 单字次数自增
                        if (last != null) { 		// 不是句首
                            if (last.length() == 1)	// SS模式
                                count[3][4]++;
                            else					// ES模式
                                count[2][4]++;
                        } else {// 第一个词
                        	count[4][0]++;
                        	count[4][4]++;			// $S模式
                        }
                        if(i == words.length - 1) {// 最后一个词
                        	count[3][5]++;			// S$模式
                        }
                    } else {// 词长度大于1
                        count[2][0]++;				// E自增
                        count[0][0]++;				// B自增
                        if (length > 2) {// 词长度大于2
                            count[1][0] += length - 2;// M增加
                            count[0][2]++;			// BE模式
                            if (length > 3) {// 词长度大于3
                                count[1][2] += length - 3;// MM模式
                            }
                            count[1][3]++;			// ME模式
                        } else {// 词长度等于2
                            count[0][3]++;			// BE模式
                        }
                        
                        if (last != null) {// 非句首
                            if (last.length() == 1) {// 上一个是单字词
                                count[3][1]++;		// SB模式
                            } else {
                                count[2][1]++;		// EB模式
                            }
                        } else {// 第一个词
                        	count[4][0]++;
                        	count[4][1]++;			// $B模式
                        }
                        
                        if(i == words.length - 1) {// 最后一个词
                        	count[2][5]++;			// E$模式
                        }
                    }
                    last = word;
                }
                //System.out.println("Finish " + words.length + " words ...");
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        for (int i = 0; i < count.length; i++)
            System.out.println(Arrays.toString(count[i]));
        System.out.println(" ===== Pi array is: ===== ");
        long allWordCount = count[2][0] + count[3][0] + count[4][0];
        pi[1] = (double)count[2][0] / allWordCount;
        pi[2] = 0.0;
        pi[3] = 0.0;
        pi[4] = (double)count[3][0] / allWordCount;
        pi[5] = (double)count[4][0] / allWordCount;
        System.out.println(Arrays.toString(pi));
        System.out.println(" ===== A matrix is: ===== ");
        for (int i = 1; i <= N; i++)
            for (int j = 1; j <= N; j++)
                A[i][j] = (double)count[i - 1][j]/ count[i - 1][0];
        for (int i = 1; i <= N; i++)
            System.out.println(Arrays.toString(A[i]));
	}
	
	// 从训练文件构建观察符号矩阵B
	public void buildMatrixB(String file, String charSet, String charMapFile, String charMapCharset) {
		/**
         * Chinese Character count => M
         * 
         * count matrix:
         *     0   1  2  3  N ...  M
         *    ALL C1 C2 C3 CN ... CM
         * 0B  *  *  *  *  *  ... *
         * 1M  *  *  *  *  *  ... *
         * 2E  *  *  *  *  *  ... *
         * 3S  *  *  *  *  *  ... *
         * 
         * NOTE:
         *  count[0][0] is the total number of begin count
         *  count[1][0] is the total number of middle count
         *  count[2][0] is the total number of end count
         *  count[3][0] is the total number of single cound
         *  
         *  B row -> N
         *  B col -> M + 1
         */
        long[][] count = new long[N][M + 1];
        for (int row = 0; row < count.length; row++) {
        	// 加一平滑
            Arrays.fill(count[row], 1);
            count[row][0] = M;
        }
        
        HashMap<Character, Integer> dict = new HashMap<Character, Integer>();
        /* 标点符号应作为single word读入字典  */
        MyUtil.readDict(charMapFile, charMapCharset, dict);
        
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file),charSet));
            String line = null;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
            	lineNum++;
                String[] words = line.split(" ");
                for (int i = 0; i < words.length; i++) {
                	String word = words[i];
                	
                	/*读文件时第一行行首会多一个空，需要处理掉 */
                	if(lineNum == 1 && i == 0)
                		word = word.substring(1);
                	
                	int length = word.length();
                	
                    if (length < 1)
                        continue;
                    if (length == 1) {// 词长度为1
                    	/* 如果字库中没有这个字，忽略不处理 */
                    	if(!dict.containsKey(word.charAt(0)))
                    		continue;
                        int index = dict.get(word.charAt(0));
                        count[3][0]++;
                        count[3][index]++;
                    } else {// 词长度大于1
                        for (int j = 0; j < length; j++) {
                        	// 获取词中的每个字
                        	/* 如果字库中没有这个字，忽略不处理 */
                        	if(!dict.containsKey(word.charAt(0)))
                        		continue;
                            int index = dict.get(word.charAt(j));
                            if (j == 0) {// 词的第一个字
                                count[0][0]++;
                                count[0][index]++;
                            } else if (j == length-1) {// 词的最后一个字
                                count[2][0]++;
                                count[2][index]++;
                            } else {// 词中间的字
                                count[1][0]++;
                                count[1][index]++;
                            }
                        }
                    } 
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println(" ===== count ===== ");
        for (int i = 0; i < count.length; i++)
            System.out.println(Arrays.toString(count[i]));
        
        System.out.println(" ========= B matrix =========");
        for (int i = 1; i < N + 1; i++) {
            for (int j = 1; j < M + 1; j++) {
                B[i][j] = (double) count[i - 1][j] / count[i - 1][0];
            }
        }
	}
	
	/* 前向算法求解观察序列的概率
	 * param T: 时间
	 * param O：观察序列
	 * return pprob：观察序列概率
	 */
	public void forward(int T, int[] O, double pprob) {
		int i, j;   /* 状态索引 */
        int t;      /* 时间索引 */
        double sum = 0.0;
        double[][] alpha = new double[T + 1][N + 1];
        
        /* 1. 初始化 */
        for (i = 1; i <= N; i++)
        	alpha[1][i] = pi[i]* B[i][O[1]];
 
        /* 2. 归纳计算 */
        for (t = 1; t < T; t++) {
        	for (j = 1; j <= N; j++) {
        		sum = 0.0;
        		for (i = 1; i <= N; i++)
        			sum += alpha[t][i] * A[i][j];
        		alpha[t + 1][j] = sum * B[j][O[t + 1]];
        	}
        }
 
        /* 3. 求和终结 */
        pprob = 0.0;
        for (i = 1; i <= N; i++)
        	pprob += alpha[T][i];
	}
	
	/* 后向算法求解观察序列的概率
	 * param T: 时间
	 * param O：观察序列
	 * return pprob：观察序列概率
	 */
	public void backward(int T, int[] O, double pprob) {
		int i, j;   /* 状态索引 */
        int t;      /* 时间索引 */
        double sum;
        double[][] beta = new double[T + 1][N + 1];
        
        /* 1. 初始化 */
        for (i = 1; i <= N; i++)
        	beta[T][i] = 1.0;
 
        /* 2. 归纳计算 */
        for (t = T - 1; t >= 1; t--) {
        	for (i = 1; i <= N; i++) {
        		sum = 0.0;
        		for (j = 1; j <= N; j++)
        			sum += A[i][j] * B[j][O[t + 1]] * beta[t + 1][j];
        		beta[t][i] = sum;
        	}
        }
 
        /* 3. 求和终结 */
        pprob = 0.0;
        for (i = 1; i <= N; i++)
        	pprob += beta[1][i];
	}
	
	/* Viterbi算法求解最优状态序列
	 * param T: 时间
	 * param O：观察序列
	 * return q： 状态序列
	 * return pprob：观察序列概率
	 */
	public void viterbi(int T, int[] O, int[] q, double pprob) {
		int i, j;
		int t;
		int	maxvalind;
		double	maxval, val;
		double[][] delta = new double[T + 1][N + 1];
		int[][] psi = new int[T + 1][N + 1];

		/* 1. 初始化  */
		for (i = 1; i <= N; i++) {
			delta[1][i] = pi[i] * B[i][O[1]];
			psi[1][i] = 0;
		}	

		/* 2. 归纳计算 */
		for (t = 2; t <= T; t++) {
			for (j = 1; j <= N; j++) {
				maxval = 0.0;
				maxvalind = 1;	
				for (i = 1; i <= N; i++) {
					val = delta[t - 1][i] * A[i][j];
					if (val > maxval) {
						maxval = val;	
						maxvalind = i;	
					}
				}
				delta[t][j] = maxval * B[j][O[t]];
				psi[t][j] = maxvalind; 
			}
		}

		/* 3. 终结 */
		q[T] = 1;
		pprob = 0.0;
		for (i = 1; i <= N; i++) {
			if (delta[T][i] > pprob) {
				pprob = delta[T][i];
	            q[T] = i;
			}
		}

		/* 4. 路径(状态序列)回溯 */
		for (t = T - 1; t >= 1; t--)
			q[t] = psi[t+1][q[t+1]];
	}
}