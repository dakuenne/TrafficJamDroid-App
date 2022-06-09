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

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.traffic.jamdroid.model.SearchData;
import org.traffic.jamdroid.views.overlays.RoutingPointOverlay;

/**
 * A sub-class of {@link MapView} to provide some additional functions. For
 * example the one-click-location.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 227 $
 */
public class SearchView extends MapView {

	/** The selected location */
	private Location location;
	/** The status of the "click" */
	private boolean isClick = false;
	/** The context of the application */
	private Context ctx;

	/**
	 * Constructor used by XML layout resource (uses default tile source).
	 * 
	 * @param context
	 *            The context of the application
	 * @param attrs
	 *            The attributes
	 */
	public SearchView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		prepareView(context);
	}

	/**
	 * Constructor with a {@link MapTileProviderBase}.
	 * 
	 * @param context
	 *            The context of the application
	 * @param tileSizePixels
	 *            The size of the tiles
	 * @param aTileProvider
	 *            The provider of the tiles
	 */
	public SearchView(final Context context, final int tileSizePixels,
			final MapTileProviderBase aTileProvider) {
		this(context, tileSizePixels, aTileProvider, null);
	}

	/**
	 * Constructor with a {@link Handler}.
	 * 
	 * @param context
	 *            The context of the application
	 * @param tileSizePixels
	 *            The size of the tiles
	 * @param aTileProvider
	 *            The provider of the tiles
	 * @param tileRequestCompleteHandler
	 *            A handler for the tiles
	 */
	public SearchView(final Context context, final int tileSizePixels,
			final MapTileProviderBase aTileProvider,
			final Handler tileRequestCompleteHandler) {
		super(context, aTileProvider,
				tileRequestCompleteHandler);
		prepareView(context);
	}

	/**
	 * Prepares the complete view and the listeners.
	 * 
	 * @param context
	 *            The context of the application
	 */
	private void prepareView(Context context) {
		this.ctx = context;
		setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isClick) {
					SearchData.getInstance().setPosition(location);
					addOverlay(new GeoPoint(location));
					isClick = false;
				}
			}
		});
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		Projection proj = getProjection();
		IGeoPoint loc = proj.fromPixels((int) event.getX(), (int) event.getY());
		Location location = new Location("SEARCH_POS");
		location.setLatitude(loc.getLatitudeE6() / 1000000.0);
		location.setLongitude(loc.getLongitudeE6() / 1000000.0);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			this.location = location;
			break;
		case MotionEvent.ACTION_UP:
			if (this.location.distanceTo(location) < 15.0) {
				isClick = true;
			}
			break;
		}

		return super.dispatchTouchEvent(event);
	}

	/**
	 * Adds an overlay at the assigned position.
	 * 
	 * @param point
	 *            The position
	 */
	private void addOverlay(final GeoPoint point) {
		RoutingPointOverlay rpo = new RoutingPointOverlay(ctx, point);
		this.getOverlays().clear();
		this.getOverlays().add(rpo);
		this.getController().setCenter(point);
		this.invalidate();
	}
}
