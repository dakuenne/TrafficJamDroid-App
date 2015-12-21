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
package org.traffic.jamdroid.views;

import java.util.List;

import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;
import org.traffic.jamdroid.R;
import org.traffic.jamdroid.model.LocalData;
import org.traffic.jamdroid.model.LocalViewContainer;
import org.traffic.jamdroid.model.RemoteData;
import org.traffic.jamdroid.utils.IConstants;
import org.traffic.jamdroid.views.overlays.DrawableOverlayItem;
import org.traffic.jamdroid.views.overlays.RoadOverlay;
import org.traffic.jamdroid.views.overlays.SpeedOverlayItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * This view shows the user all existing information about the current road he
 * is driving on. For example: speed limit, average speed and the quality of the
 * data. If a congestion is located near the current position this is indicated
 * with a traffic sign.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 227 $
 */
public class LimitationsView extends View {
	
	private static SpeedOverlayItem last = null;

	/** An icon showing the current speed limit */
	private final Bitmap limit = BitmapFactory.decodeResource(getResources(),
			R.drawable.limit);

	/** An icon showing the average speed driven on this road */
	private final Bitmap recLimit = BitmapFactory.decodeResource(
			getResources(), R.drawable.rec_limit);

	/** An icon showing the quality of the data - data is up to date */
	private final Bitmap clock = BitmapFactory.decodeResource(getResources(),
			R.drawable.clock);

	/**
	 * An icon showing the quality of the data - data is interpolated over the
	 * same days
	 */
	private final Bitmap calendarDay = BitmapFactory.decodeResource(
			getResources(), R.drawable.calendar_day);

	/**
	 * An icon showing the quality of the data - data is interpolated over all
	 * logs
	 */
	private final Bitmap calendarAll = BitmapFactory.decodeResource(
			getResources(), R.drawable.calendar_all);

	/**
	 * An icon showing the existence of congestions
	 */
	private final Bitmap warning = BitmapFactory.decodeResource(getResources(),
			R.drawable.warning);

	/**
	 * Custom-Constructor to use when creating a view from code.
	 * 
	 * @param ctx
	 *            The {@link Context} the view is running in
	 */
	public LimitationsView(Context ctx) {
		super(ctx);
	}

	/**
	 * Custom-Constructor that is called when inflating a view from XML.
	 * 
	 * @param c
	 *            The {@link Context} the view is running in
	 * @param a
	 *            The attributes of the XML tag that is inflating the view
	 */
	public LimitationsView(Context c, AttributeSet a) {
		super(c, a);
	}

	/**
	 * Custom-Constructor that is called when inflating a view from XML with a
	 * class-specific base style.
	 * 
	 * @param c
	 *            The {@link Context} the view is running in
	 * @param a
	 *            The attributes of the XML tag that is inflating the view
	 * @param i
	 *            The default style to apply to this view
	 */
	public LimitationsView(Context c, AttributeSet a, int i) {
		super(c, a, i);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		final Paint p = new Paint();
		p.setTextSize(32.0f);
		p.setTypeface(Typeface.DEFAULT_BOLD);

		// getting the data for the current part of the road by searching for
		// the matching overlayitem
		final RemoteData remote = RemoteData.getInstance();
		List<Overlay> overlays = remote.getOverlays();
		SpeedOverlayItem actual = null;
		MapView mapView = LocalViewContainer.getInstance().getMapView();
		Projection pj = mapView.getProjection();
		for (Overlay o : overlays) {
			if (o instanceof RoadOverlay) {
				RoadOverlay<DrawableOverlayItem> poc = (RoadOverlay<DrawableOverlayItem>) o;
				for (int i = 0; i < poc.getSize(); i++) {
					if (poc.getItem(i) instanceof SpeedOverlayItem) {
						SpeedOverlayItem soi = (SpeedOverlayItem) poc
								.getItem(i);
						Point tmp = new Point();
						pj.toPixels(LocalData.getInstance().getGeoPoint(), tmp);
						if (poc.hitTest(soi, tmp.x, tmp.y, mapView)) {
							actual = soi;
							break;
						}
					}
				}

			}
		}

		if (actual == null && last == null)
			return;
		
		if ((actual == null || actual.getSpeed() <= 0) && last != null)
			actual = last;
		last = actual;

		// printing the allowed speed
		float dist = (this.getWidth() - limit.getWidth()) / 2.0f;
		canvas.drawBitmap(limit, dist, dist, p);
		final String maxSpeed = (actual.getMaxSpeed() != 0) ? (int) actual
				.getMaxSpeed() + "" : "?";
		canvas.drawText(maxSpeed,
				dist + (limit.getWidth() - p.measureText(maxSpeed)) / 2, dist
						+ limit.getHeight() - 26, p);

		// printing the recommended speed
		dist = (this.getWidth() - recLimit.getWidth()) / 2.0f;
		canvas.drawBitmap(recLimit, dist, limit.getHeight() + dist * 2, p);
		final String recSpeed;
		if (actual.getSpeed() > 0) {
			if (actual.getMaxSpeed() != 0) {
				recSpeed = ((int) Math.min(actual.getSpeed(),
						actual.getMaxSpeed()))
						+ "";
			} else {
				recSpeed = ((int) actual.getSpeed()) + "";
			}
		} else {
			recSpeed = "?";
		}
		p.setColor(Color.WHITE);
		canvas.drawText(recSpeed,
				dist + (recLimit.getWidth() - p.measureText(recSpeed)) / 2, 2
						* dist + recLimit.getHeight() + limit.getHeight() - 26,
				p);

		// printing the quality
		int height = 0;
		switch (actual.getQuality()) {
		case IConstants.QUALITY_UP_TO_DATE:
			dist = (this.getWidth() - clock.getWidth()) / 2.0f;
			height = clock.getHeight();
			canvas.drawBitmap(clock, dist,
					limit.getHeight() + recLimit.getHeight() + 3 * dist, p);
			break;
		case IConstants.QUALITY_DAY:
			dist = (this.getWidth() - calendarDay.getWidth()) / 2.0f;
			height = calendarDay.getHeight();
			canvas.drawBitmap(calendarDay, dist,
					limit.getHeight() + recLimit.getHeight() + 3 * dist, p);
			break;
		case IConstants.QUALITY_ALL:
			dist = (this.getWidth() - calendarAll.getWidth()) / 2.0f;
			height = calendarAll.getHeight();
			canvas.drawBitmap(calendarAll, dist,
					limit.getHeight() + recLimit.getHeight() + 3 * dist, p);
			break;
		default:
			dist = (this.getWidth() - calendarAll.getWidth()) / 2.0f;
			height = calendarAll.getHeight();
			canvas.drawBitmap(calendarAll, dist,
					limit.getHeight() + recLimit.getHeight() + 3 * dist, p);
			break;
		}

		if (remote.hasCongestions()) {
			dist = (this.getWidth() - warning.getWidth()) / 2.0f;
			canvas.drawBitmap(warning, dist,
					limit.getHeight() + recLimit.getHeight() + height + 4
							* dist, p);
		}
	}
}