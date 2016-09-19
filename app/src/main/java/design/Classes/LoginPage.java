package design.Classes;

import config.Variables.Constants;
import utilities.GetInfo;
import utilities.LoginAsync;
import utilities.UploadAsync;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.text.style.UnderlineSpan;
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



public class LoginPage extends Activity {

	boolean isOnline;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final GetInfo gI = new GetInfo(this);

		isOnline = gI.isOnline();
		
		//gI.closeThisDb();
		
		new UploadAsync(getApplicationContext()).execute();
		/*
		 * 
		 * FOR FIRST LOGIN USE THE ADMIN PAGE TO CHECK IF THE CONNECTION TO THE
		 * SERVER IS SUCCESSFULL
		 */
		
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

		Log.d("MY TAG TEXT", Constants.getInstance(getApplicationContext())+" ");
		
		titleText.setText(Constants.getInstance(getApplicationContext()).titleText+" ");
		titleText.setPadding(0, 0, 0, 50);
		titleText.setTextSize(20);
		titleText.setGravity(Gravity.CENTER_HORIZONTAL);

		TextView emailText = new TextView(this);
		emailText.setSingleLine(true);
		emailText.setText(Constants.getInstance(getApplicationContext()).regularUsernameText);
		emailText.setPadding(0, 0, 0, 20);
		emailText.setTextSize(15);
		emailText.setGravity(Gravity.LEFT);

		TextView passwordText = new TextView(this);
		passwordText.setSingleLine(true);
		passwordText.setText(Constants.getInstance(getApplicationContext()).regularPasswordText);
		passwordText.setPadding(0, 0, 0, 20);
		passwordText.setTextSize(15);
		passwordText.setGravity(Gravity.LEFT);

		TextView registerText = new TextView(this);
		registerText.setSingleLine(true);
		SpannableString mySpannableString = new SpannableString(
				Constants.getInstance(getApplicationContext()).registerNewUserText);
		mySpannableString.setSpan(new UnderlineSpan(), 0,
				mySpannableString.length(), 0);
		registerText.setText(mySpannableString);
		registerText.setPadding(0, 50, 0, 20);
		registerText.setTextSize(20);
		registerText.setTextColor(0x990033CC);
		registerText.setGravity(Gravity.CENTER_HORIZONTAL);

		registerText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginPage.this, RegisterPage.class));
				finish();
			}
		});

		final EditText emailEdit = new EditText(this);
		emailEdit.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);

		final EditText passwordEdit = new EditText(this);
		passwordEdit.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		passwordEdit.setTransformationMethod(PasswordTransformationMethod
				.getInstance());

		Button loginButton = new Button(this);
		loginButton.setText(Constants.getInstance(getApplicationContext()).loginText);
		loginButton.setGravity(Gravity.CENTER_HORIZONTAL);

		// layoutParams.setMargins(100, 500, 100, 200);
		
		TextView contentText = new TextView(this);
		contentText.setText(Constants.getInstance(getApplicationContext()).contentText);
		contentText.setTextSize(15);
		contentText.setGravity(Gravity.LEFT);
		contentText.setPadding(0, 0, 0, 50);

		currentLineraLayout.addView(titleText, layoutParams);
		currentLineraLayout.addView(emailText);
		currentLineraLayout.addView(emailEdit);
		currentLineraLayout.addView(passwordText);
		currentLineraLayout.addView(passwordEdit);
		currentLineraLayout.addView(loginButton, layoutParams);
		currentLineraLayout.addView(registerText);
		currentLineraLayout.addView(contentText);

		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (isOnline) {
					if (completeCredentialsPassed())
						loginComplete();
				} else
					Toast.makeText(
							getApplicationContext(),
							Constants.getInstance(getApplicationContext()).internetConnectionWarning,
							Toast.LENGTH_LONG).show();
			}

			private boolean loginComplete() {
				
				new LoginAsync(LoginPage.this, emailEdit.getText().toString(),
						passwordEdit.getText().toString(),
						getApplicationContext()).execute();
				return true;
			}

			private boolean completeCredentialsPassed() {

				if (!emailEdit.getText().toString().equals(""))
					if (!passwordEdit.getText().toString().equals(""))
						return true;

				Toast.makeText(getApplicationContext(),
						Constants.getInstance(getApplicationContext()).credentialsMissmatchWarning,
						Toast.LENGTH_LONG).show();
				return false;
			}
		});
		this.setContentView(currentScrollView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(Constants.getInstance(getApplicationContext()).menuAboutText);

		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent aboutIntent = new Intent(LoginPage.this, AboutPage.class);
				try {
					startActivity(aboutIntent);
				} catch (Exception e) {
					Log.d("EXCEPTION", e.toString());
				}
				return false;
			}
		});
		
		MenuItem adminItem = menu.add(Constants.getInstance(getApplicationContext()).menuAdminText);

		adminItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent adminIntent = new Intent(LoginPage.this,
						AdminLogin.class);
				try {
					startActivity(adminIntent);
				} catch (Exception e) {
					Log.d("EXCEPTION", e.toString());
				}
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

}
