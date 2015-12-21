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

import org.traffic.jamdroid.R;
import org.traffic.jamdroid.model.RoutePoint;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Class to provide some additional functionality to the list with the positions
 * of a new route.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 227 $
 */
public class RoutingAdapter extends ArrayAdapter<RoutePoint> {

	/** The unique id */
	private int resource;
	/** The selected item */
	private int selectedPos = -1;

	/**
	 * Custom-Constructor to create an adapter-class for the provided items.
	 * 
	 * @param context
	 *            The context of the application
	 * @param resource
	 *            The id of the resource
	 * @param items
	 *            A list with items for this adapter
	 */
	public RoutingAdapter(Context context, int resource, List<RoutePoint> items) {
		super(context, resource, items);
		this.resource = resource;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout routingView;
		// Get the current alert object
		RoutePoint rp = getItem(position);

		// Inflate the view
		if (convertView == null) {
			routingView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi;
			vi = (LayoutInflater) getContext().getSystemService(inflater);
			vi.inflate(resource, routingView, true);
		} else {
			routingView = (LinearLayout) convertView;
		}
		// Get the text boxes from the listitem.xml file
		TextView alertText = (TextView) routingView
				.findViewById(R.id.txt_address);
		TextView alertDate = (TextView) routingView
				.findViewById(R.id.txt_position);

		if (selectedPos == position) {
			alertText.setBackgroundColor(Color.CYAN);
			alertDate.setBackgroundColor(Color.CYAN);
		} else {
			alertText.setBackgroundColor(Color.WHITE);
			alertDate.setBackgroundColor(Color.WHITE);
		}

		// Assign the appropriate data from our alert object above
		alertText.setText(rp.getAddress());
		alertDate.setText(rp.getPosition() + "");

		return routingView;
	}

	/**
	 * Sets the position.
	 * 
	 * @param pos
	 *            The selected position
	 */
	public void setSelectedPosition(int pos) {
		selectedPos = pos;
		// inform the view of this change
		notifyDataSetChanged();
	}

	/**
	 * Returns the selected position.
	 * 
	 * @return The position
	 */
	public int getSelectedPosition() {
		return selectedPos;
	}
}
