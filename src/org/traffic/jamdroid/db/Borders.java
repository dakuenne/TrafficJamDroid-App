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
package org.traffic.jamdroid.db;

/**
 * Wrapper-class for the range of the ids in a database-file.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 225 $
 */
public class Borders {

	/** The minimal id */
	private int minID;
	/** The maximal id */
	private int maxID;

	/**
	 * Custom-Constructor
	 * 
	 * @param minID
	 *            The minimal id
	 * @param maxID
	 *            The maximal id
	 */
	public Borders(final int minID, final int maxID) {
		this.minID = minID;
		this.maxID = maxID;
	}

	/**
	 * Returns the minimal id.
	 * @return The minimal id
	 */
	public int getMinID() {
		return minID;
	}

	/**
	 * Returns the maximal id.
	 * @return The maximal id
	 */
	public int getMaxID() {
		return maxID;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Borders)) {
			return false;
		}
		final Borders other = (Borders) o;
		if (other.getMinID() <= this.getMinID()
				&& this.getMinID() <= other.getMaxID()) {
			return true;
		}
		if (other.getMinID() >= this.getMinID()
				&& other.getMinID() <= this.getMaxID()) {
			return true;
		}
		return false;
	}

}
