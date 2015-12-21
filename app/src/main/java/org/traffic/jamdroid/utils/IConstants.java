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

/**
 * Interface to wrap the constants used in the application.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 227 $
 */
public interface IConstants {

	/** Request type - getting the download-links for the database-files */
	@Deprecated
	public static final int REQUEST_DB = 1;
	/** Request type - sending current location and getting traffic */
	public static final int REQUEST_UPDATE = 2;
	/** Request type - sending a new congestion to the server */
	public static final int REQUEST_SET_CONGESTION = 3;
	/** Request type - getting a new session id */
	public static final int REQUEST_ID = 4;
	/** Request type - acknowledge the session_id */
	public static final int REQUEST_ACK_ID = 5;
	/** Request type - deleting a congestion */
	public static final int REQUEST_DELETE_CONGESTION = 6;
	/** Request type - calculate optimal route */
	public static final int REQUEST_CALCULATE_ROUTE = 7;
	/** Request type - refresh id */
	public static final int REQUEST_REFRESH_ID = 8;
	/** Request type - getting the calculated or refreshed route */
	public static final int REQUEST_GET_ROUTE = 9;
	/** Request type - deletes the current route */
	public static final int REQUEST_DELETE_ROUTE = 10;
	/** Request type - sends all known problems */
	public static final int REQUEST_GET_PROBLEMS = 11;

	/** Path to the databases saved on the phone */
	public static final String INTERN_DB_PATH = "/data/data/org.traffic.jamdroid/databases/";
	/** Path to the databases saved on the sdcard */
	public static final String EXTERN_DB_PATH = "/sdcard/TrafficJamDroid/db/";
	/** Path to the online-help-files */
	public static final String ONLINE_HELP_PATH = "<FILESERVER>/help/index.php";
	/** Path to the server with the database-files */
	public static final String DB_DOWNLOAD_PATH = "<FILESERVER>/databases/";

	/** Congestion - indicating traffic jam */
	public static final int CONGESTION_JAM = 0;
	/** Congestion - indication a crash */
	public static final int CONGESTION_CRASH = 1;
	/** Congestion - indicating that the road is under construction */
	public static final int CONGESTION_CONSTRUCTION = 2;
	/** Congestion - indicating ice on the road */
	public static final int CONGESTION_ICE = 3;
	/** Congestion - indicating an event on the road */
	public static final int CONGESTION_EVENT = 4;
	/** Congestion - general problem */
	public static final int CONGESTION_GENERAL = 5;
	
	/** indicates that the remote data is very good */
	public static final int QUALITY_UP_TO_DATE = 0;
	/** indicates that the remote data is interpolated over the same days - for example: all Tuesdays */
	public static final int QUALITY_DAY = 1;
	/** indicates that the remote data is interpolated over all information for this road */
	public static final int QUALITY_ALL = 2;

}
