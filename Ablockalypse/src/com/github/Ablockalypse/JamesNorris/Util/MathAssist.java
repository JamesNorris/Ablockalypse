package com.github.Ablockalypse.JamesNorris.Util;

public class MathAssist {
	/**
	 * The method for curve-fitting 4 points with a supplied x, a, b, c, and d.
	 * The general equation for a 4 point curve-fit is y = ax^3 + bx^2 + cx + d.
	 * 
	 * @param x The x value for the curve-fit (x-intercept)
	 * @param a The a value for the curve-fit (slope 1)
	 * @param b The b value for the curve-fit (slope 2)
	 * @param c The c value for the curve-fit (slope 3)
	 * @param d The d value for the curve-fit (y-intercept)
	 * @return The current value for the y variable
	 */
	public static double curve(double x, double a, double b, double c, double d) {
		double y1 = a * x, y2 = b * x, y3 = c * x;
		return d + y3 + (y2 * y2) - (y1 * y1 * y1);
	}

	/**
	 * The method for creating a line with a supplied x, m, and b.
	 * The general equation is y = mx + b.
	 * 
	 * @param x The x value for the line (x-intercept)
	 * @param m The m value for the line (slope)
	 * @param b The b value for the line (y-intercept)
	 * @return The current value for the y variable
	 */
	public static double line(double x, double m, double b) {
		return (m * x) + b;
	}
}
