
/*
 * this algo solves the problem for determining if a string is palindrome.
 *
 * reference : http://www.growingwiththeweb.com/2014/02/determine-if-a-string-is-a-palindrome.html
 * 
 * this is a simplification of the notorious LPS problem which opens several interesting discussions around Manacher's and other O(n) solutions.
 *
 * OBS. the following implementation takes care initially of spaces,ponctuation and lowercases the string.
 *
 * desavera@gmail.com - 7/2016
 */

import java.util.*;

public class Solution1 {

  public static boolean isTextPalindrome(String text) {
    if (text == null) {
        return false;
    }
    int left = 0;
    int right = text.length() - 1;
    while (left < right) {
        if (text.charAt(left++) != text.charAt(right--)) {
            return false;
        }
    }
    return true;
  }

  public static boolean isPhrasePalindrome(String text) {
    String chars = text.replaceAll("[^a-zA-Z]", "").toLowerCase();
    return isTextPalindrome(chars);
  }

  public static void main (String[] args) {

    Scanner scan = new Scanner(System.in);
    while (scan.hasNextLine()) {

      String text = scan.nextLine();
      System.out.println(isPhrasePalindrome(text));

    }

  }

}




