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

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.osmdroid.util.GeoPoint;
import org.traffic.jamdroid.model.LocalDBMonitor;
import org.traffic.jamdroid.utils.IConstants;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class contains several functions for the work with the database-files.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 225 $
 * @see SQLiteDatabase
 */
public class DBWrapper {

	/** The debug-tag */
	private static final String TAG = "DBWrapper";

	/** Sql to search the linestring for an id */
	private static final String SELECT_SQL = "SELECT way FROM roadstrips WHERE id = ?";

	/** Sql to search the minimal and maximal id */
	private static final String MINMAX_SQL = "SELECT MIN(id) AS min, MAX(id) AS max FROM roadstrips";

	/** The one and only instance */
	private static DBWrapper instance = new DBWrapper();

	/** A list of all databases */
	private final Map<Borders, File> databases;

	/**
	 * Default-Constructor
	 */
	private DBWrapper() {
		databases = new HashMap<Borders, File>();
		updateDBs();
	}

	/**
	 * Fetches the points of a street from the correct database-file.
	 * 
	 * @param id
	 *            The id of the street
	 * @return A list of all points
	 */
	public List<GeoPoint> fetchPoints(final int id) {
		updateDBs();
		SQLiteDatabase db = null;
		Cursor c = null;

		try {
			// searching the database-file
			final Borders search = new Borders(id, id);
			if (databases.get(search) != null) {
				db = SQLiteDatabase.openOrCreateDatabase(databases.get(search),
						null);
				if (db != null) {
					c = db.rawQuery(SELECT_SQL, new String[] { "" + id });
					if (c != null) {
						c.moveToFirst();
						// extracting the points
						String linestring = c
								.getString(c.getColumnIndex("way"));
						return extractPoints(linestring);
					}
				}
			}
		} catch (Exception ex) {
			Log.e(TAG,
					ex.getClass().getSimpleName() + "@fetchPoints: "
							+ ex.getMessage());
		} finally {
			if (c != null) {
				c.close();
			}
			if (db != null) {
				db.close();
			}
		}
		return new LinkedList<GeoPoint>();
	}

	/**
	 * Extracts the points from the given string.
	 * 
	 * @param linestring
	 *            The linestring with the data
	 * @return A list of all points
	 */
	private List<GeoPoint> extractPoints(String linestring) {
		try {
			final List<GeoPoint> points = new LinkedList<GeoPoint>();
			linestring = linestring.substring(12, linestring.length() - 1);
			String[] strPoints = linestring.split(",");
			for (String text : strPoints) {
				String[] pos = text.trim().split(" ");
				points.add(new GeoPoint(Double.parseDouble(pos[1]), Double
						.parseDouble(pos[0])));
			}
			return points;
		} catch (Exception ex) {
			Log.e(TAG, ex.getClass() + "@extractPoints: " + ex.getMessage());
			return new LinkedList<GeoPoint>();
		}
	}

	/**
	 * Loads a database-file and searches the range of the entries.
	 * 
	 * @param database
	 *            The database-file
	 */
	private void getDBInfo(final File database) {
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			db = SQLiteDatabase.openOrCreateDatabase(database, null);
			c = db.rawQuery(MINMAX_SQL, null);
			if (c != null && c.getCount() > 0) {
				c.moveToFirst();
				final int min = c.getInt(c.getColumnIndex("min"));
				final int max = c.getInt(c.getColumnIndex("max"));
				databases.put(new Borders(min, max), database);
			}
		} catch (Exception ex) {
			Log.e(TAG,
					ex.getClass().getSimpleName() + "@getDBInfo: "
							+ ex.getMessage());
		} finally {
			if (c != null) {
				c.close();
			}
			if (db != null) {
				db.close();
			}
		}
	}

	/**
	 * Searches for local database-files and adds them to the list.
	 */
	private void updateDBs() {
		// searching local databases
		File checkPath = new File(IConstants.INTERN_DB_PATH);
		if (checkPath.exists()) {
			for (File f : checkPath.listFiles()) {
				if (f.getName().endsWith(".db") && !databases.containsValue(f)
						&& LocalDBMonitor.getLocaleDBs().contains(f.getName())) {
					getDBInfo(f);
				}
			}
		}

		// searching databases on the sdcard
		checkPath = new File(IConstants.EXTERN_DB_PATH);
		if (checkPath.exists()) {
			for (File f : checkPath.listFiles()) {
				if (f.getName().endsWith(".db") && !databases.containsValue(f)
						&& LocalDBMonitor.getLocaleDBs().contains(f.getName())) {
					getDBInfo(f);
				}
			}
		}
	}

	/**
	 * Provides the one and only instance of this class.
	 * 
	 * @return The singleton
	 */
	public static DBWrapper getInstance() {
		return instance;
	}

}
