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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.traffic.jamdroid.utils.IConstants;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * Class to handle the local database-files.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 225 $
 */
public class LocalDBMonitor {

	/** The debug-tag */
	private static final String TAG = "LocalDBMonitor";

	/** The format of the filenames */
	private static final DecimalFormat fourBitFormat = new java.text.DecimalFormat(
			"0000");

	/** List of all databases found on the system */
	private static final List<String> localDBs = new LinkedList<String>();
	/** List of all files which are currently downloaded */
	private static final List<String> downloadingDBs = new LinkedList<String>();

	/** SDCard-status */
	private static boolean externalStorageWriteable;

	/**
	 * Checks if all needed databases exists and downloads the missing files.
	 * 
	 * @param minLat
	 *            The minimal latitude
	 * @param maxLat
	 *            The maximal latitude
	 * @param minLng
	 *            The minimal longitude
	 * @param maxLng
	 *            The maximal longitude
	 * @param ctx
	 *            The applications context
	 */
	public static void checkAndGetDBs(final int minLat, final int maxLat,
			final int minLng, final int maxLng, final Context ctx) {
		checkExternalStorage();
		checkDBs(minLat, maxLat, minLng, maxLng, ctx);
		if (downloadingDBs.size() > 0) {
			downloadDBs();
		}
	}

	/**
	 * Returns a list of all local database-files.
	 * 
	 * @return The list
	 */
	public static List<String> getLocaleDBs() {
		return localDBs;
	}

	/**
	 * Checks whether a file is already downloading.
	 * 
	 * @param dbname
	 *            The name of the database
	 * @return Downloading or not
	 */
	private static boolean isDownloading(final String dbname) {
		return downloadingDBs.contains(dbname);
	}

	/**
	 * Checks if the needed file is already on the client.
	 * 
	 * @param minLat
	 *            The minimal latitude
	 * @param maxLat
	 *            The maximal latitude
	 * @param minLng
	 *            The minimal longitude
	 * @param maxLng
	 *            The maximal longitude
	 * @param ctx
	 *            The context of the application
	 */
	private static void checkDBs(final int minLat, final int maxLat,
			final int minLng, final int maxLng, final Context ctx) {
		// finding all existing databases downloaded by previous sessions
		final File checkPath;
		if (externalStorageWriteable) {
			checkPath = new File(IConstants.EXTERN_DB_PATH);
		} else {
			checkPath = new File(IConstants.INTERN_DB_PATH);
		}
		for (File f : checkPath.listFiles()) {
			if (f.getName().endsWith(".db") && !existsDB(f.getName())) {
				localDBs.add(f.getName());
			}
		}

		// searching for the needed databases and register them for a download
		// if necessary
		for (double i = minLat; i <= maxLat; i += 0.5) {
			for (double j = minLng; j <= maxLng; j += 0.5) {
				final String db = fourBitFormat.format(minLng * 100)
						+ fourBitFormat.format(minLat * 100) + ".db";
				if (!existsDB(db) && !isDownloading(db)) {
					downloadingDBs.add(db);
				}
			}
		}
	}

	/**
	 * Downloads all needed files.
	 */
	private static void downloadDBs() {
		try {
			while (!downloadingDBs.isEmpty()) {
				final String dbname = downloadingDBs.get(0);
				downloadingDBs.remove(0);
				if (!existsDB(dbname) && !isDownloading(dbname)) {
					final URL uri = new URL(IConstants.DB_DOWNLOAD_PATH
							+ dbname + ".gz");

					final URLConnection ucon = uri.openConnection();
					final GZIPInputStream gis = new GZIPInputStream(
							new BufferedInputStream(ucon.getInputStream()));
					final File database;
					if (externalStorageWriteable) {
						database = new File(IConstants.EXTERN_DB_PATH + dbname);
					} else {
						database = new File(IConstants.INTERN_DB_PATH + dbname);
					}
					if (!database.exists()) {
						if (!database.getParentFile().exists()) {
							database.getParentFile().mkdirs();
						}
						database.createNewFile();
					}
					final FileOutputStream fos = new FileOutputStream(database);
					int current = 0;
					byte data[] = new byte[1024];

					while ((current = gis.read(data)) != -1) {
						fos.write(data, 0, current);
					}
					fos.close();
					gis.close();
					localDBs.add(dbname);
					Log.d(TAG, dbname + " is existant");
				}
			}
		} catch (Exception e) {
			Log.e(TAG,
					e.getClass().getSimpleName() + "@downloadDB: "
							+ e.getMessage());
		}
	}

	/**
	 * Checks if a file is already on the device.
	 * 
	 * @param dbname
	 *            The name of the database-file
	 * @return The status
	 */
	private static boolean existsDB(final String dbname) {
		return localDBs.contains(dbname);
	}

	/**
	 * Checks the status of the external storage
	 */
	private static void checkExternalStorage() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			externalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			externalStorageWriteable = false;
		} else {
			externalStorageWriteable = false;
		}
	}

}
