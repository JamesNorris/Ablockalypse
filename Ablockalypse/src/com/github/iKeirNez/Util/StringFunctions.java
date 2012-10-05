package com.github.iKeirNez.Util;

public class StringFunctions {
	/**
	 * Separates a collection into a comma separated list
	 * 
	 * @param inputArray An object to implode
	 * @param glueString The string to be separated
	 * @return The finalized string, separated
	 */
	public static String implode(final Object[] inputArray, final String glueString, final String finalGlueString) {
		String output = "";
		if (inputArray.length > 0) {
			final StringBuilder sb = new StringBuilder();
			sb.append(inputArray[0]);
			for (int i = 1; i < inputArray.length; i++) {
				if (i != inputArray.length - 1)
					sb.append(glueString);
				else
					sb.append(finalGlueString);
				sb.append(inputArray[i]);
			}
			output = sb.toString();
		}
		return output;
	}
}
