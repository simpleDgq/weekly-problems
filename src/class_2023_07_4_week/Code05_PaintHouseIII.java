package class_2023_07_4_week;

// 在一个小城市里，有 m 个房子排成一排
// 你需要给每个房子涂上 n 种颜色之一（颜色编号为 1 到 n )
// 有的房子去年夏天已经涂过颜色了，所以这些房子不可以被重新涂色
// 我们将连续相同颜色尽可能多的房子称为一个街区
// 比方说 houses = [1,2,2,3,3,2,1,1]
// 它包含 5 个街区  [{1}, {2,2}, {3,3}, {2}, {1,1}]
// 给你一个数组 houses ，一个 m * n 的矩阵 cost 和一个整数 target，其中：
// houses[i]：是第 i 个房子的颜色，0 表示这个房子还没有被涂色
// cost[i][j]：是将第 i 个房子涂成颜色 j+1 的花费
// 请你返回房子涂色方案的最小总花费，使得每个房子都被涂色后，恰好组成 target 个街区
// 如果没有可用的涂色方案，请返回 -1
// 测试链接 : https://leetcode.cn/problems/paint-house-iii/
public class Code05_PaintHouseIII {

	public static int minCost1(int[] houses, int[][] cost, int m, int n, int target) {
		int[][][] dp = new int[m][target + 1][n + 1];
		for (int i = 0; i < m; i++) {
			for (int k = 0; k <= target; k++) {
				for (int c = 0; c <= n; c++) {
					dp[i][k][c] = -1;
				}
			}
		}
		int ans = process1(houses, cost, n, 0, target, 0, dp);
		return ans == Integer.MAX_VALUE ? -1 : ans;
	}

	public static int process1(int[] houses, int[][] cost, int n, int i, int k, int c, int[][][] dp) {
		if (k < 0) {
			return Integer.MAX_VALUE;
		}
		if (i == houses.length) {
			return k == 0 ? 0 : Integer.MAX_VALUE;
		}
		if (dp[i][k][c] != -1) {
			return dp[i][k][c];
		}
		int ans = Integer.MAX_VALUE;
		if (houses[i] != 0) {
			if (houses[i] != c) {
				ans = process1(houses, cost, n, i + 1, k - 1, houses[i], dp);
			} else {
				ans = process1(houses, cost, n, i + 1, k, houses[i], dp);
			}
		} else {
			for (int fill = 1, next; fill <= n; fill++) {
				if (fill == c) {
					next = process1(houses, cost, n, i + 1, k, fill, dp);
				} else {
					next = process1(houses, cost, n, i + 1, k - 1, fill, dp);
				}
				if (next != Integer.MAX_VALUE) {
					ans = Math.min(ans, cost[i][fill - 1] + next);
				}
			}
		}
		dp[i][k][c] = ans;
		return ans;
	}

	public static int minCost2(int[] houses, int[][] cost, int m, int n, int target) {
		int[][] dp = new int[target + 1][n + 1];
		for (int c = 0; c <= n; c++) {
			dp[0][c] = 0;
		}
		for (int k = 1; k <= target; k++) {
			for (int c = 0; c <= n; c++) {
				dp[k][c] = Integer.MAX_VALUE;
			}
		}
		int[] memo = new int[n + 1];
		for (int i = m - 1; i >= 0; i--) {
			if (houses[i] != 0) {
				int houseColor = houses[i];
				for (int k = target; k >= 0; k--) {
					int memory = dp[k][houseColor];
					for (int c = 0; c <= n; c++) {
						if (houseColor != c) {
							dp[k][c] = k == 0 ? Integer.MAX_VALUE : dp[k - 1][houseColor];
						} else {
							dp[k][c] = memory;
						}
					}
				}
			} else {
				for (int k = target; k >= 0; k--) {
					for (int c = 0; c <= n; c++) {
						memo[c] = dp[k][c];
					}
					for (int c = 0; c <= n; c++) {
						int ans = Integer.MAX_VALUE;
						for (int fill = 1, next; fill <= n; fill++) {
							if (fill == c) {
								next = memo[fill];
							} else {
								next = k == 0 ? Integer.MAX_VALUE : dp[k - 1][fill];
							}
							if (next != Integer.MAX_VALUE) {
								ans = Math.min(ans, cost[i][fill - 1] + next);
							}
						}
						dp[k][c] = ans;
					}
				}
			}
		}
		return dp[target][0] == Integer.MAX_VALUE ? -1 : dp[target][0];
	}

	public static int minCost3(int[] houses, int[][] cost, int m, int n, int target) {
		int[][] dp = new int[target + 1][n + 1];
		for (int c = 0; c <= n; c++) {
			dp[0][c] = 0;
		}
		for (int k = 1; k <= target; k++) {
			for (int c = 0; c <= n; c++) {
				dp[k][c] = Integer.MAX_VALUE;
			}
		}
		int[] memo = new int[n + 1];
		int[] minl = new int[n + 2];
		int[] minr = new int[n + 2];
		minl[0] = minr[0] = minl[n + 1] = minr[n + 1] = Integer.MAX_VALUE;
		for (int i = m - 1; i >= 0; i--) {
			if (houses[i] != 0) {
				for (int k = target, memory; k >= 0; k--) {
					memory = dp[k][houses[i]];
					for (int c = 0; c <= n; c++) {
						if (houses[i] != c) {
							dp[k][c] = k == 0 ? Integer.MAX_VALUE : dp[k - 1][houses[i]];
						} else {
							dp[k][c] = memory;
						}
					}
				}
			} else {
				for (int k = target; k >= 0; k--) {
					for (int c = 0; c <= n; c++) {
						memo[c] = dp[k][c];
					}
					for (int fill = 1; fill <= n; fill++) {
						if (k == 0 || dp[k - 1][fill] == Integer.MAX_VALUE) {
							minl[fill] = minl[fill - 1];
						} else {
							minl[fill] = Math.min(minl[fill - 1], cost[i][fill - 1] + dp[k - 1][fill]);
						}
					}
					for (int fill = n; fill >= 1; fill--) {
						if (k == 0 || dp[k - 1][fill] == Integer.MAX_VALUE) {
							minr[fill] = minr[fill + 1];
						} else {
							minr[fill] = Math.min(minr[fill + 1], cost[i][fill - 1] + dp[k - 1][fill]);
						}
					}
					for (int c = 0, ans; c <= n; c++) {
						if (c == 0 || memo[c] == Integer.MAX_VALUE) {
							ans = Integer.MAX_VALUE;
						} else {
							ans = cost[i][c - 1] + memo[c];
						}
						if (c > 0) {
							ans = Math.min(ans, minl[c - 1]);
						}
						if (c < n) {
							ans = Math.min(ans, minr[c + 1]);
						}
						dp[k][c] = ans;
					}
				}
			}
		}
		return dp[target][0] != Integer.MAX_VALUE ? dp[target][0] : -1;
	}

}