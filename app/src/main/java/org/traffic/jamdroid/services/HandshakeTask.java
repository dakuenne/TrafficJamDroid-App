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

import org.json.JSONException;
import org.json.JSONObject;
import org.traffic.jamdroid.model.Preferences;
import org.traffic.jamdroid.utils.IConstants;
import org.traffic.jamdroid.utils.Request;
import org.traffic.jamdroid.utils.Requester;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.util.Log;

/**
 * Task to perform a <i>Three-Way-Handshake</i> with the server.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 226 $
 */
public class HandshakeTask extends AsyncTask<Context, Void, Void> {

	/** The debug-tag */
	private static final String TAG = "HandshakeTask";

	@Override
	protected Void doInBackground(Context... params) {
		try {
			// requesting a new id for this session from the server
			final Request reqId = new Request(IConstants.REQUEST_ID, null);
			reqId.put("device", Secure.getString(
					params[0].getContentResolver(), Secure.ANDROID_ID));
			Requester req = Requester.getInstance(params[0]);
			final String response = req.contactServerForResult(reqId.toJson());

			// saving the id in the preferences for all future requests
			final JSONObject json = new JSONObject(response);
			final String id = json.getString("id");
			Preferences.getEditorInstance(params[0]).putString("session", id)
					.commit();
			final Long lease = json.getLong("lease");
			Preferences.getEditorInstance(params[0]).putLong("lease", lease)
					.commit();

			// acknowledge the id
			final Request reqAckId = new Request(IConstants.REQUEST_ACK_ID, id);
			req.contactServer(reqAckId.toJson());

		} catch (JSONException ex) {
			Log.e(TAG, "JSONException@doInBackground: " + ex.getMessage());
		}
		return null;
	}

}
