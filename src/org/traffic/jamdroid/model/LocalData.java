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

import java.util.LinkedList;
import java.util.List;

import org.osmdroid.util.GeoPoint;

import android.location.Location;
import android.location.LocationListener;

/**
 * This class stores all data measured by the application.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 225 $
 */
public class LocalData {

	/** The one and only instance */
	private static LocalData instance = new LocalData();

	/** The timestamp of the last saved position */
	private long timestamp;

	/** The longitude of the last saved position */
	private double longitude;

	/** The latitude of the last saved position */
	private double latitude;

	/** The speed at the last saved position */
	private float speed;

	/** The list with all current routing points */
	private List<RoutePoint> route;

	/**
	 * Default-Constructor
	 */
	private LocalData() {
		timestamp = -1;
		route = new LinkedList<RoutePoint>();
	}

	/**
	 * Returns the timestamp of the last saved position.
	 * 
	 * @return The timestamp of the last saved position
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Returns the longitude of the last saved position.
	 * 
	 * @return The longitude of the last saved position
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Returns the latitude of the last saved position.
	 * 
	 * @return The latitude of the last saved position
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Returns the speed at the last saved position.
	 * 
	 * @return The speed at the last saved position
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Returns the latitude and longitude as {@link GeoPoint}.
	 * 
	 * @return The last saved position
	 */
	public GeoPoint getGeoPoint() {
		if (longitude > latitude) {
			double help = longitude;
			longitude = latitude;
			latitude = help;
		}
		return new GeoPoint(latitude, longitude);
	}

	/**
	 * Returns the latitude and longitude as {@link Location}.
	 * 
	 * @return The last saved position
	 */
	public Location getAsLocation() {
		Location loc = new Location("SAVED_GPS");
		loc.setLatitude(latitude);
		loc.setLongitude(longitude);
		loc.setSpeed(speed);
		return loc;
	}

	/**
	 * Adds a point for the route at the <i>positions</i>.
	 * 
	 * @param position
	 *            The position in the list.
	 * @param point
	 *            The new point
	 */
	public void addRoutingPoint(final int position, final RoutePoint point) {
		route.add(position, point);
	}

	/**
	 * Adds a point for the route.
	 * 
	 * @param point
	 *            The new point
	 */
	public void addRoutingPoint(final RoutePoint point) {
		route.add(point);
	}

	/**
	 * Returns the route.
	 * 
	 * @return The route
	 */
	public List<RoutePoint> getRoute() {
		return route;
	}

	/**
	 * Sets all data provided by the {@link LocationListener}.
	 * 
	 * @param timestamp
	 *            The timestamp of the measurement
	 * @param latitude
	 *            The latitude of the measurement
	 * @param longitude
	 *            The longitude of the measurement
	 * @param speed
	 *            The speed of the measurement
	 */
	public void setData(long timestamp, double latitude, double longitude,
			float speed) {
		this.timestamp = timestamp;
		this.longitude = longitude;
		this.latitude = latitude;
		this.speed = speed;
	}

	/**
	 * Checks whether data is set or not.
	 * 
	 * @return The status of the data
	 */
	public boolean hasData() {
		return (timestamp >= 0);
	}

	@Override
	public String toString() {
		return "" + timestamp + ";" + longitude + ";" + latitude + ";";
	}

	/**
	 * Returns the one and only instance.
	 * 
	 * @return The singleton
	 */
	public static LocalData getInstance() {
		return instance;
	}
}
