package design.Classes;

import utilities.CheckURL;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import components.DatabaseHandler.AdministrativeDatabase;
import components.DatabaseHandler.LocationAccelerationDatabase;

import config.Variables.Constants;

public class AdminPage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ScrollView currentScrollView = new ScrollView(this);

		final LinearLayout currentLineraLayout = new LinearLayout(this);

		currentLineraLayout.setOrientation(LinearLayout.VERTICAL);

		@SuppressWarnings("deprecation")
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);

		currentScrollView.addView(currentLineraLayout);

		TextView titleText = new TextView(this);
		titleText.setSingleLine(true);

		titleText.setText("Administrative Panel");
		titleText.setPadding(0, 0, 0, 50);
		titleText.setTextSize(20);
		titleText.setGravity(Gravity.CENTER_HORIZONTAL);

		TextView databaseStatusHead = new TextView(this);
		databaseStatusHead.setSingleLine(true);
		databaseStatusHead.setText("Database current status");
		databaseStatusHead.setPadding(0, 0, 0, 20);
		databaseStatusHead.setTextSize(15);
		databaseStatusHead.setGravity(Gravity.LEFT);

		String databaseBodyString="";
		
		try {

			LocationAccelerationDatabase lDB = new LocationAccelerationDatabase(Constants.databaseName, Constants.locationTable, this);
			String allLocs = lDB.getAllLocationsFromDatabase().size()+"";
			String toUploadLocs = lDB.getLocationsForUploadFromDatabase().size()+"";
			
			databaseBodyString = "Locations: "+allLocs+"(total) ----- "+toUploadLocs+"(to upload)";
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			databaseBodyString = "The database has not been initialized yet";
		}
		TextView databaseStatusBody = new TextView(this);
/*		databaseStatusBody.setSingleLine(true);
*/		databaseStatusBody.setText(databaseBodyString);
		databaseStatusBody.setPadding(0, 0, 0, 20);
		databaseStatusBody.setTextSize(15);
		databaseStatusBody.setGravity(Gravity.LEFT);

		TextView connectionUrlHead = new TextView(this);
		connectionUrlHead.setSingleLine(true);
		connectionUrlHead.setText("Connection URL");
		connectionUrlHead.setPadding(0, 0, 0, 20);
		connectionUrlHead.setTextSize(15);
		connectionUrlHead.setGravity(Gravity.LEFT);

		Button testConnection = new Button(this);
		testConnection.setText("Test connection");
		testConnection.setGravity(Gravity.CENTER_HORIZONTAL);

		final EditText urlText = new EditText(this);
		urlText.setInputType(InputType.TYPE_CLASS_TEXT);
		final AdministrativeDatabase adminDb = new AdministrativeDatabase(
				Constants.databaseName, Constants.adminTable,
				getApplicationContext());
		urlText.setText(adminDb.getURL());

		testConnection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					new CheckURL(urlText.getText().toString(),
							getApplicationContext()).execute();
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "invalid url",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		TextView speedTreshHead = new TextView(this);
		speedTreshHead.setSingleLine(true);
		speedTreshHead.setText("Speed Treshold");
		speedTreshHead.setPadding(0, 0, 0, 20);
		speedTreshHead.setTextSize(15);
		speedTreshHead.setGravity(Gravity.LEFT);
		
		final EditText speedText = new EditText(this);
		speedText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		
		speedText.setText(adminDb.getSpeedThresh()+"");
		
		Button saveAndExit = new Button(this);
		saveAndExit.setText("Save and Exit");
		saveAndExit.setGravity(Gravity.CENTER_HORIZONTAL);

		// layoutParams.setMargins(100, 500, 100, 200);

		currentLineraLayout.addView(titleText, layoutParams);
		currentLineraLayout.addView(databaseStatusHead);
		currentLineraLayout.addView(databaseStatusBody);
		currentLineraLayout.addView(connectionUrlHead);
		currentLineraLayout.addView(urlText);
		currentLineraLayout.addView(testConnection, layoutParams);
		currentLineraLayout.addView(speedTreshHead);
		currentLineraLayout.addView(speedText);
		currentLineraLayout.addView(saveAndExit);

		saveAndExit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(AdminPage.this, ServiceHandling.class));
				adminDb.setSpeedTresh(Double.valueOf(speedText.getText().toString()));
				finish();

			}

		});
		this.setContentView(currentScrollView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add("About");

		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent aboutIntent = new Intent(AdminPage.this, AboutPage.class);
				try {
					startActivity(aboutIntent);
				} catch (Exception e) {
					Log.d("EXCEPTION", e.toString());
				}
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

}
