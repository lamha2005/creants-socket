/**
 * 
 */
package com.avengers.netty.socket.util;

/**
 * @author LamHa
 *
 */
public class StringUtils {

	/**
	 * Tim 1 chuoi trong 1 chuoi char
	 * 
	 * @param str
	 * @param searchStr
	 * @return
	 */
	public static boolean containsIgnoreCase(CharSequence str, CharSequence searchStr) {
		if ((str == null) || (searchStr == null)) {
			return false;
		}

		int len = searchStr.length();
		int max = str.length() - len;
		for (int i = 0; i <= max; i++) {
			if (CharSequenceUtils.regionMatches(str, true, i, searchStr, 0, len)) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkNameValid(String name) {
		return name.matches("[a-zA-Z0-9]*");
	}

	/**
	 * <p>
	 * Kiểm tra nếu một CharSequence thì empty ("") hoặc null.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.isEmpty(null)      = true
	 * StringUtils.isEmpty("")        = true
	 * StringUtils.isEmpty(" ")       = false
	 * StringUtils.isEmpty("bob")     = false
	 * StringUtils.isEmpty("  bob  ") = false
	 * </pre>
	 */
	public static boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	public static boolean isNotEmpty(final CharSequence cs) {
		return !isEmpty(cs);
	}

	/**
	 * <p>
	 * Kiểm tra nếu một CharSequence thì không được empty (""), không null, và
	 * chỉ có khoảng trắng.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.isNotBlank(null)      = false
	 * StringUtils.isNotBlank("")        = false
	 * StringUtils.isNotBlank(" ")       = false
	 * StringUtils.isNotBlank("bob")     = true
	 * StringUtils.isNotBlank("  bob  ") = true
	 * </pre>
	 */
	public static boolean isBlank(final CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(final CharSequence cs) {
		return !isBlank(cs);
	}
}
