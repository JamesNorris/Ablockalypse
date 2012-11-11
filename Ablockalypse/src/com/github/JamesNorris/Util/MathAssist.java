package com.github.JamesNorris.Util;

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
	 * Gets the distance between 2 sets of coords.
	 * 
	 * @param x The first x coord
	 * @param y The first y coord
	 * @param z The first z coord
	 * @param x2 The second x coord
	 * @param y2 The second y coord
	 * @param z2 The second z coord
	 * @return The distance between the 2 sets of coords
	 */
	public static double distance(int x, int y, int z, int x2, int y2, int z2) {
		int X = x2 - x, Y = y2 - y, Z = z2 - z;
		return Math.sqrt((X * X) + (Y * Y) + (Z * Z));
	}

	/**
	 * The method for creating a line with a supplied x, m, and b.
	 * The general equation is y = mx + b.
	 * 
	 * @param m The m value for the line (slope)
	 * @param x The x value for the line (x-intercept)
	 * @param b The b value for the line (y-intercept)
	 * @return The current value for the y variable
	 */
	public static double line(double m, double x, double b) {
		return (m * x) + b;
	}
}
