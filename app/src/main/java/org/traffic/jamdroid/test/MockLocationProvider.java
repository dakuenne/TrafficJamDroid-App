package org.traffic.jamdroid.test;

import java.io.IOException;
import java.util.List;

import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;

public class MockLocationProvider extends Thread {

	private List<String> data;

	private LocationManager locationManager;

	private String mocLocationProvider;

	public MockLocationProvider(LocationManager locationManager,
			String mocLocationProvider, List<String> data) throws IOException {

		this.locationManager = locationManager;
		this.mocLocationProvider = mocLocationProvider;
		this.data = data;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
		}
		for (int i = 0; i < this.data.size() -1 ; i++) {
			String str = this.data.get(i);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			
			String[] nextparts = this.data.get(i+1).split(";");
			Location next = new Location(mocLocationProvider);
			next.setLatitude(Double.valueOf(nextparts[2]));
			next.setLongitude(Double.valueOf(nextparts[1]));
			
			// Set one position
			String[] parts = str.split(";");
			Double latitude = Double.valueOf(parts[2]);
			Double longitude = Double.valueOf(parts[1]);
			Double altitude = 0.0;
			Location location = new Location(mocLocationProvider);
			location.setLatitude(latitude);
			location.setLongitude(longitude);
			location.setAltitude(altitude);
			location.setBearing(location.bearingTo(next));
			location.setTime(Long.valueOf(parts[0]));
			location.setAccuracy(10.0f);
			if (Build.VERSION.SDK_INT >= 26) {
				location.setVerticalAccuracyMeters(0.5f);
				location.setSpeedAccuracyMetersPerSecond(2.0f);
				location.setBearingAccuracyDegrees(2.5f);
				location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
			}

			if (location.getLatitude() != next.getLatitude() && location.distanceTo(next) > 10)
				locationManager.setTestProviderLocation(mocLocationProvider,
					location);
		}
	}
}
