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

import org.osmdroid.views.MapView;
import org.traffic.jamdroid.views.InfoView;
import org.traffic.jamdroid.views.LimitationsView;

/**
 * Class to handle the different view-components. It allows the update-process
 * to refresh the views.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 225 $
 * @see MapView
 * @see InfoView
 * @see LimitationsView
 */
public class LocalViewContainer {

	/** The one and only instance */
	private static LocalViewContainer data = new LocalViewContainer();

	/** The view with the overview map */
	private MapView mapView;
	
	/** The view with informations about the current speed */
	private InfoView infoView;
	
	/** The view with the limitations for the current street */
	private LimitationsView limitView;

	/**
	 * Returns a reference of the map.
	 * @return The map
	 */
	public MapView getMapView() {
		return mapView;
	}

	/**
	 * Returns a reference of the {@link InfoView}.
	 * @return The view
	 */
	public InfoView getInfoView() {
		return infoView;
	}
	
	/**
	 * Returns a reference of the {@link LimitationsView}.
	 * @return The view
	 */
	public LimitationsView getLimitView() {
		return limitView;
	}

	/**
	 * Sets the view.
	 * @param mapView The new view
	 */
	public void setMapView(MapView mapView) {
		this.mapView = mapView;
	}

	/**
	 * Sets the view.
	 * @param infoView The new view
	 */
	public void setInfoView(InfoView infoView) {
		this.infoView = infoView;
	}

	/**
	 * Sets the view.
	 * @param limitView The new view
	 */
	public void setLimitView(LimitationsView limitView) {
		this.limitView = limitView;
	}

	/**
	 * Returns the one and only instance.
	 * @return The singleton
	 */
	public static LocalViewContainer getInstance() {
		return data;
	}

}
