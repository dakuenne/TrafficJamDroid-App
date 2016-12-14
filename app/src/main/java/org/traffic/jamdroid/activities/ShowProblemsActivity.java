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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.traffic.jamdroid.R;
import org.traffic.jamdroid.db.DBWrapper;
import org.traffic.jamdroid.views.overlays.DrawableOverlayItem;
import org.traffic.jamdroid.views.overlays.RoadOverlay;
import org.traffic.jamdroid.views.overlays.SpeedOverlayItem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * This activity visualizes the identified recurring problems. It is called by
 * the selection of a problem in the {@link KnownProblemsActivity}.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 191 $
 * @see MapView
 * @see ProgressDialog
 */
public class ShowProblemsActivity extends AppCompatActivity {

	/** The dialog to show the users that the data is loading */
	private ProgressDialog pd = null;

	/** The map to visualize the information */
	private MapView mapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.problemmap);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		setTitle(R.string.alert_prob);

		mapView = (MapView) findViewById(R.id.probmapview);
		this.pd = ProgressDialog.show(this, "", getApplicationContext()
				.getResources().getString(R.string.popup_visualize_problems));
		new GetProblemsTask().execute(getIntent());

		// setting up the options of the map
		mapView.setBuiltInZoomControls(true);
		mapView.setMultiTouchControls(true);
		mapView.getController().setZoom(15);
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	/**
	 * A task to create all needed overlays for the map.
	 * 
	 * @author Daniel Kuenne
	 * @version $LastChangedRevision: 191 $
	 */
	private class GetProblemsTask extends AsyncTask<Intent, Void, GeoPoint> {

		@Override
		protected GeoPoint doInBackground(Intent... args) {
			final ArrayList<Integer> streets = args[0]
					.getIntegerArrayListExtra("data");
			final List<DrawableOverlayItem> listSpeed = new LinkedList<DrawableOverlayItem>();

			// loading the streets
			final DBWrapper db = DBWrapper.getInstance();
			GeoPoint center = null;
			for (int i = streets.size() - 1; i >= 0; i--) {
				List<GeoPoint> points = db.fetchPoints(streets.get(i));
				center = points.get((int) (Math.random() * points.size()));
				listSpeed.add(new SpeedOverlayItem(getApplicationContext(),
						points, Color.BLUE));
			}
			// drawing the overlays
			RoadOverlay<DrawableOverlayItem> itemizedSpeedOverlay = new RoadOverlay<DrawableOverlayItem>(
					listSpeed, getApplicationContext());
			mapView.getOverlays().add(itemizedSpeedOverlay);
			return center;
		}

		@Override
		protected void onPostExecute(GeoPoint result) {
			if (result != null) {
				ShowProblemsActivity.this.mapView.getController().setCenter(
						result);
			}
			if (ShowProblemsActivity.this.pd != null) {
				ShowProblemsActivity.this.pd.dismiss();
			}

		}
	}
}
