package org.report.xml.support;

import org.apache.commons.lang3.StringUtils;

public class XMLCleaner {

	public static String removeNonAlphaNummerics(String replace) {
		return replace.replaceAll("[^a-zA-Z0-9]", "");
	}

	public static String addAlphaNummericsEnd(String replace, String action) {
		return replace + action;
	}

	public static String addAlphaNummericsStart(String replace, String action) {
		return action + replace;
	}

	public static String removeBetweenIndexes(String replace, int start, int end) {
		StringBuffer strBuffer = new StringBuffer(replace);
		
		
		if (checkIndex(replace, start) && checkIndex(replace, end)) {
			return strBuffer.delete(start, end).toString();
		} else
			return null;
		
	}

	public static String insertBetweenIndexes(String replace, int start, String substring) {
		StringBuffer strBuffer = new StringBuffer(replace);

		if (checkIndex(replace, start)) {
			return strBuffer.insert(start, substring).toString();
		} else
			return null;
	}

	public static String removeCharacter(String replace, String index) {
		Integer indexInt = new Integer(index);

		if (checkIndex(replace, indexInt)) {
			StringBuilder sb = new StringBuilder(replace);
			String result = sb.deleteCharAt(indexInt).toString();
			return result;
		} else
			return null;
			
	

	}

	private static boolean checkIndex(String replace, int index) {
		if ((replace != null) && (replace.length() > index) && (index >= 0))
			return true;
		else {
			System.out.println("Invalid Index " + index);
			System.exit(0);
		}
		return false;

	}

	public static String replaceString(String orginal, String newString) {

		return StringUtils.replaceOnce(orginal, orginal, newString);
	}
}
