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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

	public double getPi(int i) {
		return pi[i];
	}

	public void setPi(double pi, int i) {
		this.pi[i] = pi;
	}

	// 从文件中读入HMM数据，即N，M，A，B，pi
	/* HMM file format:
	---------------------------------------------
	N <number of states>
	M <number of symbols>
	a11 a12 ... a1N
	a21 a22 ... a2N
	 .   .   .   .
	 .   .   .   .
	 .   .   .   .
	aN1 aN2 ... aNN
	b11 b12 ... b1M
	b21 b22 ... b2M
	 .   .   .   .
	 .   .   .   .
	 .   .   .   .
	bN1 bN2 ... bNM
	pi1 pi2 ... piN
	 */
	public void readHMM(String file, String charSet) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charSet));
			String line = null;
			int n = Integer.parseInt(br.readLine());		// 读入N
			int m = Integer.parseInt(br.readLine());		// 读入M
			setN(n);
			setM(m);
			
			/* 为A B pi 分配空间*/
			A = new double[n + 1][n + 1];
			B = new double[n + 1][m + 1];
			pi = new double[n + 1];
			
			/* 读入状态转移概率矩阵A */
			for(int i = 1; i <= n; i++) {
				line = br.readLine();
				String[] values = line.split(" ");
				for(int j = 1; j <= n; j++) {
					setA(Double.parseDouble(values[j - 1]), i, j);
				}
			}
			
			/* 读入观察符号概率矩阵B */
			for(int i = 1; i <= n; i++) {
				line = br.readLine();
				String[] values = line.split(" ");
				for(int j = 1; j <= m; j++) {
					setB(Double.parseDouble(values[j - 1]), i, j);
				}
			}
			
			/* 读入初始状态概率分布pi */
			line = br.readLine();
			String[] values = line.split(" ");
			for(int i = 1; i <= n; i++) {
				setPi(Double.parseDouble(values[i - 1]), i);
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
			bw.write(Integer.toString(getN()));		// 写入N
			bw.newLine();
			bw.write(Integer.toString(getM()));		// 写入M
			bw.newLine();
			
			/* 写入状态转移概率矩阵A */
			for(int i = 1; i <= getN(); i++) {
				String line = null;
				for(int j = 1; j < getN(); j++) {
					line += Double.toString(getA(i, j)) + " ";
				}
				line += Double.toString(getA(i, getN())) + "\n";
				bw.write(line);
			}
			
			/* 写入观察符号概率矩阵B */
			for(int i = 1; i <= getN(); i++) {
				String line = null;
				for(int j = 1; j < getM(); j++) {
					line += Double.toString(getB(i, j)) + " ";
				}
				line += Double.toString(getB(i, getM())) + "\n";
				bw.write(line);
			}
			
			/* 写入初始状态概率分布 */
			String line = null;
			for(int i = 1; i < getN(); i++) {
				line += Double.toString(getPi(i)) + " ";
			}
			line += Double.toString(getPi(getN())) + "\n";
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
         *   ALL B M E S
         * B *   * * * *
         * M *   * * * *
         * E *   * * * *
         * S *   * * * *
         * 
         * NOTE:
         *  count[2][0] is the total number of complex words
         *  count[3][0] is the total number of single words
         */
		long[][] count = new long[4][5];
		try {
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file),charSet));
            String line = null;
            String last = null;
            while ((line = br.readLine()) != null) {
                String[] words = line.split(" ");
                for (int i = 0; i < words.length; i++) {
                    String word = words[i].trim();
                    int length = word.length();
                    // 词长度为0
                    if (length < 1)
                        continue;
                    // 词长度为1
                    if (length == 1) {
                        count[3][0]++;
                        if (last != null) {
                            if (last.length() == 1)
                                count[3][4]++;
                            else
                                count[2][4]++;
                        }
                    } else {// 词长度大于1
                        count[2][0]++;
                        count[0][0]++;
                     // 词长度大于2
                        if (length > 2) {
                            count[1][0] += length - 2;
                            count[0][2]++;
                         // 词长度大于3
                            if (length > 3) {
                                count[1][2] += length - 3;
                            }
                            count[1][3]++;
                        } else {// 词长度等于2
                            count[0][3]++;
                        }
                        
                        if (last != null) {
                            if (last.length() == 1) {
                                count[3][1]++;
                            } else {
                                count[2][1]++;
                            }
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
        
        for (int i=0; i<count.length; i++)
            System.out.println(Arrays.toString(count[i]));
        System.out.println(" ===== So Pi array is: ===== ");
        double[] pi = new double[5];
        long allWordCount = count[2][0] + count[3][0];
        pi[1] = (double)count[2][0] / allWordCount;
        pi[2] = 0.0;
        pi[3] = 0.0;
        pi[4] = (double)count[3][0] / allWordCount;
        System.out.println(Arrays.toString(pi));
        System.out.println(" ===== And A matrix is: ===== ");
        double[][] A = new double[5][5];
        for (int i = 1; i <= 5; i++)
            for (int j = 1; j <= 5; j++)
                A[i][j] = (double)count[i - 1][j]/ count[i - 1][0];
        for (int i = 1; i <= 5; i++)
            System.out.println(Arrays.toString(A[i]));
	}
	
	// 从训练文件构建观察符号矩阵B
	public void buildMatrixB(String file, String charSet, String charMapFile, String charMapCharset) {
		/**
         * Chinese Character count => 5167
         * 
         * count matrix:
         *   ALL C1 C2 C3 CN C5168
         * B  *  *  *  *  *  1/ALL+5168
         * M  *  *  *  *  *  1/ALL+5168
         * E  *  *  *  *  *  1/ALL+5168
         * S  *  *  *  *  *  1/ALL+5168
         * 
         * NOTE:
         *  count[0][0] is the total number of begin count
         *  count[1][0] is the total number of middle count
         *  count[2][0] is the total number of end count
         *  count[3][0] is the total number of single cound
         *  
         *  B row -> 4
         *  B col -> 5169
         */
        long[][] count = new long[4][5169];
        for (int row = 0; row < count.length; row++) {
            Arrays.fill(count[row], 1);
            count[row][0] = 5168;
        }
        
        HashMap<Character, Integer> dict = new HashMap<Character, Integer>();
        MyUtil.readDict(charMapFile, charMapCharset, dict);
        
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file),charSet));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] words = line.split(" ");
                for (int i = 0; i < words.length; i++) {
                    String word = words[i].trim();
                    if (word.length() < 1)
                        continue;
                    
                    // 词长度为1
                    if (word.length() == 1) {
                        int index = dict.get(word.charAt(0));
                        count[3][0]++;
                        count[3][index]++;
                    } else {// 词长度大于1
                        for (int j = 0; j < word.length(); j++) {
                        	// 获取词中的每个字
                            int index = dict.get(word.charAt(j));
                            // 词的第一个字
                            if (j == 0) {
                                count[0][0]++;
                                count[0][index]++;
                            } else if (j == word.length()-1) {// 词的最后一个字
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
        double[][] B = new double[count.length + 1][count[0].length + 1];
        for (int row = 1; row <= B.length; row++) {
            for (int col = 1; col <= B[row].length; col++) {
                B[row][col] = (double) count[row - 1][col - 1] / count[row - 1][0];
            }
        }
	}
	
	// 前向算法求解观察序列的概率
	public void forward(int T, int[] O, double[][] alpha, double pprob) {
		int i, j;   /* state indices */
        int t;      /* time index */
        double sum;     /* partial sum */
 
        /* 1. Initialization */
        for (i = 1; i <= N; i++)
        	alpha[1][i] = pi[i]* B[i][O[1]];
 
        /* 2. Induction */
        for (t = 1; t < T; t++) {
        	for (j = 1; j <= N; j++) {
        		sum = 0.0;
        		for (i = 1; i <= N; i++)
        			sum += alpha[t][i]* (A[i][j]);
        		alpha[t+1][j] = sum*(B[j][O[t+1]]);
        	}
        }
 
        /* 3. Termination */
        pprob = 0.0;
        for (i = 1; i <= N; i++)
        	pprob += alpha[T][i];
	}
	
	// 后向算法求解观察序列的概率
	public void backward(int T, int[] O, double[][] beta, double pprob) {
		int i, j;   /* state indices */
        int t;      /* time index */
        double sum;
 
        /* 1. Initialization */
        for (i = 1; i <= N; i++)
        	beta[T][i] = 1.0;
 
        /* 2. Induction */
        for (t = T - 1; t >= 1; t--) {
        	for (i = 1; i <= N; i++) {
        		sum = 0.0;
        		for (j = 1; j <= N; j++)
        			sum += A[i][j] * (B[j][O[t+1]])*beta[t+1][j];
        		beta[t][i] = sum;
        	}
        }
 
        /* 3. Termination */
        pprob = 0.0;
        for (i = 1; i <= N; i++)
        	pprob += beta[1][i];
	}
	
	// Viterbi算法求解最优状态序列
	public void viterbi(int T, int[] O, double[][] delta, int[][] psi,int[] q, double pprob) {
		int i, j;
		int t;
		int	maxvalind;
		double	maxval, val;

		/* 1. Initialization  */
		for (i = 1; i <= N; i++) {
			delta[1][i] = pi[i] * B[i][O[1]];
			psi[1][i] = 0;
		}	

		/* 2. Recursion */
		for (t = 2; t <= T; t++) {
			for (j = 1; j <= N; j++) {
				maxval = 0.0;
				maxvalind = 1;	
				for (i = 1; i <= N; i++) {
					val = delta[t-1][i] * A[i][j];
					if (val > maxval) {
						maxval = val;	
						maxvalind = i;	
					}
				}
				
				delta[t][j] = maxval * B[j][O[t]];
				psi[t][j] = maxvalind; 
			}
		}

		/* 3. Termination */
		q[T] = 1;
		pprob = 0.0;
		for (i = 1; i <= N; i++) {
			if (delta[T][i] > pprob) {
				pprob = delta[T][i];
	            q[T] = i;
			}
		}

		/* 4. Path (state sequence) backtracking */
		for (t = T - 1; t >= 1; t--)
			q[t] = psi[t+1][q[t+1]];
	}
}