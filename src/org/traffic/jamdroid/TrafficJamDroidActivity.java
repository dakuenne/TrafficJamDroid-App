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
package org.traffic.jamdroid;

import org.osmdroid.views.MapView;
import org.traffic.jamdroid.activities.BaseActivity;
import org.traffic.jamdroid.model.DataUpdater;
import org.traffic.jamdroid.model.LocalData;
import org.traffic.jamdroid.model.LocalViewContainer;
import org.traffic.jamdroid.model.RemoteData;
import org.traffic.jamdroid.services.HandshakeTask;
import org.traffic.jamdroid.views.InfoView;
import org.traffic.jamdroid.views.LimitationsView;
import org.traffic.jamdroid.views.overlays.LocationOverlay;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;

/**
 * Initial {@link android.app.Activity} of the application <i>TrafficJamDroid</i>.
 * <p>
 * If the application is started for the first time, it performs a
 * <i>Three-Way-Handshake</i> to get a session-id. This id is refreshed every 12
 * hours to disable profiling.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 235 $
 * @see Handler
 * @see DataUpdater
 * @see HandshakeTask
 */
public class TrafficJamDroidActivity extends BaseActivity {

	/** The Handler to perform the updates in a background-thread */
	private Handler updateHandler = new Handler();

	/** The process to initialize the updates */
	private DataUpdater mDataUpdater;

	/** The map */
	private MapView mapView;

	/** The overlay indicating the current position */
	private LocationOverlay actualPosition;
	//private MyMockLocationOverlay actualPosition;
	
	/** The PowerManager to protect the dimming of the screen */
	private PowerManager.WakeLock wl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		// performing a three-way-handshake to get a new id
		new HandshakeTask().execute(getApplicationContext());
		
		// setting up the wake-lock
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "TrafficJamDroidActivity");

		mDataUpdater = new DataUpdater(getApplicationContext(), updateHandler);
		mapView = (MapView) findViewById(R.id.mapview);

		// saving the view-components for the update services
		LocalViewContainer.getInstance().setMapView(mapView);
		LocalViewContainer.getInstance().setInfoView(
				(InfoView) findViewById(R.id.mapinfo));
		LocalViewContainer.getInstance().setLimitView(
				(LimitationsView) findViewById(R.id.maplimits));

		// setting up the options of the map
		mapView.setBuiltInZoomControls(true);
		mapView.setMultiTouchControls(true);
		mapView.getController().setZoom(15);

		// setting up the overlay for the current position
		actualPosition = new LocationOverlay(this, mapView);
		//actualPosition = new MyMockLocationOverlay(this, mapView);
		actualPosition.setLocationUpdateMinDistance(50);
		actualPosition.setLocationUpdateMinTime(5000);
		
		actualPosition.runOnFirstFix(new Runnable() {

			public void run() {
				mapView.getController().animateTo(
						actualPosition.getMyLocation());
			}
		});
		RemoteData.getInstance().addOverlay(actualPosition, true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// acquire the lock
		wl.acquire();

		// setting up the map
		mapView.getController()
				.setCenter(LocalData.getInstance().getGeoPoint());
		mapView.getOverlays().clear();
		mapView.getOverlays().addAll(RemoteData.getInstance().getOverlays());

		// following the location of the user
		actualPosition.enableMyLocation();
		actualPosition.enableFollowLocation();

		// starting the updates
		updateHandler.removeCallbacks(mDataUpdater);
		updateHandler.postDelayed(mDataUpdater, 1000);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		// release the lock
		wl.release();

		// disabling the location
		actualPosition.disableMyLocation();
		actualPosition.disableFollowLocation();

		// stopping the updates
		updateHandler.removeCallbacks(mDataUpdater);
	}
}