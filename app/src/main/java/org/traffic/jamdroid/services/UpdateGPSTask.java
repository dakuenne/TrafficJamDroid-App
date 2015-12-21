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
package org.traffic.jamdroid.services;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.traffic.jamdroid.R;
import org.traffic.jamdroid.db.DBWrapper;
import org.traffic.jamdroid.model.LocalData;
import org.traffic.jamdroid.model.LocalViewContainer;
import org.traffic.jamdroid.model.Preferences;
import org.traffic.jamdroid.model.RemoteData;
import org.traffic.jamdroid.utils.IConstants;
import org.traffic.jamdroid.utils.Request;
import org.traffic.jamdroid.utils.Requester;
import org.traffic.jamdroid.views.LimitationsView;
import org.traffic.jamdroid.views.overlays.CongestionItem;
import org.traffic.jamdroid.views.overlays.DrawableOverlayItem;
import org.traffic.jamdroid.views.overlays.RoadOverlay;
import org.traffic.jamdroid.views.overlays.RouteOverlayItem;
import org.traffic.jamdroid.views.overlays.SpeedOverlayItem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Task to refresh the data. It sends an update-request to the server and
 * receives the traffic-data, the congestions and the status of the navigation.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 226 $
 */
public class UpdateGPSTask extends AsyncTask<Context, Void, Void> {

	/** The debug-tag */
	private static final String TAG = "UpdateGPSTask";
	/** The context of this application */
	private Context context;

	@Override
	protected Void doInBackground(Context... args0) {
		context = args0[0];
		try {
			LocalData local = LocalData.getInstance();
			RemoteData remote = RemoteData.getInstance();

			// setting up the request with the session-id
			Request r = new Request(IConstants.REQUEST_UPDATE, Preferences
					.getInstance(context).getString("session", null));
			r.put("lat", local.getLatitude());
			r.put("lon", local.getLongitude());
			r.put("time", local.getTimestamp());
			r.put("speed", local.getSpeed());
			r.put("bbox",
					Preferences.getInstance(context).getString("getDataPref",
							"2"));
			r.put("save",
					Preferences.getInstance(context).getBoolean("sendDataPref",
							true));
			Requester req = Requester.getInstance(context);
			String response = req.contactServerForResult(r.toJson()).trim();

			if (response.contains("error")) {
				Log.e(TAG, "ArgumentError@doInBackground: "
						+ new JSONObject(response).getString("error"));
				return null;
			}

			if (response != null && !response.equals("null")) {
				final JSONObject jobj = new JSONObject(response);

				// parsing the response, creating the overlays and saving the
				// information
				// adding the speedoverlays
				final JSONArray overlays = jobj.getJSONArray("traffic");
				remote.clearOverlays();
				DBWrapper db = DBWrapper.getInstance();
				List<DrawableOverlayItem> listSpeed = new LinkedList<DrawableOverlayItem>();
				for (int i = overlays.length() - 1; i >= 0; i--) {
					final JSONObject obj = overlays.getJSONObject(i);
					listSpeed.add(new SpeedOverlayItem(context, db
							.fetchPoints(obj.getInt("id")), obj
							.getDouble("speed"), obj.getDouble("maxspeed"), obj
							.getDouble("quality")));
				}
				RoadOverlay<DrawableOverlayItem> itemizedSpeedOverlay = new RoadOverlay<DrawableOverlayItem>(
						listSpeed, context);
				remote.addOverlay(itemizedSpeedOverlay);

				// adding the congestions
				remote.setHasCongestions(jobj.has("congestions"));
				if (jobj.has("congestions")) {
					final JSONArray congestions = jobj
							.getJSONArray("congestions");
					Drawable drawable = context.getResources().getDrawable(
							R.drawable.calendar_day);
					List<OverlayItem> list = new LinkedList<OverlayItem>();
					CongestionItem.setContext(context);
					for (int i = congestions.length() - 1; i >= 0; i--) {
						final JSONObject obj = congestions.getJSONObject(i);
						CongestionItem overlayItem = new CongestionItem(
								obj.getInt("id"), obj.getInt("type"),
								new GeoPoint(obj.getDouble("lat"), obj
										.getDouble("lon")),
								obj.getLong("time"));
						list.add(overlayItem);
					}
					ItemizedIconOverlay<OverlayItem> itemizedOverlay = new ItemizedIconOverlay<OverlayItem>(
							list, drawable, gestureListener,
							new DefaultResourceProxyImpl(context));
					remote.addOverlay(itemizedOverlay);
				}

				if (jobj.has("routing")) {
					final Request rRoute = new Request(
							IConstants.REQUEST_GET_ROUTE, Preferences
									.getInstance(context).getString("session",
											null));
					response = Requester.getInstance(context)
							.contactServerForResult(rRoute.toJson());
					if (response.contains("error")) {
						throw new IllegalArgumentException("no route available");
					}
					if (response != null && !response.equals("null")) {
						final JSONObject jResponse = new JSONObject(response);
						final JSONArray jPoints = jResponse
								.getJSONArray("route");
						final List<GeoPoint> points = new LinkedList<GeoPoint>();
						for (int i = 0; i < jPoints.length(); i++) {
							final JSONObject jPoint = jPoints.getJSONObject(i);
							points.add(new GeoPoint(jPoint.getDouble("lat"),
									jPoint.getDouble("lon")));

						}
						final RouteOverlayItem roi = new RouteOverlayItem(
								points);
						List<DrawableOverlayItem> pList = new LinkedList<DrawableOverlayItem>();
						pList.add(roi);
						RoadOverlay<DrawableOverlayItem> ro = new RoadOverlay<DrawableOverlayItem>(
								pList, context.getApplicationContext());
						remote.addOverlay(ro, true);
					}
				}

				// updating the overlays on the map
				MapView mapView = LocalViewContainer.getInstance().getMapView();
				if (mapView != null) {
					mapView.getOverlays().clear();
					mapView.getOverlays().addAll(remote.getOverlays());
					mapView.postInvalidate();
				}

				// updating the informations on the right side
				LimitationsView limitView = LocalViewContainer.getInstance()
						.getLimitView();
				if (limitView != null) {
					limitView.postInvalidate();
				}
			}
		} catch (Exception ex) {
			Log.e(TAG, ex.getClass() + "@doInBackground: " + ex.getMessage());
		}
		return null;
	}

	/**
	 * Listener for the congestions to show additional information and provide
	 * an opportunity to delete them
	 */
	private ItemizedIconOverlay.OnItemGestureListener<OverlayItem> gestureListener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
		@Override
		public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
			Toast.makeText(context, item.getTitle() + ", " + item.getSnippet(),
					Toast.LENGTH_LONG).show();
			return true;
		}

		@Override
		public boolean onItemLongPress(final int index, final OverlayItem item) {
			final CongestionItem cgItem = (CongestionItem) item;
			AlertDialog.Builder builder = new AlertDialog.Builder(
					LocalViewContainer.getInstance().getMapView().getContext());
			builder.setMessage(
					context.getResources().getString(
							R.string.delete_congestion_question))
					.setCancelable(false)
					.setPositiveButton(
							context.getResources().getString(R.string.yes),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									new SendCongestionTask()
											.execute(new SendCongestionTask.CongestionWrapper(
													cgItem.getID(), context,
													false));
									Toast.makeText(
											context,
											context.getResources().getString(
													R.string.delete_congestion),
											Toast.LENGTH_LONG).show();
								}
							})
					.setNegativeButton(
							context.getResources().getString(R.string.no),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
			return true;
		}
	};
}
