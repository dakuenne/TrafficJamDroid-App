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

import java.util.LinkedList;
import java.util.List;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.traffic.jamdroid.model.LocalViewContainer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

/**
 * This overlay represents a line indicating the best way for a driver to get
 * from one position to another. It is only visible if the user has calculated a
 * route.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 227 $
 */
public class RouteOverlayItem extends DrawableOverlayItem {

	private String caption;

	/**
	 * Custom-Constructor to create a <code>RouteOverlay</code>.
	 * 
	 * @param points
	 *            A list with all points describing the route
	 */
	public RouteOverlayItem(final List<GeoPoint> points) {
		super(Color.BLACK, points);
		new SetCaptionTask().execute(points.get(0),
				points.get(points.size() - 1));
		this.getPaint().setStrokeWidth(16.0f);
		for (GeoPoint p : points) {
			addPoint(p);
		}
	}

	@Override
	protected void draw(final Canvas canvas, final MapView mapView,
			final boolean shadow) {

		// setting up the size of the overlay
		if (mapView.getZoomLevel() <= 10)
			this.getPaint().setStrokeWidth(1.0f);
		else if (mapView.getZoomLevel() == 11)
			this.getPaint().setStrokeWidth(11.0f);
		else if (mapView.getZoomLevel() == 12)
			this.getPaint().setStrokeWidth(14.0f);

		// the line with with alpha-setting and anti-alias
		this.getPaint().setAlpha(95);
		this.getPaint().setAntiAlias(true);
		super.draw(canvas, mapView, shadow);

		List<GeoPoint> drawingPoints = new LinkedList<GeoPoint>();
		drawingPoints.add(getGeoPoints().get(0));
		drawingPoints.add(getGeoPoints().get(getGeoPoints().size() - 1));

		Projection projection = mapView.getProjection();
		for (GeoPoint geopoint : drawingPoints) {
			// transform geo-position to point on canvas
			Point point = new Point();
			projection.toPixels(geopoint, point);

			// the circle to mark the spot
			Paint circlePaint = new Paint();
			circlePaint.setAntiAlias(true);
			circlePaint.setColor(Color.BLUE);
			circlePaint.setAlpha(90);
			circlePaint.setStyle(Paint.Style.FILL);
			canvas.drawCircle(point.x, point.y, 20, circlePaint);

			// border region
			circlePaint.setColor(Color.WHITE);
			circlePaint.setAlpha(255);
			circlePaint.setStyle(Paint.Style.STROKE);
			circlePaint.setStrokeWidth(3);
			canvas.drawCircle(point.x, point.y, 20, circlePaint);
		}
	}

	@Override
	public String toString() {
		return caption;
	}

	/**
	 * Task to calculate the description of a rooute.
	 * 
	 * @author Daniel Kuenne
	 * @version $LastChangedRevision: 227 $
	 */
	private class SetCaptionTask extends AsyncTask<GeoPoint, Void, Void> {

		@Override
		protected Void doInBackground(GeoPoint... params) {
			StringBuffer caption = new StringBuffer();
			caption.append("Route von ");
			int run = 0;
			for (GeoPoint point : params) {
				Location loc = new Location("PREVIEW");
				loc.setLatitude((double) point.getLatitudeE6() / 1000000);
				loc.setLongitude((double) point.getLongitudeE6() / 1000000);
				final Geocoder gc = new Geocoder(LocalViewContainer
						.getInstance().getInfoView().getContext()
						.getApplicationContext());
				List<Address> l = new LinkedList<Address>();
				try {
					l = gc.getFromLocation(loc.getLatitude(),
							loc.getLongitude(), 1);
				} catch (Exception e) {
					Log.w("SetCaptionTask", e.getClass().getSimpleName()
							+ "@doInBackground: " + e.getMessage());
				}
				if (l.size() > 0 && l.get(0).getThoroughfare() != null) {
					caption.append(l.get(0).getThoroughfare());
					if (run == 0)
						caption.append(" nach ");
				}
				run++;

			}
			RouteOverlayItem.this.caption = caption.toString();
			return null;
		}
	}

}