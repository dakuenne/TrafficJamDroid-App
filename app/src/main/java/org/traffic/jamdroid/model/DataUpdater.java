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

import org.traffic.jamdroid.services.HandshakeTask;
import org.traffic.jamdroid.services.RefreshIDTask;
import org.traffic.jamdroid.services.UpdateDBTask;
import org.traffic.jamdroid.services.UpdateGPSTask;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * Thread which controls the update-sequences.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 225 $
 */
public class DataUpdater implements Runnable {

	/** The context of the application */
	private final Context ctx;
	/** The handler to restart this thread */
	private final Handler handler;

	/**
	 * Custom-Constructor for this class.
	 * 
	 * @param ctx
	 *            The context of the application
	 * @param handler
	 *            The handler to restart this thread
	 */
	public DataUpdater(final Context ctx, final Handler handler) {
		this.ctx = ctx;
		this.handler = handler;
	}

	@Override
	public void run() {
		try {
			// running updates
			new UpdateDBTask().execute(ctx);
			new UpdateGPSTask().execute(ctx);

			final long lease = Preferences.getInstance(
					ctx.getApplicationContext()).getLong("lease",
					System.currentTimeMillis());
			if (System.currentTimeMillis() > lease) {
				new HandshakeTask().execute(ctx.getApplicationContext());
			} else if (System.currentTimeMillis() + 1800000 > lease) {
				new RefreshIDTask().execute(ctx);
			}

			// restart the update-job
			final int updateFrequenz = Integer.valueOf(Preferences.getInstance(
					ctx).getString("editUpdatePref", "30")) * 1000;
			handler.postDelayed(this, updateFrequenz);
		} catch (Exception ex) {
			Log.e("DataUpdater", ex.getClass().getSimpleName() + "@run(): "
					+ ex.getMessage());
		}
	}
}
