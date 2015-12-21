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

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.traffic.jamdroid.R;
import org.traffic.jamdroid.model.LocalData;
import org.traffic.jamdroid.model.LocalViewContainer;
import org.traffic.jamdroid.model.Preferences;
import org.traffic.jamdroid.model.RemoteData;
import org.traffic.jamdroid.model.RoutePoint;
import org.traffic.jamdroid.utils.IConstants;
import org.traffic.jamdroid.utils.Request;
import org.traffic.jamdroid.utils.Requester;
import org.traffic.jamdroid.views.InfoView;
import org.traffic.jamdroid.views.LimitationsView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;

/**
 * This overlay indicates the position of the car/user on the map. If no
 * movement is detected a person is drawn. Otherwise an arrow indicates position
 * and direction of the user.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 227 $
 */
public class LocationOverlay extends MyLocationOverlay {

	/** The old timestamp - used to calculate the speed of the car */
	private long oldTime;

	/** The old location - used to calculate the speed of the car */
	private Location oldLocation = null;;

	/**
	 * Custom-Constructor to create a <code>LocationOverlay</code>.
	 * 
	 * @param ctx
	 *            The context of the complete application
	 * @param mapView
	 *            The map on which the overlay should be drawn
	 */
	public LocationOverlay(final Context ctx, final MapView mapView) {
		this(ctx, mapView, new DefaultResourceProxyImpl(ctx));
	}

	/**
	 * Custom-Constructor to create a <code>LocationOverlay</code>.
	 * 
	 * @param ctx
	 *            The context of the complete application
	 * @param mapView
	 *            The map on which the overlay should be drawn
	 * @param pResourceProxy
	 *            The proxy to handle the bitmaps
	 */
	public LocationOverlay(Context ctx, MapView mapView,
			ResourceProxy pResourceProxy) {
		super(ctx, mapView, pResourceProxy);
	}

	@Override
	public void onLocationChanged(final Location location) {
		super.onLocationChanged(location);

		long timed = 0;
		long time = location.getTime();
		final LocalData data = LocalData.getInstance();

		// calculating the speed
		if (oldLocation != null) {
			double dist = location.distanceTo(oldLocation);
			timed = Math.abs(time - oldTime);
			float s = (float) ((dist / 1000.0) / (timed / 3600000.0));
			location.setSpeed(s);
		}
		oldLocation = location;
		oldTime = time;

		// saving the data and redrawing the view
		if ((timed > 500 && location.getSpeed() < 300)
				|| (data.getLatitude() == 0.0 && data.getLongitude() == 0.0)) {
			List<RoutePoint> route = LocalData.getInstance().getRoute();
			if (route.size() > 0) {
				GeoPoint position = new GeoPoint(location);
				if (route.get(route.size() - 1).getPosition()
						.distanceTo(position) < 50) {
					final Context context = LocalViewContainer.getInstance()
							.getMapView().getContext();
					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setMessage(
							context.getResources().getString(
									R.string.popup_route_finished))
							.setCancelable(false)
							.setPositiveButton(
									context.getResources().getString(
											R.string.ok),
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.dismiss();
										}
									});
					builder.create().show();
					route.clear();
					Request r = new Request(IConstants.REQUEST_DELETE_ROUTE,
							Preferences.getInstance(
									context.getApplicationContext()).getString(
									"session", null));
					Requester.getInstance(context).contactServer(r.toJson());
					RemoteData.getInstance().deleteRouteOverlay();
					LocalViewContainer.getInstance().getMapView()
							.postInvalidate();
				}

			}

			LocalData.getInstance().setData(time, location.getLatitude(),
					location.getLongitude(), location.getSpeed());
			final InfoView previewView = LocalViewContainer.getInstance()
					.getInfoView();
			if (previewView != null) {
				previewView.new GeocodingTask().execute();
			}
			final LimitationsView limitView = LocalViewContainer.getInstance()
					.getLimitView();
			if (limitView != null) {
				limitView.postInvalidate();
			}
		}
	}
}
