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
package org.traffic.jamdroid.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class to encapsulate a server-request. The client classes can add data and
 * the <code>{@link Requester}</code> sends this data as json-string to the
 * server.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 227 $
 */
public class Request {

	/** The type of the request defined in <code>{@link IConstants}</code> */
	private int type;

	/** The session-id which identifies the client */
	private String session;

	/** The data send to the server */
	private Object data;

	/**
	 * Custom-Constructor of a <code>Request</code> with data
	 * 
	 * @param type
	 *            The type of the request
	 * @param session
	 *            The session-id
	 * @param data
	 *            The data encapsulating the information of the client
	 */
	public Request(int type, String session, Object data) {
		this.type = type;
		this.session = session;
		this.data = data;

	}

	/**
	 * Custom-Constructor of a <code>Request</code> without data
	 * 
	 * @param type
	 *            The type of the request
	 * @param session
	 *            The session-id
	 */
	public Request(int type, String session) {
		this(type, session, null);
	}

	/**
	 * Getter for the type
	 * 
	 * @return The id which identifies the request
	 * @see IConstants
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Adds a <i>value</i> under a given <i>key</i> to the request. The
	 * <i>value</i> could be a <code>{@link Map}</code>, a
	 * <code>{@link List}</code> or an <code>{@link Object}</code> which can be
	 * parsed as json.
	 * 
	 * @param key
	 *            The key in the json-request
	 * @param value
	 *            The data to send
	 * @throws IllegalStateException
	 *             Something went wrong with the data
	 */
	@SuppressWarnings("unchecked")
	public void put(String key, Object value) throws IllegalStateException {
		// checks if main-data is a map
		if (data != null && !(data instanceof Map))
			throw new IllegalStateException();

		if (data == null)
			data = new HashMap<String, Object>();
		Map<String, Object> map = (Map<String, Object>) data;

		// putting a map as jsonobject
		if (value instanceof Map) {
			map.put(key, new JSONObject((Map<String, Object>) value));
			// putting a list as jsonarray
		} else if (value instanceof List) {
			map.put(key, new JSONArray((List<Object>) value));
			// everything else
		} else {
			map.put(key, value);
		}
	}

	/**
	 * Parses the data-object, adds the meta-information and builds a
	 * json-string which can be sent to the server
	 * 
	 * @return A json-string encapsulating the data
	 * @throws IllegalStateException
	 *             Some part of the data could not be parsed
	 */
	@SuppressWarnings("unchecked")
	public String toJson() throws IllegalStateException {
		try {
			// build meta
			String session = this.session == null ? "" : this.session;
			JSONObject meta = new JSONObject();
			meta.put("type", this.type);
			meta.put("id", session);

			// Build Response
			JSONObject ret = new JSONObject();
			ret.put("meta", meta);
			if (this.data instanceof Map)
				ret.put("data", new JSONObject((Map<String, Object>) this.data));
			else if (this.data != null)
				ret.put("data", this.data);
			else
				ret.put("data", JSONObject.NULL);

			// return a string
			return ret.toString();
		} catch (Exception e) {
			throw new IllegalStateException("Error while building request");
		}
	}
}