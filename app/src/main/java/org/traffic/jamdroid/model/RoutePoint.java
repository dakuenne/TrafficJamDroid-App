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
package org.traffic.jamdroid.model;

import org.osmdroid.util.GeoPoint;

/**
 * Wrapper-class for the points of a route.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 225 $
 */
public class RoutePoint {

	/** The address-line */
	private String address;
	/** The gps-position */
	private GeoPoint position;

	/**
	 * Custom-Constructor with latitude and longitude.
	 * 
	 * @param address
	 *            The address-line
	 * @param lat
	 *            The latitude of the point
	 * @param lon
	 *            The longitude of the point
	 */
	public RoutePoint(final String address, final double lat, final double lon) {
		this(address, new GeoPoint(lat, lon));
	}

	/**
	 * Custom-Constructor with a {@link GeoPoint}.
	 * 
	 * @param address
	 *            The address-line
	 * @param point
	 *            The point
	 */
	private RoutePoint(final String address, final GeoPoint point) {
		this.address = address;
		this.position = point;
	}

	/**
	 * Returns the address.
	 * 
	 * @return The address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Returns the position.
	 * 
	 * @return The position
	 */
	public GeoPoint getPosition() {
		return position;
	}
}
