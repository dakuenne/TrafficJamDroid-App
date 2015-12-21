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
package org.traffic.jamdroid.views.overlays;

import java.util.List;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.traffic.jamdroid.R;
import org.traffic.jamdroid.model.LocalViewContainer;
import org.traffic.jamdroid.utils.Converter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

/**
 * This overlay shows the current traffic of a street and indicates this with a
 * colored line. The lines color is between green (fast-flowing traffic) and red
 * (jam). This type of overlay is mainly created by the @link{UpdateGPSTask} and
 * stored in a {@link RoadOverlay} to provide <code>onTap</code>
 * -functionality.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 227 $
 * @see RoadOverlay
 */
public class SpeedOverlayItem extends DrawableOverlayItem {

	/** Stores the average speed for this strip. */
	private double speed;

	/** Stores the allowed speed for this strip */
	private double maxSpeed;

	/** Stores the quality of the informations */
	private int quality;	
	
	public SpeedOverlayItem(final Context ctx, final List<GeoPoint> points, final int color) {
		super(color, points);
		this.getPaint().setStrokeWidth(10.0f);
		for (GeoPoint p : points) {
			addPoint(p);
		}
	}

	/**
	 * Custom-Constructor to create a <code>SpeedOverlay</code>. This overlay
	 * indicates the traffic on a specified part of a road with a colored line.
	 * A green line indicates fast-flowing traffic while a red displays jams or
	 * slow traffic.
	 * 
	 * @param ctx
	 *            The {@link Context} the view is running in
	 * @param points
	 *            A list with all turning-points
	 * @param speed
	 *            The current speed
	 * @param maxSpeed
	 *            The speed limit
	 * @param quality
	 *            The quality of the data
	 */
	public SpeedOverlayItem(final Context ctx, final List<GeoPoint> points,
			final double speed, final double maxSpeed, final double quality) {
		super(calculateColor(speed, maxSpeed), points);
		this.getPaint().setStrokeWidth(10.0f);
		this.maxSpeed = maxSpeed;
		this.speed = speed;
		this.quality = (int) quality;
		for (GeoPoint p : points) {
			addPoint(p);
		}
	}

	/**
	 * Returns the current speed.
	 * 
	 * @return The current average speed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Returns the speed limit.
	 * 
	 * @return The maximal allowed speed
	 */
	public double getMaxSpeed() {
		return maxSpeed;
	}

	/**
	 * Returns the quality of the data.
	 * 
	 * @return The quality of the data
	 */
	public int getQuality() {
		return quality;
	}

	@Override
	protected void draw(final Canvas canvas, final MapView mapView,
			final boolean shadow) {

		// change width if necessary
		if (mapView.getZoomLevel() <= 10)
			return;
		if (mapView.getZoomLevel() == 11)
			this.getPaint().setStrokeWidth(5.0f);
		if (mapView.getZoomLevel() == 12)
			this.getPaint().setStrokeWidth(8.0f);
		super.draw(canvas, mapView, shadow);
	}

	/**
	 * Generates the color of a <code>SpeedOverlayItem</code> depending on the
	 * actual speed and the allowed speed. Therefore the quotient of actual
	 * speed and allowed speed is transferred to the HSV color model which
	 * provides a color between green and red.
	 * 
	 * @param speed
	 *            The speed driven on the street
	 * @param maxSpeed
	 *            The maximum speed
	 * @return A color between red and green
	 */
	private static int calculateColor(final double speed, final double maxSpeed) {
		if (speed < 0 || maxSpeed < 0) {
			return Color.WHITE;
		}
		return Converter.formatHueToRGB((float) Math.max(
				Math.min(speed / maxSpeed * 120, 120), 0));
	}

	@Override
	public String toString() {
		Context context = LocalViewContainer.getInstance().getMapView()
				.getContext().getApplicationContext();

		final String strMaxSpeed = (maxSpeed != 0) ? (int) maxSpeed + " km/h"
				: context.getResources().getString(R.string.soi_unknown);
		String recSpeed = "";
		if (speed > 0) {
			if (maxSpeed != 0) {
				recSpeed = ((int) Math.min(speed, maxSpeed)) + " km/h";
			} else {
				recSpeed = (speed) + " km/h";
			}
		} else {
			recSpeed = context.getResources().getString(R.string.soi_unknown);
		}

		StringBuffer buff = new StringBuffer();
		buff.append(context.getResources().getString(R.string.soi_speed));
		buff.append(": " + recSpeed + ", ");
		buff.append(context.getResources().getString(R.string.soi_tempolimit));
		buff.append(": " + strMaxSpeed + ", ");
		buff.append(context.getResources().getString(R.string.soi_quality)
				+ ": ");
		switch (quality) {
		case 0:
			buff.append(context.getResources()
					.getString(R.string.soi_quality_0));
			break;
		case 1:
			buff.append(context.getResources()
					.getString(R.string.soi_quality_1));
			break;
		default:
			buff.append(context.getResources()
					.getString(R.string.soi_quality_2));
		}

		return buff.toString();
	}
}