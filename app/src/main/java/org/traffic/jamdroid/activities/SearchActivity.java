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

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Overlay;
import org.traffic.jamdroid.R;
import org.traffic.jamdroid.model.LocalData;
import org.traffic.jamdroid.model.SearchData;
import org.traffic.jamdroid.views.SearchView;
import org.traffic.jamdroid.views.overlays.RoutingPointOverlay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * This activity provides a search-dialog. The user can search a location by
 * entering an address or description and he can select a position on the map.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 191 $
 */
public class SearchActivity extends Activity {

	/** The displayed map */
	private SearchView map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_address);

		// setting up the map
		map = (SearchView) findViewById(R.id.prev_map);
		if (map != null) {
			map.setBuiltInZoomControls(false);
			map.getController().setZoom(15);
			map.getController()
					.setCenter(LocalData.getInstance().getGeoPoint());
			map.setMultiTouchControls(true);
		}

		// resetting the searcg
		SearchData.getInstance().setPosition(null);

		// initializing the submit-button
		final Button okay = (Button) findViewById(R.id.okay);
		okay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent out = new Intent();
				// adding the last location to the answer
				if (SearchData.getInstance().getPosition() != null) {
					Location loc = SearchData.getInstance().getPosition();
					setResult(1, out);
					out.putExtra("lat", loc.getLatitude());
					out.putExtra("lon", loc.getLongitude());
				}
				finish();
			}
		});

		// initializing the cancel-button
		final Button cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// initializing the button to search addresses
		final Button search = (Button) findViewById(R.id.search_address);
		search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					Geocoder gc = new Geocoder(SearchActivity.this);
					List<Address> l = new LinkedList<Address>();

					// searching the best matching five addresses
					final EditText searchfield = (EditText) findViewById(R.id.searchtext);
					if (searchfield.getText().length() == 0) {
						return;
					}
					l = gc.getFromLocationName(
							searchfield.getText().toString(), 5);

					final CharSequence[] items = new CharSequence[l.size()];
					int i = 0;
					for (Address a : l) {
						if (a.getAddressLine(0) != null
								&& a.getAddressLine(1) != null
								&& a.getAddressLine(2) != null) {
							items[i] = a.getAddressLine(0) + ", "
									+ a.getAddressLine(1) + ", "
									+ a.getAddressLine(2);
							i++;
						}
					}

					// showing a dialog for the selection of an address
					final List<Address> ld = l;
					AlertDialog.Builder builder = new AlertDialog.Builder(
							SearchActivity.this);
					builder.setTitle(R.string.routedialog);
					builder.setItems(items,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int item) {
									// saving the clicked position and showing
									// it on the map
									final Location loc = new Location("CLICK");
									loc.setLatitude(ld.get(item).getLatitude());
									loc.setLongitude(ld.get(item)
											.getLongitude());
									SearchData.getInstance().setPosition(loc);
									addOverlay(new GeoPoint(loc));
								}
							});
					AlertDialog alert = builder.create();
					alert.show();
				} catch (Exception e) {
					Log.w("SearchActivity", e.getClass().getSimpleName()
							+ "@search.onClick: " + e.getMessage());
				}

			}
		});
	}

	/**
	 * Adds an {@link Overlay} on the selected position.
	 * 
	 * @param point
	 *            The selected position
	 */
	private void addOverlay(final GeoPoint point) {
		RoutingPointOverlay rpo = new RoutingPointOverlay(
				getApplicationContext(), point);
		map.getOverlays().clear();
		map.getOverlays().add(rpo);
		map.getController().setCenter(point);
		map.invalidate();
	}
}
