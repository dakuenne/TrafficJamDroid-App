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

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.OverlayItem;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * This class provides a functionality to draw path-elements on a
 * {@link MapView}.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 163 $
 */
public class DrawableOverlayItem extends OverlayItem {

	/** A list of all points used to draw this item */
	private ArrayList<Point> points;
	
	/** A list of all points used to draw this item */
	private List<GeoPoint> geopoints;

	/** A counter for the precomputed points */
	private int pointsPrecomputed;

	/** The object to paint the path */
	private Paint paint = new Paint();

	/** The path containing all lines between the points */
	private final Path path = new Path();

	/** A temporarily used point */
	private final Point tmpPoint1 = new Point();

	/** A temporarily used point */
	private final Point tmpPoint2 = new Point();

	/** A bounding rectangle for the current line segment */
	private final Rect lineBounds = new Rect();

	/**
	 * Custom-Constructor with a color.
	 * 
	 * @param color
	 *            The color of the overlay
	 */
	public DrawableOverlayItem(final int color, final List<GeoPoint> points) {
		super("", "", new GeoPoint(0, 0));
		this.paint.setColor(color);
		this.paint.setStrokeWidth(2.0f);
		this.paint.setStyle(Paint.Style.STROKE);
		this.geopoints = points;
		
		this.clearPath();
	}
	
	/**
	 * Returns a list of all {@link GeoPoint}s.
	 * 
	 * @return The list
	 */
	public List<GeoPoint> getGeoPoints() {
		return geopoints;
	}

	/**
	 * Returns the points.
	 * 
	 * @return A list with all points
	 */
	public List<Point> getPoints() {
		return points;
	}

	/**
	 * Sets the color of the overlay.
	 * 
	 * @param color
	 *            The new color
	 */
	public void setColor(final int color) {
		this.paint.setColor(color);
	}

	/**
	 * Sets an alpha-value for the overlay.
	 * 
	 * @param a
	 *            The new alpha-value
	 */
	public void setAlpha(final int a) {
		this.paint.setAlpha(a);
	}

	/**
	 * Returns the {@link Paint}-element.
	 * 
	 * @return The element
	 */
	public Paint getPaint() {
		return paint;
	}

	/**
	 * Sets a new {@link Paint}-element.
	 * 
	 * @param pPaint
	 *            The new element
	 */
	public void setPaint(Paint pPaint) {
		if (pPaint == null)
			throw new IllegalArgumentException("pPaint argument cannot be null");
		paint = pPaint;
	}

	/**
	 * Deletes all points
	 */
	public void clearPath() {
		this.points = new ArrayList<Point>();
		this.pointsPrecomputed = 0;
	}

	/**
	 * Adds a {@link GeoPoint} to the line.
	 * 
	 * @param pt
	 *            The new point
	 */
	public void addPoint(final GeoPoint pt) {
		this.addPoint(pt.getLatitudeE6(), pt.getLongitudeE6());
	}

	/**
	 * Adds a new point identified by latitude and longitude to the line.
	 * 
	 * @param latitudeE6
	 *            The latitude
	 * @param longitudeE6
	 *            The longitude
	 */
	public void addPoint(final int latitudeE6, final int longitudeE6) {
		this.points.add(new Point(latitudeE6, longitudeE6));
	}

	/**
	 * Returns the number of points.
	 * 
	 * @return The number of points
	 */
	public int getNumberOfPoints() {
		return this.points.size();
	}

	/**
	 * Draws an instance of this class on the given canvas. All points are
	 * connected by lines.
	 * 
	 * @param canvas
	 *            The {@link Canvas} to draw on
	 * @param shadow
	 *            Is the element in a shadow
	 */
	protected void draw(final Canvas canvas, final MapView mapView,
			final boolean shadow) {
		if (shadow) {
			return;
		}

		if (this.points.size() < 2) {
			// nothing to paint
			return;
		}

		final Projection pj = mapView.getProjection();

		// precompute new points to the intermediate projection.
		final int size = this.points.size();

		while (this.pointsPrecomputed < size) {
			final Point pt = this.points.get(this.pointsPrecomputed);
			pj.toProjectedPixels(pt.x, pt.y, pt);

			this.pointsPrecomputed++;
		}

		Point screenPoint0 = null; // points on screen
		Point screenPoint1 = null;
		Point projectedPoint0; // points from the points list
		Point projectedPoint1;

		// clipping rectangle in the intermediate projection, to avoid
		// performing projection.
		BoundingBoxE6 boundingBox = pj.getBoundingBox();
		Point topLeft = pj.toProjectedPixels(boundingBox.getLatNorthE6(),
				boundingBox.getLonWestE6(), null);
		Point bottomRight = pj.toProjectedPixels(boundingBox.getLatSouthE6(),
				boundingBox.getLonEastE6(), null);
		final Rect clipBounds = new Rect(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y);

		path.rewind();
		projectedPoint0 = this.points.get(size - 1);
		lineBounds.set(projectedPoint0.x, projectedPoint0.y, projectedPoint0.x,
				projectedPoint0.y);

		for (int i = size - 2; i >= 0; i--) {
			// compute next points
			projectedPoint1 = this.points.get(i);
			lineBounds.union(projectedPoint1.x, projectedPoint1.y);

			if (!Rect.intersects(clipBounds, lineBounds)) {
				// skip this line, move to next point
				projectedPoint0 = projectedPoint1;
				screenPoint0 = null;
				continue;
			}

			// the starting point may be not calculated, because previous
			// segment was out of clip
			// bounds
			if (screenPoint0 == null) {
				screenPoint0 = pj.toPixelsFromProjected(projectedPoint0,
						this.tmpPoint1);
				path.moveTo(screenPoint0.x, screenPoint0.y);
			}

			screenPoint1 = pj.toPixelsFromProjected(projectedPoint1,
					this.tmpPoint2);

			// skip this point, too close to previous point
			if (Math.abs(screenPoint1.x - screenPoint0.x)
					+ Math.abs(screenPoint1.y - screenPoint0.y) <= 1) {
				continue;
			}

			path.lineTo(screenPoint1.x, screenPoint1.y);

			// update starting point to next position
			projectedPoint0 = projectedPoint1;
			screenPoint0.x = screenPoint1.x;
			screenPoint0.y = screenPoint1.y;
			lineBounds.set(projectedPoint0.x, projectedPoint0.y,
					projectedPoint0.x, projectedPoint0.y);
		}

		canvas.drawPath(path, this.paint);

	}
}
