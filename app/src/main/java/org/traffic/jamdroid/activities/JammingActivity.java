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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;

import org.traffic.jamdroid.R;
import org.traffic.jamdroid.model.LocalData;
import org.traffic.jamdroid.model.Option;
import org.traffic.jamdroid.model.SearchData;
import org.traffic.jamdroid.services.SendCongestionTask;
import org.traffic.jamdroid.utils.IConstants;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * This activity provides all functionality used for the collection of
 * congestion-information. It allows the user to select one of six types and the
 * position of the problem and sends the information to the server.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 224 $
 */
public class JammingActivity extends BaseActivity {

	/** The location of the message */
	private Location position;

	/** The field with the name of the street */
	private TextView txtPosition;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jamming);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		txtPosition = ((TextView) findViewById(R.id.cur_position));

		// initialize the onCLickListener for all six checkboxes
		CheckBox.OnClickListener optionOnClickListener = new CheckBox.OnClickListener() {
			public void onClick(View v) {
				Option option = (Option) v.getTag();
				exclusivelySetOption(option);
			}
		};

		// enabling the listener
		for (Option option : Option.values()) {
			CheckBox radio = (CheckBox) findViewById(option.getIdInLayout());

			if (radio != null) {
				radio.setOnClickListener(optionOnClickListener);
				radio.setTag(option);
			}
		}
		extractCaption(LocalData.getInstance().getAsLocation());

		// initializing the button for the search of a location
		final ImageButton searchPos = ((ImageButton) findViewById(R.id.btn_jam_search));
		searchPos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(JammingActivity.this,
						SearchActivity.class);
				startActivityForResult(i, 1);
			}
		});

		// initializing the submit.button
		final Button submit = (Button) findViewById(R.id.btn_submit);
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int id = 5;
				for (Option option : Option.values()) {
					CheckBox radio = (CheckBox) findViewById(option
							.getIdInLayout());

					if (radio.isChecked()) {
						id = getCongestionIdByDescription(radio.getText()
								.toString());
						break;
					}
				}
				// sending the congestion to the server
				new SendCongestionTask()
						.execute(new SendCongestionTask.CongestionWrapper(
								position, id, getApplicationContext(), true));

				setResult(RESULT_OK, new Intent());
				finish();
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		Option currentOption = Option.One;
		exclusivelySetOption(currentOption);
	}

	/**
	 * Sets the name of the street.
	 * 
	 * @param point
	 *            The selected instance of {@link Location}
	 */
	private void extractCaption(Location point) {
		if (point == null)
			return;
		position = point;
		this.new SetCaptionTask().execute(point);

		BigDecimal lat = new BigDecimal(point.getLatitude()).setScale(4,
				RoundingMode.HALF_EVEN);
		BigDecimal lon = new BigDecimal(point.getLongitude()).setScale(4,
				RoundingMode.HALF_EVEN);

		txtPosition.setText(lon + ", " + lat);
	}

	/**
	 * Provides the functionality to check only one congestion-type.
	 * 
	 * @param selectedOption
	 *            The checked element
	 */
	private void exclusivelySetOption(Option selectedOption) {
		for (Option option : Option.values()) {
			CheckBox radio = (CheckBox) findViewById(option.getIdInLayout());
			if (radio != null) {
				radio.setChecked(option.ordinal() == selectedOption.ordinal());
			}
		}
	}

	/**
	 * Searches the id of a congestion in {@link IConstants}.
	 * 
	 * @param desc
	 *            The description of a congestion
	 * @return The id used by the program
	 */
	private int getCongestionIdByDescription(String desc) {
		if (desc.equals(this.getString(R.string.dsc_jam_jam)))
			return IConstants.CONGESTION_JAM;
		if (desc.equals(this.getString(R.string.dsc_jam_crash)))
			return IConstants.CONGESTION_CRASH;
		if (desc.equals(this.getString(R.string.dsc_jam_construction)))
			return IConstants.CONGESTION_CONSTRUCTION;
		if (desc.equals(this.getString(R.string.dsc_jam_ice)))
			return IConstants.CONGESTION_ICE;
		if (desc.equals(this.getString(R.string.dsc_jam_event)))
			return IConstants.CONGESTION_EVENT;
		return IConstants.CONGESTION_GENERAL;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			extractCaption(SearchData.getInstance().getPosition());
		}
	}

	/**
	 * Task to search for the name of the selected street. If no matching street
	 * can be found the gps-coordinate is shown.
	 * 
	 * @author Daniel Kuenne
	 * @version $LastChangedRevision: 224 $
	 */
	private class SetCaptionTask extends AsyncTask<Location, Void, String> {

		@Override
		protected String doInBackground(Location... params) {
			Location point = params[0];
			final Geocoder gc = new Geocoder(getApplicationContext());
			String text;
			List<Address> l = new LinkedList<Address>();
			try {
				l = gc.getFromLocation(point.getLatitude(),
						point.getLongitude(), 1);
			} catch (Exception e) {
				Log.w("SetCaptionTask", e.getClass().getSimpleName() + "@doInBackground: " + e.getMessage());
			}
			if (l.size() > 0 && l.get(0).getThoroughfare() != null) {
				text = (l.get(0).getThoroughfare());
			} else {
				BigDecimal lat = new BigDecimal(point.getLatitude()).setScale(
						4, RoundingMode.HALF_EVEN);
				BigDecimal lon = new BigDecimal(point.getLongitude()).setScale(
						4, RoundingMode.HALF_EVEN);
				text = lon + ", " + lat;
			}
			return text;
		}

		@Override
		protected void onPostExecute(String result) {
			txtPosition.setText(result);
		}

	}
}