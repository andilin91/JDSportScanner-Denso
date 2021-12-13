package id.co.qualitas.erajaya.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import id.co.qualitas.erajaya.BuildConfig;
import id.co.qualitas.erajaya.R;
import id.co.qualitas.erajaya.constants.Constants;
import id.co.qualitas.erajaya.helper.Helper;
import id.co.qualitas.erajaya.model.UserResponse;
import id.co.qualitas.erajaya.session.SessionManager;

import java.util.Map;

public class SplashScreenActivity extends Activity {
	private SessionManager session;
	private ProgressDialog progress;
	private ActionBar ab;
	private TextView txtVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
//		getActionBar().hide();
		session = new SessionManager(getApplicationContext());
		txtVersion = findViewById(R.id.txtVersion);
		String versionName = BuildConfig.VERSION_NAME;
		txtVersion.setText("JDSport-EAN Version " + versionName);
		if (session.isUrlEmpty()) {
			Map<String, String> urlSession = session.getUrl();
			Helper.setItemParam(Constants.URL,
					urlSession.get(Constants.KEY_URL));
		} else {
			Helper.setItemParam(Constants.URL, Constants.URL);
		}
		new CountDownTimer(Constants.LONG_1000, Constants.LONG_100) {

			public void onTick(long millisUntilFinished) {

			}

			public void onFinish() {
				if (session.isDataIn()) {
					Map<String, String> dataSession = session.getDataDetails();
					String mData = dataSession.get(Constants.KEY_DATA);
					UserResponse userResponse = (UserResponse) Helper
							.stringToObject(mData);
					Helper.setItemParam(Constants.USER_DETAIL, userResponse);
					Map<String, String> dataSession2 = session.getToken();
					String mData2 = dataSession2.get(Constants.KEY_TOKEN);
					Helper.setItemParam(Constants.TOKEN, mData2);
					Intent intent = new Intent(getApplicationContext(),
							MenuActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}else{

					Intent intent = new Intent(getApplicationContext(),
							LoginActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
			}
		}.start();
	}
	
}
