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

import android.location.Location;

/**
 * Wrapper-class for the data which is generated within the search of an
 * address.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 225 $
 */
public class SearchData {

	/** The one and only instance */
	private static SearchData data = new SearchData();

	/** The selected position */
	private Location position;

	/**
	 * Default-Constructor
	 */
	private SearchData() {
		position = new Location("INIT");
	}

	/**
	 * Sets the selected position.
	 * @param position The new position
	 */
	public void setPosition(Location position) {
		this.position = position;

	}

	/**
	 * Returns the position.
	 * @return The position
	 */
	public Location getPosition() {
		return position;
	}

	/**
	 * Returns the one and only instance.
	 * @return The singleton
	 */
	public static SearchData getInstance() {
		return data;
	}
}
