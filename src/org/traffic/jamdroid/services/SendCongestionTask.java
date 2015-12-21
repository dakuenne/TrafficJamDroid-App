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

import org.traffic.jamdroid.model.Preferences;
import org.traffic.jamdroid.services.SendCongestionTask.CongestionWrapper;
import org.traffic.jamdroid.utils.IConstants;
import org.traffic.jamdroid.utils.Request;
import org.traffic.jamdroid.utils.Requester;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

/**
 * Task to send a request for the creation or deletion of a congestion to the
 * server.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 226 $
 */
public class SendCongestionTask extends
		AsyncTask<CongestionWrapper, Void, Void> {

	@Override
	protected Void doInBackground(final CongestionWrapper... args) {
		Request r = new Request(args[0].getRequestType(), Preferences
				.getInstance(args[0].getContext()).getString("session", null));
		if (args[0].getRequestType() == IConstants.REQUEST_SET_CONGESTION) {
			r.put("lat", args[0].getLocation().getLatitude());
			r.put("lon", args[0].getLocation().getLongitude());
			r.put("type", args[0].getType());
		} else {
			r.put("id", args[0].getId());
		}
		r.put("time", System.currentTimeMillis());
		Requester req = Requester.getInstance(args[0].getContext());
		req.contactServer(r.toJson());
		return null;
	}

	/**
	 * Wrapper-class for the data to send.
	 * 
	 * @author Daniel Kuenne
	 * @version $LastChangedRevision: 226 $
	 */
	public static class CongestionWrapper {

		/** The location of a new congestion */
		private Location location;
		/** The type of a new congestion */
		private int type;
		/** The id of an existing congestion */
		private int id;
		/** The context of the application */
		private Context ctx;

		/** The type of the request */
		private int request = IConstants.REQUEST_SET_CONGESTION;

		/**
		 * Custom-Constructor for a delete-request.
		 * 
		 * @param id
		 *            The id of the congestion
		 * @param ctx
		 *            The context of the application
		 * @param isSet
		 *            The status of the congestion
		 */
		public CongestionWrapper(final int id, final Context ctx,
				final boolean isSet) {
			this.id = id;
			this.ctx = ctx;
			if (!isSet) {
				request = IConstants.REQUEST_DELETE_CONGESTION;
			}
		}

		/**
		 * Custom-Constructor for a create-request.
		 * 
		 * @param location
		 *            The location
		 * @param type
		 *            The type
		 * @param ctx
		 *            The context of the application
		 * @param isSet
		 *            The status of the congestion
		 */
		public CongestionWrapper(final Location location, final int type,
				final Context ctx, final boolean isSet) {
			this.location = location;
			this.type = type;
			this.ctx = ctx;
			if (!isSet) {
				request = IConstants.REQUEST_DELETE_CONGESTION;
			}
		}

		/**
		 * Returns the location.
		 * 
		 * @return Th location
		 */
		public Location getLocation() {
			return location;
		}

		/**
		 * Returns the type.
		 * 
		 * @return The type
		 */
		public int getType() {
			return type;
		}

		/**
		 * Returns the id.
		 * 
		 * @return The id
		 */
		public int getId() {
			return id;
		}

		/**
		 * Returns the context.
		 * 
		 * @return The context
		 */
		public Context getContext() {
			return ctx;
		}

		/**
		 * Returns the request-type.
		 * 
		 * @return The type
		 */
		public int getRequestType() {
			return request;
		}
	}

}
