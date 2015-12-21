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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;
import org.traffic.jamdroid.R;
import org.traffic.jamdroid.utils.IConstants;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * This class creates overlays for the map which indicate a congestion.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 227 $
 */
public class CongestionItem extends OverlayItem {

	/** The id */
	private int id;
	/** The context of the application */
	private static Context context;
	/** Format of a date */
	private static final String DATE_FORMAT = "dd.MM.yyyy 'um' HH:mm:ss";
	/** Format for the toasts */
	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			DATE_FORMAT);

	/**
	 * Custom-Constructor with all informations provided by the server.
	 * 
	 * @param id
	 *            The id of the congestion
	 * @param type
	 *            The type
	 * @param aGeoPoint
	 *            The position
	 * @param time
	 *            The time of the registration
	 */
	public CongestionItem(int id, int type, GeoPoint aGeoPoint, long time) {
		super(getAdequateTitle(type), context.getResources().getString(
				R.string.since)
				+ " " + sdf.format(new Date(time)), aGeoPoint);
		this.id = id;
		setMarker(getAdequateMarker(type));
	}

	/**
	 * Returns the id.
	 * 
	 * @return The id
	 */
	public int getID() {
		return id;
	}

	/**
	 * Sets the context of the application.
	 * 
	 * @param ctx
	 *            The context
	 */
	public static void setContext(Context ctx) {
		context = ctx;
	}

	/**
	 * Searches the adequate image, which belongs to the passed type
	 * 
	 * @param type
	 *            The type of the congestion
	 * @return An image for the overlay
	 */
	private static Drawable getAdequateMarker(final int type) {
		if (context == null)
			return null;
		switch (type) {
		case IConstants.CONGESTION_JAM:
			return context.getResources().getDrawable(R.drawable.layer_jam);
		case IConstants.CONGESTION_CRASH:
			return context.getResources().getDrawable(R.drawable.layer_crash);
		case IConstants.CONGESTION_CONSTRUCTION:
			return context.getResources().getDrawable(
					R.drawable.layer_construction);
		case IConstants.CONGESTION_ICE:
			return context.getResources().getDrawable(R.drawable.layer_ice);
		case IConstants.CONGESTION_EVENT:
			return context.getResources().getDrawable(R.drawable.layer_event);
		default:
			return context.getResources().getDrawable(R.drawable.layer_general);
		}
	}

	/**
	 * Searches the adequate title of the passed type
	 * 
	 * @param type
	 *            The type of the congestion
	 * @return The title
	 */
	private static String getAdequateTitle(final int type) {
		if (context == null)
			return "";
		switch (type) {
		case IConstants.CONGESTION_JAM:
			return context.getResources().getString(R.string.dsc_jam_jam);
		case IConstants.CONGESTION_CRASH:
			return context.getResources().getString(R.string.dsc_jam_crash);
		case IConstants.CONGESTION_CONSTRUCTION:
			return context.getResources().getString(
					R.string.dsc_jam_construction);
		case IConstants.CONGESTION_ICE:
			return context.getResources().getString(R.string.dsc_jam_ice);
		case IConstants.CONGESTION_EVENT:
			return context.getResources().getString(R.string.dsc_jam_event);
		default:
			return context.getResources().getString(R.string.dsc_jam_general);
		}
	}
}
