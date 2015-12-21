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

import org.traffic.jamdroid.R;

/**
 * Wrapper-class for the six different congestion-types.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 225 $
 */
public enum Option {
	One(R.id.rb1), Two(R.id.rb2), Three(R.id.rb3), Four(R.id.rb4), Five(
			R.id.rb5), Six(R.id.rb6);

	/** The id of the current option */
	int idInLayout;

	/**
	 * Custom-Constructor
	 * 
	 * @param idInLayout
	 *            The id of the option
	 */
	private Option(int idInLayout) {
		this.idInLayout = idInLayout;
	}

	/**
	 * Returns the id of the selected option
	 * 
	 * @return The option
	 */
	public int getIdInLayout() {
		return idInLayout;
	}
}
