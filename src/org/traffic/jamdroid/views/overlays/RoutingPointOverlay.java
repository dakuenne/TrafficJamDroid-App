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

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * This overlay indicates a single position on a <code>MapView</code>. This
 * position is identified by a latitude/longitude pair and displayed as a filled
 * circle.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 227 $
 */
public class RoutingPointOverlay extends Overlay {

	/** The position of the overlay in latitude and longitude */
	private final GeoPoint geopoint;
	/** The radius of the circle */
	private final int radius;
	/** The inner color of the overlay */
	private final int color;

	/**
	 * Custom-Constructor to create a <code>RoutingPointOverlay</code> at the
	 * assigned position. The default radius of 10px and the default color
	 * <i>blue</i> are used for this.
	 * 
	 * @param ctx
	 *            The context of the complete application
	 * @param point
	 *            The geographic position of this overlay
	 */
	public RoutingPointOverlay(final Context ctx, final GeoPoint point) {
		this(ctx, point, 20, Color.BLUE);
	}

	/**
	 * Custom-Constructor to create a <code>RoutingPointOverlay</code> at the
	 * assigned position with the assigned color and the assigned radius.
	 * 
	 * @param ctx
	 *            The context of the complete application
	 * @param point
	 *            The geographic position of this overlay
	 * @param radius
	 *            The radius of the complete circle
	 * @param color
	 *            The color to fill the circle
	 */
	public RoutingPointOverlay(final Context ctx, final GeoPoint point,
			final int radius, final int color) {
		super(ctx);
		this.geopoint = point;
		this.radius = radius;
		this.color = color;
	}

	@Override
	protected void draw(Canvas c, MapView osmv, boolean shadow) {
		// transform geo-position to point on canvas
		Projection projection = osmv.getProjection();
		Point point = new Point();
		projection.toMapPixels(geopoint, point);

		// the circle to mark the spot
		Paint circlePaint = new Paint();
		circlePaint.setAntiAlias(true);
		circlePaint.setColor(color);
		circlePaint.setAlpha(90);
		circlePaint.setStyle(Paint.Style.FILL);
		c.drawCircle(point.x, point.y, radius, circlePaint);

		// border region
		circlePaint.setColor(Color.WHITE);
		circlePaint.setAlpha(255);
		circlePaint.setStyle(Paint.Style.STROKE);
		circlePaint.setStrokeWidth(3);
		c.drawCircle(point.x, point.y, radius, circlePaint);
	}

}