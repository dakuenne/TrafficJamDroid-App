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

import java.io.IOException;
import java.net.Socket;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.traffic.jamdroid.model.Preferences;

import android.content.Context;
import android.util.Log;

/**
 * Class to handle the socket-communication with the server
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 227 $
 */
public class Requester {

	/** The only instance of <code>Requester</code> */
	private static Requester requester;

	/** The log-tag */
	private static final String TAG = "Requester";
	/** The context of the application */
	private Context context;

	/**
	 * Custom-Constructor of the <code>Requester</code>. Because of the
	 * singleton-design-pattern the constructor is marked as
	 * <code>private</code>.
	 * 
	 * @param context
	 *            The context of the application
	 */
	private Requester(Context context) {
		this.context = context;
	}

	/**
	 * Sends the <i>request</i> to the server and doesn't wait for a response.
	 * 
	 * @param request
	 *            The complete json-string send to the server
	 */
	public void contactServer(String request) {
		Socket socket = null;

		try {
			socket = new Socket(Preferences.getInstance(context).getString(
					"editServerPref", "<SERVER_IP>"),
					Integer.valueOf(Preferences.getInstance(context).getString(
							"editPortPref", "<SERVER_PORT>")));
			write(socket, request);
		} catch (Exception e) {
			Log.e(TAG, e.getClass() + "@contactServer: " + e.getMessage());
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				Log.e(TAG, "IOException@contactServer " + e.getMessage());
			}
		}
	}

	/**
	 * Sends a <i>request</i> to the server and waits 10 seconds for a response.
	 * If the response doesn't come an empty string is returned.
	 * 
	 * @param request
	 *            The complete json-string send to the server
	 * @return The answer of the server or an empty string
	 */
	public String contactServerForResult(String request) {
		return contactServerForResult(request, 25000);
	}

	/**
	 * Sends a <i>request</i> to the server and waits 10 seconds for a response.
	 * If the response doesn't come an empty string is returned.
	 * 
	 * @param request
	 *            The complete json-string send to the server
	 * @param timeout
	 *            The time to wait for a response in millis
	 * @return The answer of the server or an empty string
	 */
	public String contactServerForResult(String request, int timeout) {
		Socket socket = null;

		try {
			socket = new Socket(Preferences.getInstance(context).getString(
					"editServerPref", "<SERVER_IP>"),
					Integer.valueOf(Preferences.getInstance(context).getString(
							"editPortPref", "<SERVER_PORT>")));
			socket.setSoTimeout(timeout);

			// Sending the request
			write(socket, request);

			// Reading the response
			String response = read(socket);
			return response;
		} catch (Exception e) {
			Log.e(TAG,
					e.getClass() + "@contactServerForResult: " + e.getMessage());
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				Log.e(TAG,
						"IOException@contactServerForResult: " + e.getMessage());
			}
		}
		return "";
	}

	/**
	 * Writes the given <i>text</i> to the <i>sockets</i> outputstream.
	 * 
	 * @param socket
	 *            The socket for the communication
	 * @param text
	 *            The text to send
	 * @throws IOException
	 *             Connection timed out
	 */
	private void write(Socket socket, String text) throws IOException {
		// compressed version
		GZIPOutputStream out = new GZIPOutputStream(socket.getOutputStream());
		out.write(text.getBytes(), 0, text.length());
		out.finish();
		out.flush();
	}

	/**
	 * Reads a <i>sockets</i> inputstream, buffers the complete data and returns
	 * it as string.
	 * 
	 * @param socket
	 *            The socket for the communication
	 * @return The complete data as json-string
	 * @throws IOException
	 *             Connection timed out
	 */
	private String read(Socket socket) throws IOException {
		// compressed version
		GZIPInputStream in = new GZIPInputStream(socket.getInputStream());
		int c;
		StringBuffer res = new StringBuffer();
		while ((c = in.read()) != -1) {
			res.append((char) c);
		}
		return res.toString();
	}

	/**
	 * Returns the single instance of <code>Requester</code>.
	 * 
	 * @param ctx
	 *            The applications context
	 * @return The singleton
	 */
	public static Requester getInstance(Context ctx) {
		if (requester == null) {
			requester = new Requester(ctx);
		}
		return requester;
	}

}