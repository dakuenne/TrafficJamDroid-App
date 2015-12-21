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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Class to handle the the work with the {@link SharedPreferences}.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 225 $
 */
public class Preferences {

	/** The one and only editor */
	private static Editor editor = null;
	/** The one and only preferences */
	private static SharedPreferences preferences = null;

	/**
	 * Returns a singleton to read the preferences.
	 * 
	 * @param ctx
	 *            The context of the application
	 * @return The singleton
	 */
	public static SharedPreferences getInstance(final Context ctx) {
		if (preferences == null) {
			preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		}
		return preferences;
	}

	/**
	 * Returns a singleton to edit the preferences.
	 * 
	 * @param ctx
	 *            The context of the application
	 * @return The singleton
	 */
	public static Editor getEditorInstance(final Context ctx) {
		if (editor == null) {
			editor = getInstance(ctx).edit();
		}
		return editor;
	}
}
