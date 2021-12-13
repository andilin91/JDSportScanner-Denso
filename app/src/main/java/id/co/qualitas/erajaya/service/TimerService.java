package id.co.qualitas.erajaya.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import id.co.qualitas.erajaya.constants.Constants;
import id.co.qualitas.erajaya.session.SessionManager;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TimerService extends Service {

	public static String str_receiver = "id.co.qualitas.barcodepp.service.receiver";
	private Handler myhandler = new Handler();

	private int intCounter = 0;

	private boolean isTimerStart;

	private long timeElapsed = 0;
	private long delayedTime = System.currentTimeMillis();
	Intent intent;

	private Runnable myTasks = new Runnable() {

		public void run() {
			// TODO Auto-generated method stub
			saveData();
			myhandler.postDelayed(myTasks, 1000);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void saveData() {
		// TODO Auto-generated method stub
		SessionManager session = new SessionManager(this);
		Map<String, String> dataSession = session.getPEDetails();
		String mData = dataSession.get(Constants.KEY_PE);
//		PWOEmployee pwoEmployee = (PWOEmployee) Helper
//				.stringToObject(mData);
//		if (pwoEmployee.getDelaytime() != 0
//				&& pwoEmployee.getDelaytime() != 0) {
//			delayedTime = pwoEmployee.getDelaytime();
//			timeElapsed = pwoEmployee.getDuration();
//		}
//		timeElapsed += (System.currentTimeMillis() - delayedTime);
//		// txtTimer.setText(convertHmsTimer(timeElapsed));
//		pwoEmployee.setDelaytime(System.currentTimeMillis());
//		pwoEmployee.setDuration(timeElapsed);
//		// delayedTime = System.currentTimeMillis();
//		String str_testing = convertHmsTimer(timeElapsed);
//		new SessionManager(getApplicationContext()).createPESession(Helper
//				.objectToString(pwoEmployee));
//		Log.e("TIME", str_testing);
//
//		fn_update(str_testing);
	}

	public static String convertHmsTimer(Long millis) {
		return String.format(
				Locale.getDefault(),
				"%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						% TimeUnit.HOURS.toMinutes(1),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						% TimeUnit.MINUTES.toSeconds(1));
	}

	// @Override
	// public void onStart(Intent intent, int startId) {
	// myhandler.postDelayed(myTasks, 1000);
	// intent = new Intent(str_receiver);
	// super.onStart(intent, startId);
	// // Log.i("Start Service", "onStart");
	// }

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread() {
			public void run() {
				myhandler.postDelayed(myTasks, 1000);
			}
		}.start();
		return START_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// myhandler.postDelayed(myTasks, 1000);
		intent = new Intent(str_receiver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		myhandler.removeCallbacks(myTasks);
		Intent i = new Intent(getApplicationContext(), TimerService.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		stopService(i);
		Log.e("Service finish", "Finish");
	}

	private void fn_update(String str_time) {

		intent.putExtra("time", str_time);
		sendBroadcast(intent);
	}

}
