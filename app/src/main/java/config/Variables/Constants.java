package config.Variables;

import android.content.Context;
import android.util.Log;

import se.kth.mobilitycollectorv2.R;

public class Constants {
	public String titleText = null;
	static Context thisCtx;
	
	private static Constants thisInstance = null;
	
	public static final String locationTable = "Location_table";
	public static final String adminTable = "Admin_table";
	public static final String annotationTable = "Annotation_table";
	public static final String simpleLocationTable = "Simple_Location_table";
	public static final String databaseName = "Mobility_collector";
	public static String serviceColumnName = "isServiceOn";
	public static String userIdColumnName = "userId";
	public static String urlColumnName = "urlCurrent";
	public static String speedThresholdColumnName = "speedTresh";
	public static final String servletName = "/ConnectToDatabase";
	

	protected Constants(){
		//Log.d("MY TAG TEST SINGLETON","TEST SINGLETON");
	}
	
	protected Constants(Context ctx ){
		//Log.d("MY TAG NEW CONTEXT","NEW CONTEXT");
		thisCtx = ctx;
		Log.d("MY TAG STRING",loginText);
		titleText=thisCtx.getResources().getString(R.string.app_name);
		thisInstance = new Constants();
	}
	
	public static Constants getInstance(Context ctx){
		if (thisInstance == null) 
			{
			//Log.d("MY TAG NULL","NULL");
			try {
				thisInstance = new Constants(ctx);
				} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			thisCtx = ctx;
			//Log.d("MY TAG CTX NULL",(thisCtx == null)+" ");
			}
		return thisInstance;
	}
	
	/*
	 * General
	 */
	
	
	public  String regularUsernameText = thisCtx.getResources().getString(R.string.regularUsernameText);
	public  String regularPasswordText = thisCtx.getResources().getString(R.string.regularPasswordText);
	public  String registerNewUserText = thisCtx.getResources().getString(R.string.registerNewUserText);
	public  String loginText = thisCtx.getResources().getString(R.string.loginText);
	public  String registerAndLoginButton = thisCtx.getResources().getString(R.string.registerAndLoginButton);
	public String titleControlCenter = thisCtx.getResources().getString(R.string.titleControlCenter);

	/*
	 * For the About Section
	 */
	public String subTitleText = thisCtx.getResources().getString(R.string.subTitleText);
	public String versionText = thisCtx.getResources().getString(R.string.versionText);
	public String contentText = thisCtx.getResources().getString(R.string.contentText);
	public String closeButtonText = thisCtx.getResources().getString(R.string.closeButtonText);

	/*
	 * For the Admin Section
	 */
	public String adminTitleText = thisCtx.getResources().getString(R.string.adminTitleText);
	public String sudoEmailText = thisCtx.getResources().getString(R.string.sudoEmailText);
	public String sudoPasswordText = thisCtx.getResources().getString(R.string.sudoPasswordText);
	public String sudoCredentialsWrongMessage = thisCtx.getResources().getString(R.string.sudoCredentialsWrongMessage);

	/*
	 * Menu
	 */
	public String menuAboutText = thisCtx.getResources().getString(R.string.menuAboutText);
	public String menuAdminText = thisCtx.getResources().getString(R.string.menuAdminText);
	public String menuStatusText = thisCtx.getResources().getString(R.string.menuStatusText);
	public String menuUploadText = thisCtx.getResources().getString(R.string.menuUploadText);

	/*
	 * Dialogs
	 */
	public String yesText = thisCtx.getResources().getString(R.string.yesText);
	public String noText = thisCtx.getResources().getString(R.string.noText);
	public String enableGPSBody = thisCtx.getResources().getString(R.string.enableGPSBody);
	public String enableGPSTitle = thisCtx.getResources().getString(R.string.enableGPSTitle);
	public String serviceDisableBody = thisCtx.getResources().getString(R.string.serviceDisableBody);
	public String serviceDisableTitle = thisCtx.getResources().getString(R.string.serviceDisableTitle);
	public String stopCollectionText = thisCtx.getResources().getString(R.string.stopCollectionText);
	public String startCollectionText = thisCtx.getResources().getString(R.string.startCollectionText);

	/*
	 * Notifications
	 */
	public String notificationTitle = thisCtx.getResources().getString(R.string.notificationTitle);
	public String notificationActionText = thisCtx.getResources().getString(R.string.notificationActionText);
	public String confirmUpload = thisCtx.getResources().getString(R.string.confirmUpload);
	public String infirmUpload = thisCtx.getResources().getString(R.string.infirmUpload);

	/*
	 * Toast warnings
	 */
	public String deactivateGPSWarning = thisCtx.getResources().getString(R.string.deactivateGPSWarning);
	public String internetConnectionWarning = thisCtx.getResources().getString(R.string.internetConnectionWarning);
	public String credentialsMissmatchWarning = thisCtx.getResources().getString(R.string.credentialsMissmatchWarning);
	public String confirmPassword = thisCtx.getResources().getString(R.string.confirmPassword);
	public String passwordError = thisCtx.getResources().getString(R.string.passwordError); // register only
	public String emailWarning = thisCtx.getResources().getString(R.string.emailWarning);

}
