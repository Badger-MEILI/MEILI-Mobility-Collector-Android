package components.Location;

import android.location.Location;
import android.util.Log;

public class EmbeddedLocation {
	LocationValues currentLocation;
	AccelerometerValues currentAcc;

	public EmbeddedLocation(Location l, int userID, String provider) {
		this.currentLocation = new LocationValues(l, userID, provider);
	}

	public EmbeddedLocation(Location l, AccelerometerValues a, int userID, String provider) {
		Log.d("user id", userID + " ");
		this.currentLocation = new LocationValues(l, userID, provider);
		this.currentAcc = a;
	}

	public EmbeddedLocation(LocationValues lV, AccelerometerValues a) {
		this.currentLocation = lV;
		this.currentAcc = a;
	}

	public EmbeddedLocation(Location location, int userId, boolean b, String provider) {
		// TODO Auto-generated constructor stub
		this.currentLocation = new LocationValues(location, userId, provider);
		this.currentAcc = AccelerometerValues.getBlankAcc();
	}

	public LocationValues getCurrentLocation() {
		return currentLocation;
	}

	public AccelerometerValues getCurrentAcc() {
		return currentAcc;
	}

}
