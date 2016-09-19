package utilities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import components.DatabaseHandler.CentralDatabase;

import config.Variables.Constants;
import config.Variables.Variables;

public class UploadAsync extends AsyncTask<Void, Void, Boolean> {

	URL url = null;
	Context mContext;
	CentralDatabase centralDB;
	boolean showToast = false;

	public UploadAsync(Context ctx) {
		mContext = ctx;
		centralDB = new CentralDatabase(mContext);
		showToast = false;
	}

	public UploadAsync(Context ctx, boolean bool) {
		mContext = ctx;
		centralDB = new CentralDatabase(mContext);
		showToast = true;
	}

	protected Boolean doInBackground(Void... params) {
		URL url = null;
		String upload = centralDB.getUploadStatement();
		Log.d("MY TAG UPLOAD ", upload);

				if (!upload
				.equalsIgnoreCase("method=upload&embeddedLocations_=[]&simpleLocations_=")) {
			try {
				url = new URL((Variables.urlConnection+Variables.locationUploadEndpoint));
				HttpURLConnection urlConn = (HttpURLConnection) url
						.openConnection();

				urlConn.setDoInput(true);
				urlConn.setDoOutput(true);
				urlConn.setRequestMethod("POST");
				urlConn.setUseCaches(false);
				urlConn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				urlConn.setRequestProperty("Charest", "utf-8");
				// to connect to the server side
				urlConn.connect();

				DataOutputStream dop = new DataOutputStream(
						urlConn.getOutputStream());


				dop.writeBytes(upload);
				dop.flush();
				dop.close();
				DataInputStream dis = new DataInputStream(
						urlConn.getInputStream());
				String locPassage = dis.readLine();

                Log.d("MY tag async", locPassage);
				dis.close();

				if ("OK".equalsIgnoreCase(locPassage)) {
					return true;
				} else {
					return false;
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
                return false;
			} catch (IOException e) {
				e.printStackTrace();
                return false;
			}
		}  
		return true;

	}

	protected void onPostExecute(Boolean result) {
		Log.d("MY TAG TEXT smth", Constants.getInstance(mContext) + " "
				+ showToast);

		if (result) {
			if (showToast)
				Toast.makeText(mContext,
						Constants.getInstance(mContext).confirmUpload,
						Toast.LENGTH_LONG).show();
			centralDB.setUploadToTrue();
		} else if (showToast)
			Toast.makeText(mContext,
					Constants.getInstance(mContext).infirmUpload,
					Toast.LENGTH_LONG).show();
		super.onPostExecute(result);
	}

}
