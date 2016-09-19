package design.Classes;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import android.app.Application;

public class Meili extends Application{

	
	public String helloFromGlobalApplication = "foo text";

	private static Application singleton;

	public static Application getInstance() {
	return singleton;
	}

	@Override
	public void onCreate() {
 	super.onCreate();
	singleton = this;
	}

	
}
