package components.Location;

import android.location.Location;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;

public class LocationValues {

	public long time_;
	public int user_id;
	public double lat_;
	public double lon_;
	public double speed_;
	public double altitude_;
	public double bearing_;
	public double accuracy_;
	public int satellites_;
	public String provider;

	public LocationValues(int user_id, double lat_, double lon_, double speed_,
			double altitude_, double bearing_, double accuracy_, int satellites_, long time_, String provider) {
		super();
		this.user_id = user_id;
		this.lat_ = lat_;
		this.lon_ = lon_;
		this.speed_ = speed_;
		this.altitude_ = altitude_;
		this.bearing_ = bearing_;
		this.accuracy_ = accuracy_;
		this.satellites_ = satellites_;
		this.time_ = time_;
		this.provider = provider;
	}

	public LocationValues(Location l, int userid, int sats, String provider) {
		// TODO Auto-generated constructor stub
		time_ = l.getTime();
		user_id = userid;
		lat_ = l.getLatitude();
		lon_ = l.getLongitude();
		speed_ = l.getSpeed();
		altitude_ = l.getAltitude();
		bearing_ = l.getBearing();
		accuracy_ = l.getAccuracy();
		satellites_ = sats;
		this.provider = provider;
	}

	public LocationValues(Location l, int userid, String provider) {
		// TODO Auto-generated constructor stub
		time_ = l.getTime();
		user_id = userid;
		lat_ = l.getLatitude();
		lon_ = l.getLongitude();
		speed_ = l.getSpeed();
		altitude_ = l.getAltitude();
		bearing_ = l.getBearing();
		accuracy_ = l.getAccuracy();
		satellites_ = 0;
		this.provider = provider;
	}

	public static LinkedHashMap<String, String> getAllElements() {
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();
		
		for (Field f : LocationValues.class.getDeclaredFields()) {
			if (!f.getName().contains("serialVersionUID"))
			hashMap.put(f.getName(), utilities.GetInfo.convertTypeJavaToSql(f.getType().getName()));
		}
		return hashMap;
	}

	public Location getAsLocation() {
		Location thisLocation = new Location("");
		thisLocation.setTime(time_);
		thisLocation.setLatitude(lat_);
		thisLocation.setLongitude(lon_);
		thisLocation.setSpeed((float) speed_);
		thisLocation.setAltitude(altitude_);
		thisLocation.setBearing((float) bearing_);
		thisLocation.setAccuracy((float) accuracy_);
		return thisLocation;
	}
}
