package utilities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;

import components.DatabaseHandler.AdministrativeDatabase;

import config.Variables.Constants;
import config.Variables.Variables;
import design.Classes.ServiceHandling;

public class RegisterAsync extends AsyncTask<Void, Void, String> {

	URL url = null;
	Context mContext;
	String userName;
	String passWord;
    String phoneModel;
    String sdkInt;

	Activity referencingActivity;

	public RegisterAsync(Activity ref, String username, String password,
			Context ctx, String phoneModel, String sdkInt) {
		this.referencingActivity = ref;
		mContext = ctx;
		userName = username;
		passWord = password;
        this.phoneModel = phoneModel;
        this.sdkInt = sdkInt;
	}

	protected String doInBackground(Void... params) {
		URL url = null;
		try {
			url = new URL((Variables.urlConnection + Variables.userRegisterEndpoint));
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
			dop.writeBytes("method=" + URLEncoder.encode("register", "utf-8"));
			// it is essential that to add "&" to separate two strings
			dop.writeBytes("&username=" + URLEncoder.encode(userName, "utf-8"));
			dop.writeBytes("&password=" + URLEncoder.encode(passWord, "utf-8"));
            dop.writeBytes("&phone_model=" + URLEncoder.encode(phoneModel, "utf-8"));
            dop.writeBytes("&phone_os=" + URLEncoder.encode(sdkInt, "utf-8"));
			dop.flush();
			dop.close();
			DataInputStream dis = new DataInputStream(urlConn.getInputStream());
            Log.d("My tag", dis.toString());
			String locPassage = dis.readLine();
            Log.d("My tag", locPassage);
			dis.close();
			// to disconnect the server side
			urlConn.disconnect();


            if (locPassage.equalsIgnoreCase("username taken")) return "Failed";

            try {
                JSONArray jArray = new JSONArray(locPassage);
                return jArray.getJSONObject(0).getInt("id")+"";
            }
            catch (Exception e){
                return "Failed";
            }


		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Failed";

	}

	protected void onPostExecute(String result) {
		if (result.equals("Failed")) {
			Toast.makeText(mContext, "Username is taken", Toast.LENGTH_LONG)
					.show();
		} else {
			
			Log.d("MY REGISTER TAG", result);
			Toast.makeText(mContext, "Successful registration",
					Toast.LENGTH_LONG).show();

			AdministrativeDatabase adminDb = new AdministrativeDatabase(
					Constants.databaseName, Constants.adminTable, mContext);

			adminDb.updateUserId(Integer.valueOf(result.replace(" ", "")));

			this.referencingActivity.startActivity(new Intent(
					this.referencingActivity.getApplicationContext(),
					ServiceHandling.class));
		}
		super.onPostExecute(result);
	}

}
