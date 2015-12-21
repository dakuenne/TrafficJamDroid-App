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
package org.traffic.jamdroid.services;

import org.osmdroid.util.BoundingBoxE6;
import org.traffic.jamdroid.model.LocalDBMonitor;
import org.traffic.jamdroid.model.LocalViewContainer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Task to check if all needed database-files are on the device. If not the
 * files are downloaded.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 226 $
 */
public class UpdateDBTask extends AsyncTask<Context, Void, Void> {

	@Override
	protected Void doInBackground(Context... arg0) {
		try {
			final BoundingBoxE6 bbox = LocalViewContainer.getInstance()
					.getMapView().getBoundingBox();
			final int minLat = (int) (bbox.getLatSouthE6() / 1E6);
			final int maxLat = (int) (bbox.getLatNorthE6() / 1E6);
			final int minLng = (int) (bbox.getLonEastE6() / 1E6);
			final int maxLng = (int) (bbox.getLonWestE6() / 1E6);

			if (minLat != 0 && minLng != 0) {
				LocalDBMonitor.checkAndGetDBs(minLat, maxLat, minLng, maxLng,
						arg0[0]);
			}
		} catch (Exception ex) {
			Log.e("UpdateDBTask", ex.getClass().getSimpleName()
					+ "@doInBackground: " + ex.getMessage());
		}
		return null;
	}

}
