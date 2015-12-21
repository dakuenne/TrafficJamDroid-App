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
import java.util.Locale;

import org.traffic.jamdroid.R;
import org.traffic.jamdroid.model.LocalData;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * This view shows the user the information about his driving. For example the
 * speed and the name of the road he is driving on.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 227 $
 */
public class InfoView extends LinearLayout {

	/** View showing the current speed of the user */
	private final TextView txtSpeed;

	/** View showing the name of the current road */
	private final TextView txtRoad;

	/**
	 * Custom-Constructor that is called when inflating a view from XML.
	 * 
	 * @param context
	 *            The {@link Context} the view is running in
	 * @param attr
	 *            The attributes of the XML tag that is inflating the view
	 */
	public InfoView(final Context context, final AttributeSet attr) {
		this(context, attr, null);
	}

	/**
	 * Custom-Constructor that is called when inflating a view from XML.
	 * 
	 * @param context
	 *            The {@link Context} the view is running in
	 * @param pw
	 *            A popup window that can be used to display an arbitrary view
	 */
	public InfoView(final Context context, final PopupWindow pw) {
		this(context, null, pw);
	}

	/**
	 * Custom-Constructor to merge all other constructors.
	 * 
	 * @param context
	 *            The {@link Context} the view is running in
	 * @param attrs
	 *            The attributes of the XML tag that is inflating the view
	 * @param pw
	 *            A popup window that can be used to display an arbitrary view
	 */
	public InfoView(final Context context, final AttributeSet attrs,
			final PopupWindow pw) {
		super(context, attrs);
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = layoutInflater.inflate(R.layout.info_view, this);
		txtSpeed = (TextView) layout.findViewById(R.id.speed);
		txtRoad = (TextView) layout.findViewById(R.id.road);
	}
	
	/**
	 * Inner class to load the name of the current street with a
	 * {@link Geocoder}. Avoid blocking the UI by using an {@link AsyncTask}.
	 * 
	 * @author Daniel Kuenne
	 * @version $LastChangedRevision: 227 $
	 */
	public class GeocodingTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			String street = "---";
			try {
				final LocalData gpsdata = LocalData.getInstance();
				Geocoder geocoder = new Geocoder(InfoView.this.getContext(),
						Locale.getDefault());
				List<Address> addresses = geocoder.getFromLocation(
						gpsdata.getLatitude(), gpsdata.getLongitude(), 1);
				if (addresses.size() > 0
						&& addresses.get(0).getThoroughfare() != null) {
					street = addresses.get(0).getThoroughfare();
				} else {
					street = "---";
				}
			} catch (Exception e) {
				street = "---";
			}
			return street;
		}

		@Override
		protected void onPostExecute(String result) {
			txtSpeed.setText("" + (int) LocalData.getInstance().getSpeed()
					+ " km/h");
			txtRoad.setText(result);
			InfoView.this.postInvalidate();
		}
	}
}
