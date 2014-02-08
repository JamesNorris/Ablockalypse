package com.github.jamesnorris.ablockalypse.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MathUtility {
    public static Integer[] parseIntervalNotation(String line) {
        List<Integer> listLevels = new ArrayList<Integer>();
        int includingStart = line.indexOf("[");
        int notIncludingStart = line.indexOf("(");
        int includingEnd = line.indexOf("]");
        int notIncludingEnd = line.indexOf(")");
        boolean startIncludes = includingStart != -1;
        boolean endIncludes = includingEnd != -1;
        String[] integers = line.substring((startIncludes ? includingStart : notIncludingStart) + 1, endIncludes ? includingEnd : notIncludingEnd).split(Pattern.quote(","));
        int start = Integer.parseInt(integers[0].trim());
        int end = Integer.parseInt(integers[1].trim());
        for (int i = start; i <= end; i++) {
            listLevels.add(i);
        }
        return listLevels.toArray(new Integer[listLevels.size()]);
    }

    public static int parsePercentage(String line) throws NumberFormatException {
        if (line.isEmpty()) {
            throw new IllegalArgumentException("Line cannot be null!");
        }
        int signIndex = line.indexOf("%");
        String totalDigits = "";
        for (int i = 3; i >= 0; i--) {
            char beforeSign = line.charAt(signIndex - i);
            if (beforeSign == ' ' || !Character.isDefined(beforeSign) || !Character.isDigit(beforeSign)) {
                break;
            }
            totalDigits = totalDigits.concat(Character.toString(beforeSign));
        }
        return Integer.parseInt(totalDigits);
    }

    public static double absDegrees(double degrees) {
        if (degrees < 0) {
            for (int i = (int) Math.round(degrees / -360); i >= 0; i--) {
                degrees += 360;;
            }
        }
        return degrees % 360;
    }

    public static double refAngle(double degrees) {
        if (degrees < 0 || degrees > 360) {
            degrees = absDegrees(degrees);
        }
        if (degrees < 90) {
            return degrees;
        } else if (degrees < 180) {
            return 180 - degrees;
        } else if (degrees < 270) {
            return degrees - 180;
        } else {
            return 360 - degrees;
        }
    }
}
