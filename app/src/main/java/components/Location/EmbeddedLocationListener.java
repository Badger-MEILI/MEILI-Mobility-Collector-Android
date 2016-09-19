package components.Location;

import java.util.LinkedList;

import utilities.GetInfo;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;

import components.DatabaseHandler.AdministrativeDatabase;
import components.DatabaseHandler.LocationAccelerationDatabase;
import components.DatabaseHandler.LocationSimpleDatabase;
import components.PowerSaving.PowerSavingAlarm;

import config.Variables.Constants;
import config.Variables.Variables;

public class EmbeddedLocationListener {

	private SensorManager mSensorManager;
	private SensorEventListener mSensorListener;
	private Sensor mSensor;

	private static LocationManager locationManager;
	private static LocationListener locationListener;
	private static LocationListener secLocationListener;
	private static Location prevLocation;

	private static EmbeddedLocation currentEmbeddedLocation;

	private static Context mContext;
	private static long timeFrequency;
	private static float distanceFrequency;

	private static float[] gravity;
	private static final float ALPHA = 0.8f;
	private static LinkedList<float[]> accelerometerValues;
	private boolean skipOneLocation = false;

	public static PowerManager pm;
	public static PowerManager.WakeLock wl;
	public static PowerManager.WakeLock serviceWakeLock;

	LinkedList<EmbeddedLocation> noiseTestList;
	public static boolean serviceIsStarted = false;

	/*
	 * private static boolean isAccelerometerOn = false; private static boolean
	 * isRunning = false;
	 */

	PowerSavingAlarm powerAlarm;
	LocationAccelerationDatabase locationDatabase;
	LocationSimpleDatabase locationDatabaseSimple;
	AdministrativeDatabase adminDb;
	GetInfo info;
	int userId;
	
	public static boolean isMoving;

	public EmbeddedLocationListener(Context ctx, long timeFreq, float distFreq) {
		mContext = ctx;
		timeFrequency = timeFreq;
		distanceFrequency = distFreq;
		gravity = new float[3];

		locationDatabase = new LocationAccelerationDatabase(
				Constants.databaseName, Constants.locationTable, mContext);
		locationDatabaseSimple = new LocationSimpleDatabase(
				Constants.databaseName, Constants.simpleLocationTable, mContext);
		adminDb = new AdministrativeDatabase(Constants.databaseName,
				Constants.adminTable, mContext);
		
		locationListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {

			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
				Toast.makeText(
						mContext,
						Constants.getInstance(mContext).deactivateGPSWarning,
						Toast.LENGTH_LONG).show();
			}

			@Override
			public void onLocationChanged(Location location) {
				Log.d("MY TAG ", "RECEVIED GPS location");
				releaseServiceWakeLock();
				
				{
					stopSecListening();
					Log.d("MY TAG", "STOPPED SECONDARY");
				}
				
				if (!skipOneLocation)

				{
					// check that the user id is in its correct instance

					if (userId == 0)
						userId = adminDb.getUserId();

					/*
					 * this also resets the accelerometer values
					 */

					if (Variables.powerSaving)
					//	if (powerAlarm!=null)
						powerAlarm.CancelAlarm(mContext, true);

					if (Variables.isAccelerometerEmbedded) {
						stopAccelerometer();

						/*
						 * instance of the ongoing accelerometer values
						 */

						if (getAccelerometerValues().size() != 0)

						/*
						 * the location and embedded accelerometer reading
						 */

						{

							if (isAccurate(location)) {
								currentEmbeddedLocation = new EmbeddedLocation(
										location, new AccelerometerValues(
												getAccelerometerValues()),
										userId, "GPS");

								filterAndInsertIntoDatabase(currentEmbeddedLocation);
							}

							resetAccelerometerValues();
							// feed listener

							/*
							 * start new instance of the accelerometer
							 */

						} else
							locationDatabase
									.insertLocationIntoDb(new EmbeddedLocation(
											location, userId, true, "GPS"));
						startAccelerometer();
					} else {
						if (isAccurate(location))
							locationDatabaseSimple
									.insertLocationIntoDb(new LocationValues(
											location, userId, "GPS"));
					}
					

					if (Variables.equiDistance)
						try {
							tryToAdaptSpeedUsingList(location);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					if (Variables.powerSaving) {
						powerAlarm = new PowerSavingAlarm();
						powerAlarm.SetAlarm(mContext, true);
					}
				} else {
					skipOneLocation = false;
					releaseLock();
				}
			}

			private void filterAndInsertIntoDatabase(EmbeddedLocation loc) {
				if (prevLocation == null) {
					prevLocation = loc.getCurrentLocation().getAsLocation();
					locationDatabase.insertLocationIntoDb(loc);
				} else {
					if (loc.getCurrentLocation()
							.getAsLocation().getTime() - prevLocation.getTime() >= EquidistanceTracking.currentFrequency*1000) {
						locationDatabase.insertLocationIntoDb(loc);
						prevLocation = loc.getCurrentLocation().getAsLocation();
					}
				}
				/*
				 * int difBearing = 0; if (noiseTestList == null) {
				 * noiseTestList = new LinkedList<EmbeddedLocation>();
				 * noiseTestList.add(loc); } else if (loc.currentLocation.time_
				 * - noiseTestList.getLast().getCurrentLocation().time_ != 0) {
				 * if (noiseTestList.size() <= 5) noiseTestList.add(loc); else {
				 * float prevBearing = 400; Location prevLocation = null; for
				 * (EmbeddedLocation l : noiseTestList) if (prevLocation ==
				 * null) prevLocation = l.getCurrentLocation() .getAsLocation();
				 * else { if (prevBearing == 400) prevBearing =
				 * prevLocation.bearingTo(l .getCurrentLocation()
				 * .getAsLocation()); else { if (Math.abs(prevBearing -
				 * prevLocation.bearingTo(l .getCurrentLocation()
				 * .getAsLocation())) > 120) difBearing++;
				 * 
				 * prevBearing = prevLocation.bearingTo(l .getCurrentLocation()
				 * .getAsLocation()); } prevLocation = l.getCurrentLocation()
				 * .getAsLocation(); } //TODO if (difBearing < 3) { for
				 * (EmbeddedLocation e : noiseTestList)
				 * locationDatabase.insertLocationIntoDb(e); Log.d("NOISE TAG ",
				 * "REGULAR INSERT "); } else {
				 * locationDatabase.insertLocationIntoDb(noiseTestList
				 * .getFirst()); Log.d("NOISE TAG", "DELETE ALL "); }
				 * noiseTestList = null; } }
				 */

			}

			private boolean isAccurate(Location location) {
				if (Variables.isAccuracyFilterEnabled) {
					if (location.getAccuracy() <= Variables.accuracyFilterValue)
						return true;
					else
						return false;
				}
				return true;
			}
		};

	
		secLocationListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {

			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
				Toast.makeText(
						mContext,
						Constants.getInstance(mContext).deactivateGPSWarning,
						Toast.LENGTH_LONG).show();
			}

			@Override
			public void onLocationChanged(Location location) {

				Log.d("MY TAG ", "RECEVIED WiFi location");
				
				
				if (!skipOneLocation)

				{
					// check that the user id is in its correct instance

					if (userId == 0)
						userId = adminDb.getUserId();

					/*
					 * this also resets the accelerometer values
					 */

					/*if (Variables.powerSaving)
						try {
							powerAlarm.CancelAlarm(mContext, true);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					*/
					if (Variables.isAccelerometerEmbedded) {
						stopAccelerometer();

						/*
						 * instance of the ongoing accelerometer values
						 */

						if (getAccelerometerValues().size() != 0)

						/*
						 * the location and embedded accelerometer reading
						 */

						{

							/*if (isAccurate(location)) {
								Toast.makeText(mContext, "Received accurate WIFI location", Toast.LENGTH_LONG).show();
							} else 	Toast.makeText(mContext, "Received inaccurate WIFI location", Toast.LENGTH_LONG).show();*/

							currentEmbeddedLocation = new EmbeddedLocation(
									location, new AccelerometerValues(
											getAccelerometerValues()),
											userId, "WiFi");
							filterAndInsertIntoDatabase(currentEmbeddedLocation);
							
							resetAccelerometerValues();

							/*
							 * start new instance of the accelerometer
							 */

						} else
							locationDatabase
									.insertLocationIntoDb(new EmbeddedLocation(
											location, userId, true, "WiFi"));
						startAccelerometer();
					} else {
						if (isAccurate(location))
							locationDatabaseSimple
									.insertLocationIntoDb(new LocationValues(
											location, userId, "WiFi"));
					}
				} else {
					skipOneLocation = false;
					resetAccelerometerValues();
				//	releaseLock();
				}
				
				
			}

			private void filterAndInsertIntoDatabase(EmbeddedLocation loc) {
				if (prevLocation == null) {
					prevLocation = loc.getCurrentLocation().getAsLocation();
					locationDatabase.insertLocationIntoDb(loc);
				} else {
					if (prevLocation.getTime() != loc.getCurrentLocation()
							.getAsLocation().getTime()) {
						locationDatabase.insertLocationIntoDb(loc);
						prevLocation = loc.getCurrentLocation().getAsLocation();
					}
				}
			}

			private boolean isAccurate(Location location) {
				if (Variables.isAccuracyFilterEnabled) {
					if (location.getAccuracy() <= Variables.accuracyFilterValue)
						return true;
					else
						return false;
				}
				return true;
			}
		};
	
		
		mSensorListener = new SensorEventListener() {
			int counter = 0;

			@Override
			public void onAccuracyChanged(Sensor arg0, int arg1) {

			}

			@Override
			public void onSensorChanged(SensorEvent event) {

				float[] values = event.values.clone();
				counter++;

				values = lowPass(values[0], values[1], values[2]);
				if (counter > 10)
					accelerometerValues.add(values);
			}
		};
	}

	public static long getTimeFreq() {
		return timeFrequency;
	}

	public static float getDistanceFreq() {
		return distanceFrequency;
	}

	/**
	 * This calls for the normal launch of the location listener
	 */
	public void startListening() {

		serviceIsStarted = true;
		requestServiceWakeLock();

		Log.d("GPS PROVIDER ON", "ENABLED");

		if (Variables.powerSaving) {
			powerAlarm = new PowerSavingAlarm();
			powerAlarm.SetAlarm(mContext, true);
		}

		// isRunning = true;

		locationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);

		/*
		 * mSensorManager = (SensorManager) mContext
		 * .getSystemService(Context.SENSOR_SERVICE);
		 */

		startAccelerometer();

		locationManager.removeUpdates(locationListener);
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				timeFrequency, Variables.samplingMinDistance,
				locationListener);

		/*
		 * for (EmbeddedLocation lv:
		 * locationDatabase.getAllLocationsFromDatabase()) { Location l = new
		 * Location(""); l.setTime(lv.currentLocation.time_);
		 * l.setLatitude(lv.currentLocation.lat_);
		 * l.setLongitude(lv.currentLocation.lon_);
		 * locationListener.onLocationChanged(l); }
		 */

	}

	private void resetAccelerometerValues() {
		accelerometerValues = new LinkedList<float[]>();
	}

	public void stopListening() {
		try {
			locationManager.removeUpdates(locationListener);
			Log.d("GPS PROVIDER", "DISABLED ");
			//locationManager = null;
			releaseLock();
		} catch (Exception e) {
			e.printStackTrace();
		}

		stopAccelerometer();

	}

	public void stopAlarm() {
		if (Variables.powerSaving) {
			try {
				powerAlarm.CancelAlarm(mContext, true);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				powerAlarm.CancelAlarm(mContext);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void startAccelerometer() {

		// isAccelerometerOn = true;
		emptyAccelerometerValuesGetNewList();

		mSensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(mSensorListener, mSensor,
				getNeededDelay());

	}

	private static int getNeededDelay() {
		// not needed - gets service killed
		/*
		 * if (timeFrequency == 1000) return SensorManager.SENSOR_DELAY_FASTEST;
		 * else if (timeFrequency <= 10000) return
		 * SensorManager.SENSOR_DELAY_GAME; else if (timeFrequency <= 20000)
		 * return SensorManager.SENSOR_DELAY_UI;
		 */
		// TODO Modify this
		return SensorManager.SENSOR_DELAY_NORMAL;
	}

	private void stopAccelerometer() {

		try {
			mSensorManager.unregisterListener(mSensorListener, mSensor);
			Log.d("Accelerometer SENSOR", "DISABLED");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static LinkedList<float[]> getAccelerometerValues() {
		/*
		 * Singleton accelerometer values
		 */

		if (accelerometerValues == null)
			accelerometerValues = new LinkedList<float[]>();
		return accelerometerValues;
	}

	/**
	 * Reset the list's content
	 */
	private static LinkedList<float[]> emptyAccelerometerValuesGetNewList() {

		accelerometerValues = getAccelerometerValues();

		accelerometerValues = new LinkedList<float[]>();

		gravity = new float[3];

		return accelerometerValues;
	}

	/**
	 * Smooth the accelerometer values using a low pass filter
	 */
	private static float[] lowPass(float x, float y, float z) {
		float[] filteredValues = new float[3];

		gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * x;
		gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * y;
		gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * z;

		filteredValues[0] = x - gravity[0];
		filteredValues[1] = y - gravity[1];
		filteredValues[2] = z - gravity[2];

		return filteredValues;
	}

	@SuppressWarnings("static-access")
	private void tryToAdaptSpeedUsingList(Location location) {

		EquidistanceTracking.getInstance().addLocationToList(location);

		final long newFrequency = Math.round(EquidistanceTracking.getInstance()
				.checkForLocationAdjustment());
		if (newFrequency != -1) {

			if (newFrequency > 10000) {

				locationManager.removeUpdates(locationListener);

				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, newFrequency,
						Variables.samplingMinDistance, locationListener);
			} else
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, newFrequency,
						Variables.samplingMinDistance, locationListener);
			//skipOneLocation = true;
			resetAccelerometerValues();
			requestLock();
		timeFrequency = newFrequency;
		}
	}

	private void requestLock() {
		// TODO Auto-generated method stub
		getWakeLockInstance().acquire();
	}

	private WakeLock getWakeLockInstance() {
		if (wl == null) {
			pm = (PowerManager) mContext
					.getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Fix wake lock");
		}
		return wl;
	}

	private WakeLock getServiceWakeLockInstance() {
		if (serviceWakeLock == null) {
			pm = (PowerManager) mContext
					.getSystemService(Context.POWER_SERVICE);
			serviceWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					"Service wake lock");
		}
		return serviceWakeLock;
	}

	private void releaseServiceWakeLock() {
		if (serviceIsStarted) {
			getServiceWakeLockInstance().release();
			serviceIsStarted = false;
		}
	}

	private void requestServiceWakeLock() {
		serviceIsStarted = true;
		getServiceWakeLockInstance().acquire();
	}

	private void releaseLock() {
		try {
			if (getWakeLockInstance().isHeld())
			getWakeLockInstance().release();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void restartForGarbageCollector() {
		// TODO Auto-generated method stub
		Log.d("I was received now", System.currentTimeMillis() + " ");
		stopAlarm();
		System.exit(-1);
	}

	public void startSecListening() {
		
/*		// prevent re-registration
		isMoving = false;*/

		Log.d("WIFI PROVVIDER", "ENABLED WIFI" );
		
		if (locationManager==null)
		locationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);

		startAccelerometer();

		locationManager.removeUpdates(locationListener);
		locationManager.removeUpdates(secLocationListener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				30000, 50,
				secLocationListener);
		
	}
	
	public void stopSecListening() {
		try {
			if (locationManager != null)
			locationManager.removeUpdates(secLocationListener);
			releaseLock();
			Log.d("WiFi PROVIDER", "DISABLED");
		} catch (Exception e) {
			e.printStackTrace();
		}

		stopAccelerometer();

	}

	public static void getMove() {
		// TODO Auto-generated method stub
		if (getAccelerometerValues()!=null)
		{
			Log.d("MY TAG MOVING NULL", "PASSED" );
			if (getAccelerometerValues().size()!=0)
			{
				Log.d("MY TAG MOVING ACC SIZE", "PASSED" );
				AccelerometerValues aV = new AccelerometerValues(getAccelerometerValues());
		isMoving = aV.isTotalIsMoving2();
 		}
			else Log.d("MY TAG MOVING ACC SIZE", "FAILED" );
		} else Log.d("MY TAG MOVING NULL", "FAILED" );
		
	}

}
