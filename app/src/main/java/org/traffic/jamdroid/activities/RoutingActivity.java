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
package org.traffic.jamdroid.activities;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.traffic.jamdroid.R;
import org.traffic.jamdroid.model.LocalData;
import org.traffic.jamdroid.model.Preferences;
import org.traffic.jamdroid.model.RemoteData;
import org.traffic.jamdroid.model.RoutePoint;
import org.traffic.jamdroid.model.SearchData;
import org.traffic.jamdroid.utils.IConstants;
import org.traffic.jamdroid.utils.Request;
import org.traffic.jamdroid.utils.Requester;
import org.traffic.jamdroid.views.RoutingAdapter;
import org.traffic.jamdroid.views.overlays.DrawableOverlayItem;
import org.traffic.jamdroid.views.overlays.RoadOverlay;
import org.traffic.jamdroid.views.overlays.RouteOverlayItem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;

/**
 * This activity provides all functionality for the routing interface. It allows
 * the user to select the current position as starting position and search more
 * points by address or by click.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 224 $
 */
public class RoutingActivity extends BaseActivity {

	/** The list with all targets */
	private ListView lstRoute;

	/** The adapter to handle the list with all targets */
	private RoutingAdapter arrayAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.routing);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

		LocalData.getInstance().getRoute().clear();

		// initializing the array adapter
		arrayAdapter = new RoutingAdapter(RoutingActivity.this,
				R.layout.listitem, LocalData.getInstance().getRoute());

		lstRoute = (ListView) findViewById(R.id.routingpoints);
		lstRoute.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onItemClick(AdapterView arg0, View view, int position,
					long id) {
				// user clicked a list item, make it "selected"
				arrayAdapter.setSelectedPosition(position);
			}
		});

		// set the above adapter as the adapter of choice for our list
		lstRoute.setAdapter(arrayAdapter);

		// initializing the checkbox for the "current position"
		final CheckBox currentPosition = (CheckBox) findViewById(R.id.start_current_position);
		currentPosition
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// creating the point
							new CreateRoutingPointTask(LocalData.getInstance()
									.getLatitude(), LocalData.getInstance()
									.getLongitude()).execute(0);
						} else {
							// deleting the point
							if (LocalData.getInstance().getRoute().size() > 0)
								LocalData.getInstance().getRoute().remove(0);
							arrayAdapter.notifyDataSetChanged();
						}

					}
				});

		// initializing the button to search an address
		final ImageButton searchAddress = (ImageButton) findViewById(R.id.btn_search_address);
		searchAddress.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(RoutingActivity.this,
						SearchActivity.class);
				startActivityForResult(i, 1);
			}
		});

		// move up event handler
		ImageButton btnMoveUp = (ImageButton) findViewById(R.id.btn_up);
		btnMoveUp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				moveUp();
			}
		});

		// move down event handler
		ImageButton btnMoveDown = (ImageButton) findViewById(R.id.btn_down);
		btnMoveDown.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				moveDown();
			}
		});

		// calculating the route
		Button btnCalcRoute = (Button) findViewById(R.id.btn_calc_route);
		btnCalcRoute.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new CalculateRouteTask().execute();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			final Location loc = SearchData.getInstance().getPosition();
			new CreateRoutingPointTask(loc.getLatitude(), loc.getLongitude())
					.execute();
		}
	}

	/**
	 * Moves the selected item "up" in the list.
	 */
	private void moveUp() {
		int selectedPos = arrayAdapter.getSelectedPosition();
		if (selectedPos > 0) {
			final RoutePoint rp = LocalData.getInstance().getRoute()
					.remove(selectedPos);
			LocalData.getInstance().getRoute().add(selectedPos - 1, rp);
			// set selected position in the adapter
			arrayAdapter.setSelectedPosition(selectedPos - 1);
		}
	}

	/**
	 * Moves the selected item "down" in the list.
	 */
	private void moveDown() {
		int selectedPos = arrayAdapter.getSelectedPosition();
		if (selectedPos < LocalData.getInstance().getRoute().size() - 1) {
			RoutePoint rp = LocalData.getInstance().getRoute()
					.remove(selectedPos);
			LocalData.getInstance().getRoute().add(selectedPos + 1, rp);
			// set selected position in the adapter
			arrayAdapter.setSelectedPosition(selectedPos + 1);
		}
	}

	/**
	 * Task to search the address to a given coordinate. If the address is found
	 * a new point for the route is created.
	 * 
	 * @author Daniel Kuenne
	 * @version $LastChangedRevision: 224 $
	 */
	private class CreateRoutingPointTask extends AsyncTask<Integer, Void, Void> {

		/** The dialog to show the users that the data is loading */
		private ProgressDialog m_dialog;

		/** The latitude of the position which should be added to the route */
		private final double lat;
		/** The longitude of the position which should be added to the route */
		private final double lon;

		/**
		 * Custom-Constructor for this task.
		 * 
		 * @param lat
		 *            The latitude of the position
		 * @param lon
		 *            The longitude of the position
		 */
		public CreateRoutingPointTask(final double lat, final double lon) {
			this.lat = lat;
			this.lon = lon;
		}

		@Override
		protected void onPreExecute() {
			m_dialog = ProgressDialog.show(RoutingActivity.this, "",
					RoutingActivity.this.getApplicationContext().getResources()
							.getString(R.string.popup_generate_info));
		}

		@Override
		protected Void doInBackground(Integer... params) {
			String address = "";
			try {
				final Geocoder gc = new Geocoder(RoutingActivity.this);
				List<Address> l = new LinkedList<Address>();
				l = gc.getFromLocation(lat, lon, 1);
				if (l.size() > 0) {
					final Address a = l.get(0);
					address = a.getAddressLine(0) + ", " + a.getAddressLine(1)
							+ ", " + a.getAddressLine(2);
				}
			} catch (Exception e) {
				Log.e("CreateRoutingPointTask", e.getClass().getSimpleName()
						+ "@doInBackground: " + e.getMessage());
			}
			// adding the position to the route
			if (params.length > 0) {
				LocalData.getInstance().addRoutingPoint(params[0],
						new RoutePoint(address, lat, lon));
			} else {
				LocalData.getInstance().addRoutingPoint(
						new RoutePoint(address, lat, lon));
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			RoutingActivity.this.arrayAdapter.notifyDataSetChanged();
			if (m_dialog.isShowing()) {
				m_dialog.dismiss();
			}
		}

	}

	/**
	 * Task to initialize the calculation of the route. This tasks sends a
	 * request to the server to calculate the route, parses the returning data
	 * and visualizes it with an overlay. If no route is available a popup is
	 * shown.
	 * 
	 * @author Daniel Kuenne
	 * @version $LastChangedRevision: 224 $
	 */
	private class CalculateRouteTask extends AsyncTask<Void, Void, String> {

		/** The dialog to show the users that the data is loading */
		private ProgressDialog m_dialog;

		@Override
		protected void onPreExecute() {
			m_dialog = ProgressDialog.show(RoutingActivity.this, "",
					RoutingActivity.this.getApplicationContext().getResources()
							.getString(R.string.popup_calculate_route));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				final Request r = new Request(
						IConstants.REQUEST_CALCULATE_ROUTE, Preferences
								.getInstance(getApplicationContext())
								.getString("session", null));

				// building the request
				final JSONArray jarray = new JSONArray();
				final List<RoutePoint> l = LocalData.getInstance().getRoute();
				for (RoutePoint rp : l) {
					try {
						final JSONObject jobject = new JSONObject();
						jobject.put("lat",
								rp.getPosition().getLatitudeE6() / 1E6);
						jobject.put("lon",
								rp.getPosition().getLongitudeE6() / 1E6);
						jarray.put(jobject);
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
				r.put("route", jarray);
				String response = Requester
						.getInstance(getApplicationContext())
						.contactServerForResult(r.toJson(), 120000);
				if (response.contains("error")) {
					throw new IllegalArgumentException("no route available");
				}

				// getting the route
				final Request r2 = new Request(IConstants.REQUEST_GET_ROUTE,
						Preferences.getInstance(getApplicationContext())
								.getString("session", null));
				response = Requester.getInstance(getApplicationContext())
						.contactServerForResult(r2.toJson());

				if (response.contains("error")) {
					throw new IllegalArgumentException("no route available");
				}

				// parsing the json and creating the overlay
				if (response != null && !response.equals("null")) {
					final JSONObject jResponse = new JSONObject(response);
					final JSONArray jPoints = jResponse.getJSONArray("route");
					final List<GeoPoint> points = new LinkedList<GeoPoint>();
					for (int i = 0; i < jPoints.length(); i++) {
						final JSONObject jPoint = jPoints.getJSONObject(i);
						points.add(new GeoPoint(jPoint.getDouble("lat"), jPoint
								.getDouble("lon")));

					}
					final RouteOverlayItem roi = new RouteOverlayItem(points);
					List<DrawableOverlayItem> pList = new LinkedList<DrawableOverlayItem>();
					pList.add(roi);
					RoadOverlay<DrawableOverlayItem> ro = new RoadOverlay<DrawableOverlayItem>(
							pList, getApplicationContext());
					RemoteData.getInstance().addOverlay(ro, true);
				}
			} catch (Exception e) {
				Log.e("CalculateRouteTask", e.getClass().getSimpleName()
						+ "@doInBackground: " + e.getMessage());
				return e.getMessage();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Resources res = RoutingActivity.this.getApplicationContext()
					.getResources();
			// route not available
			if (result != null) {
				m_dialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						RoutingActivity.this);
				builder.setMessage(
						res.getString(R.string.popup_no_route_available))
						.setCancelable(false)
						.setPositiveButton(res.getString(R.string.ok),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				builder.create().show();
			}

			// if route available -> close dialog
			if (m_dialog.isShowing()) {
				m_dialog.dismiss();
				RoutingActivity.this.finish();
			}
		}

	}
}
