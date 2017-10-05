package components.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import components.Location.EmbeddedLocationListener;
import config.Variables.Constants;
import config.Variables.Variables;
import design.Classes.ServiceHandling;
import utilities.GetInfo;
import utilities.UploadAsync;

public class CollectingService extends Service {

	public static EmbeddedLocationListener mListener;

	public static boolean secListening = false;
	
	public GetInfo gI;
	private Timer timer;
	private Timer garbageTimer;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		stopListening(this);
		mListener.stopAlarm();
		timer.cancel();
		try {
			garbageTimer.cancel();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gI.setServiceOff();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		gI = new GetInfo(this);

		gI.setServiceOn();

		mListener = new EmbeddedLocationListener(this,
				Variables.samplingMinTime, Variables.samplingMinDistance);

		startListening(this);

		scheduleAlarmForDailyRefresh(this);

		autoUploading(this);

		Intent interactionActivity = new Intent(this, ServiceHandling.class);

		PendingIntent pIntent = PendingIntent.getActivity(this, 0,
				interactionActivity, 0);

		PendingIntent myservicefinal = PendingIntent.getActivity(this, 0,
				interactionActivity, 0);

		Log.d("MY TAG TEXT", Constants.getInstance(getApplicationContext())+" ");

		// TODO Change the icon to a project specific icon
		Notification noti = new NotificationCompat.Builder(this)
				.setContentTitle(Constants.getInstance(getApplicationContext()).notificationTitle)
				.setContentText(Constants.getInstance(getApplicationContext()).titleText)
				.setSmallIcon(se.kth.mobilitycollectorv2.R.drawable.ic_mobility)
				.addAction(se.kth.mobilitycollectorv2.R.drawable.ic_mobility,
						Constants.getInstance(getApplicationContext()).notificationActionText, myservicefinal)
				.setContentIntent(pIntent).build();
		@SuppressWarnings("unused")
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		startForeground(1, noti);

		return START_STICKY;
	}

	private void scheduleAlarmForDailyRefresh(Context ctx) {

		Calendar cur_cal = new GregorianCalendar();
		cur_cal.setTimeInMillis(System.currentTimeMillis());

		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
		cal.set(Calendar.YEAR, cur_cal.get(Calendar.YEAR));
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cur_cal.get(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cur_cal.get(Calendar.MILLISECOND));
		cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE));
		cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));
		cal.add(Calendar.MINUTE, 120);

		garbageTimer = new Timer();
		garbageTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.d("I ran", "at "+System.currentTimeMillis());
				
				mListener.restartForGarbageCollector();
				onStartCommand(null, 0, START_STICKY);
			}
		}, cal.getTime());
		
		Log.d("Set the alarm at ", cal.toString());

	}

	public static void startListening(Context ctx) {
		Log.d("MY TAG","start listening on null " + (mListener==null));
		if (mListener==null)
		mListener = new EmbeddedLocationListener(ctx,
				Variables.samplingMinTime, Variables.samplingMinDistance);
		mListener.startListening();
	}

	
	public static void stopListening(Context ctx) {
		
		Log.d("MY TAG","stop listening on null " + (mListener==null));
		
		if (mListener!=null)
		/*mListener = new EmbeddedLocationListener(ctx,
				Variables.samplingMinTime, Variables.samplingMinDistance);
		//if (mListener!=null)*/
		mListener.stopListening();
	}

	/*public static void stopSecListening(Context ctx) {
		secListening=false;
			mSecListener.stopSecListening();
	}*/
	
	public void autoUploading(final Context ctx) {
		new UploadAsync(ctx).execute();

		timer = new Timer();
		if (Variables.isAutoUpload == 1) {
			timer.schedule(new TimerTask() {
				@Override
				public void run()

				{
					if (gI.isOnline())
						try {
							new UploadAsync(ctx).execute();
						} catch (Exception e) {
						}
				}

			}, 5 * 60 * 1000,
					(long) (60 * 60 * 1000 * Variables.frequencyInHours));
		}

		else {
			timer.cancel();
		}
	}

	public static void startSecondaryListening(Context ctx) {
		if (mListener!=null)
			/*mListener = new EmbeddedLocationListener(ctx,
					Variables.samplingMinTime, Variables.samplingMinDistance);*/
			{
			mListener.stopListening();
			mListener.startSecListening();
			}
	}

}
