package com.verr1.valkyrienmanager.util;

import java.util.Collection;

public class StringUtil {

    /**
     Calculates the similarity between a value and a collection of strings (toMatch) as a percentage.
     The similarity is determined based on the longest common subsequence (LCS) between the value and each string in the collection.

     @param value The string to compare.
     @param toMatch The collection of strings to match against.
     @return The similarity percentage (0.0 to 100.0) based on the best match. */
    public static double calculateSimilarity(String value, Collection<String> toMatch) {
        if (value == null || toMatch == null || toMatch.isEmpty()) {
            return 0.0;
        }
        return toMatch.stream().mapToDouble(
                        match -> {
                            int lcsLength = longestCommonSubsequence(value, match);
                            int maxLength = Math.max(value.length(), match.length());
                            return maxLength == 0 ? 0.0 : (double) lcsLength / maxLength * 100.0;
                        })
                .max().orElse(0.0);
    }


    public static double calculateSimilarity(String value, String toMatch) {
        if (value == null || toMatch == null || toMatch.isEmpty()) {
            return 0.0;
        }
        int lcsLength = longestCommonSubsequence(value, toMatch);
        int maxLength = Math.max(value.length(), toMatch.length());
        return (double) lcsLength / maxLength * 100.0;
    }



    /**
     Calculates the length of the longest common subsequence (LCS) between two strings.

     @param str1 The first string.
     @param str2 The second string.
     @return The length of the LCS. */
    private static int longestCommonSubsequence(String str1, String str2) {
        int m = str1.length();
        int n = str2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1; }
                else { dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]); } } }
        return dp[m][n];
    }
}
