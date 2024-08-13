package io.seevae.array.双指针解决数组问题;

public class 最长回文子串 {

    public static void main(String[] args) {
        String s = "ababbcdeffedpq";
        String re = longestPalindrome(s);
        System.out.println(re);
    }

    //ababbcdeffed -> deffed
    public static String longestPalindrome(String s) {
        String longest = "";
        for (int i = 0; i < s.length(); i++) {
            String str1 = getPalindromeFromCertainPoint(s, i, i); //回文串以中间一个字符为对称
            String str2 = getPalindromeFromCertainPoint(s, i, i + 1); //回文串以中间两个字符为对称
            String longerStr = str1.length() > str2.length() ? str1 : str2;
            if (longerStr.length() > longest.length()) {
                longest = longerStr;
            }
        }
        return longest;
    }

    /**
     * subString是左闭右开区间，所以不包含结束索引位置的元素
     *
     * @param s
     * @param l
     * @param r
     * @return
     */
    public static String getPalindromeFromCertainPoint(String s, int l, int r) {
        while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) {
            l--;
            r++;
        }
        return s.substring(l + 1, r);
    }

}
