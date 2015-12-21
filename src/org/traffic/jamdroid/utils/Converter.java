/*
 * Copyright (c) 2011, Daniel Kuenne
 * 
 * This file is part of TrafficJamDroid.
 *
 * TrafficJamDroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TrafficJamDroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TrafficJamDroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.traffic.jamdroid.utils;

import android.graphics.Color;

/**
 * This class provides a variety of methods to convert data
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 227 $
 */
public class Converter {

	/** conversion factor for degrees and microdegrees */
	public static final double MICRODEGREES_COEFF = 1E6;

	/**
	 * Calculates the RGB-color by converting a hue to a HSV-color
	 * 
	 * @param h
	 *            The hue of the HSV-color
	 * @return RGB-color
	 */
	public static int formatHueToRGB(float h) {
		float[] hsv = { h, 1.0f, 1.0f };
		return Color.HSVToColor(hsv);
	}

	/**
	 * Formats a given microdegree-value into a degree-value
	 * 
	 * @param micro
	 *            The microdegrees
	 * @return The degrees
	 */
	public static double formatMicrodegreesToDegrees(int micro) {
		return (double) micro / MICRODEGREES_COEFF;
	}

	/**
	 * Formats a given degree-value into a microdegree-value
	 * 
	 * @param degrees
	 *            The degrees
	 * @return The microdegrees
	 */
	public static int formatDegreesToMicrodegrees(double degrees) {
		return (int) (degrees * MICRODEGREES_COEFF);
	}

}