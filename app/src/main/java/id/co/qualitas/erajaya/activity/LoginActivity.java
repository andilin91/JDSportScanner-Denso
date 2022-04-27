package id.co.qualitas.erajaya.activity;

import static android.view.Window.FEATURE_NO_TITLE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.densowave.bhtsdk.barcode.BarcodeDataReceivedEvent;
import com.densowave.bhtsdk.barcode.BarcodeException;
import com.densowave.bhtsdk.barcode.BarcodeManager;
import com.densowave.bhtsdk.barcode.BarcodeScanner;
import com.google.gson.Gson;

import id.co.qualitas.erajaya.BuildConfig;
import id.co.qualitas.erajaya.R;
import id.co.qualitas.erajaya.constants.Constants;
import id.co.qualitas.erajaya.helper.Helper;
import id.co.qualitas.erajaya.model.LoginResponse;
import id.co.qualitas.erajaya.model.Material;
import id.co.qualitas.erajaya.model.UserResponse;
import id.co.qualitas.erajaya.model.WSMessage;
import id.co.qualitas.erajaya.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

public class LoginActivity extends BaseActivity implements BarcodeManager.BarcodeManagerListener, BarcodeScanner.BarcodeDataListener {
    private Button btnLogin;
    private EditText edtUsername, edtPassword;
    private TextView txtVersion, txtSetting;
    private Dialog settingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        initialize();
//        initBarcode(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save Scanner Settings
        if (mScannerType != null) {
            outState.putInt("SCANNER_TYPE", SCANNER_TYPE.indexOf(mScannerType));
        }
        if (mSettings != null) {
            outState.putInt("TRIGGER_MODE", TRIGGER_MODE.indexOf(mSettings.scan.triggerMode));
            outState.putBooleanArray("SYMBOL_SETTING", new SymbolSetting(mSettings).getSettings());
        }
    }


    private void setDataScan(String scanData) {
        scanData = scanData.replaceAll("[^\\x20-\\x7e]", "").trim();
        if (scanData != null && !scanData.isEmpty()) {
            if (edtUsername.hasFocus()) {
                edtUsername.setText(scanData);
            } else {
                edtPassword.setText(scanData);
            }
        }
    }

    private void openDialogSetting() {
        settingDialog = new Dialog(this);
        settingDialog.requestWindowFeature(FEATURE_NO_TITLE);
        settingDialog.setContentView(R.layout.dialog_ipaddress);
        EditText ipAddress = settingDialog.findViewById(R.id.ipAddress);
        ipAddress.setText(Helper.getItemParam(Constants.URL).toString());

        Button btnSave = settingDialog.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            if (!ipAddress.getText().toString().isEmpty()) {
                new SessionManager(getApplicationContext()).createUrlSession(ipAddress.getText().toString());
                Helper.setItemParam(Constants.URL, ipAddress.getText().toString());
                Toast.makeText(getApplicationContext(), "IP Address changed", Toast.LENGTH_SHORT).show();
                settingDialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(), "Please fill ip address", Toast.LENGTH_SHORT).show();
            }
        });
        settingDialog.show();
    }

    private void initialize() {
        btnLogin = findViewById(R.id.btnLogin);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        TextView txtVersion = findViewById(R.id.txtVersion);
        String versionName = BuildConfig.VERSION_NAME;
        txtVersion.setText(Constants.VERSION_NAME + versionName);
        txtSetting = findViewById(R.id.setting);
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (edtUsername.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Username cannot empty", Toast.LENGTH_SHORT).show();
                } else if (edtPassword.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Password cannot empty", Toast.LENGTH_SHORT).show();
                } else {
                    PARAM = 1;
                    new RequestUrl().execute();
                    progress.show();
                }
            }
        });
        txtSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogSetting();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        try {
//            if (mBarcodeScanner != null) {
//                if (mSettings == null) {
//                    mSettings = mBarcodeScanner.getSettings();
//                    setScanner();
//                }
//                mBarcodeScanner.setSettings(mSettings);
//                mBarcodeScanner.claim();
//            }
//        } catch (BarcodeException e) {
//            Log.e(TAG, "Error Code = " + e.getErrorCode(), e);
//        } catch (java.lang.IllegalArgumentException e) {
//            Toast toast = Toast.makeText(this, R.string.error_message_symbol_settings, Toast.LENGTH_LONG);
//            toast.show();
//        }
    }

    @Override
    public void onBarcodeManagerCreated(BarcodeManager barcodeManager) {
        // When barcode scanner manager created.
        mBarcodeManager = barcodeManager;
        try {
            List<BarcodeScanner> listScanner = mBarcodeManager.getBarcodeScanners();
            if (listScanner.size() > 0) {
                // Get BarcodeScanner instance
                mBarcodeScanner = listScanner.get(0);

                // Register Data Received event
                mBarcodeScanner.addDataListener(this);
                // Setting for Scanner
                if (mScannerType == null) {
                    mScannerType = mBarcodeScanner.getInfo().getType();
                }
                if (mSettings == null) {
                    mSettings = mBarcodeScanner.getSettings();
                    this.setScanner();
                }
                mBarcodeScanner.setSettings(mSettings);

                // Enable Scanner
                mBarcodeScanner.claim();

            }
        } catch (BarcodeException e) {
            Log.e(TAG, "Error Code = " + e.getErrorCode(), e);
        }
    }


    @Override
    public void onBarcodeDataReceived(BarcodeDataReceivedEvent event) {
        // When Scanner read some data
        List<BarcodeDataReceivedEvent.BarcodeData> listBarcodeData = event.getBarcodeData();

        for (BarcodeDataReceivedEvent.BarcodeData data : listBarcodeData) {

            runOnUiThread(new Runnable() {
                        // Apply data to UI
                        String denso = "";
                        String data = "";

                        Runnable setData(String _denso, String _data) {
                            denso = _denso;
                            data = _data;
                            return this;
                        }

                        @Override
                        public void run() {
                            this.data = this.data.replaceAll("[^\\x20-\\x7e]", "").trim();
                            if (this.data != null && !this.data.isEmpty()) {
                                if (edtUsername.hasFocus()) {
                                    edtUsername.setText(this.data);
                                } else {
                                    edtPassword.setText(this.data);
                                }
                            }
                        }
                    }.setData(data.getSymbologyDenso(), data.getData())
            );
        }
    }

    private class RequestUrl extends AsyncTask<Void, Void, LoginResponse> {

        @Override
        protected LoginResponse doInBackground(Void... voids) {
            try {
                if (PARAM == 1) {
                    String URL_LOGIN = Constants.OAUTH_TOKEN_PATH;
                    @SuppressLint("WrongThread") String pwd = Constants.AND.concat(Constants.TXTPASSWORD).concat(Constants.EQUALS) + edtPassword.getText().toString();
                    @SuppressLint("WrongThread") String username = Constants.USERNAME.concat(Constants.EQUALS) + edtUsername.getText().toString();
                    String grantType = Constants.GRANT_TYPE.concat(Constants.EQUALS) + Constants.TXTPASSWORD + Constants.AND;

                    final String url = Helper.getItemParam(Constants.URL).toString().concat(URL_LOGIN);
                    final String content = grantType.concat(username).concat(pwd);

                    return (LoginResponse) Helper.postWebserviceLogin(url, LoginResponse.class, content);
                } else {
                    String URL_USER_DETAIL = Constants.API_GET_DETAIL_USER;

                    final String url = Helper.getItemParam(Constants.URL).toString().concat(URL_USER_DETAIL);

                    wsResult = (WSMessage) Helper.getWebservice(url, WSMessage.class);
                    return null;
                }
            } catch (Exception ex) {
                if (ex.getMessage() != null) {
                    Helper.setItemParam(Constants.INTERNAL_SERVER_ERROR, ex.getMessage());
                }
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(LoginResponse logins) {
            if (PARAM == 1) {
                if (logins != null) {
                    if (String.valueOf(logins.getError()).equals(Constants.INVALID_GRANT)) {

                        Toast.makeText(getApplicationContext(), Constants.ERROR_LOGIN_2, Toast.LENGTH_SHORT).show();
                    } else {
                        Helper.setItemParam(Constants.LOGIN, logins);
                        Helper.setItemParam(Constants.TOKEN, logins.getAccess_token());
                        new SessionManager(getApplicationContext()).createTokenSession(logins.getAccess_token());
                        PARAM = 2;
                        new RequestUrl().execute();
                    }
                } else {
                    progress.dismiss();
                    if (Helper.getItemParam(Constants.ERROR_LOGIN) != null) {
                        Toast.makeText(getApplicationContext(), Constants.ERROR_LOGIN_2, Toast.LENGTH_SHORT).show();
                        Helper.removeItemParam(Constants.ERROR_LOGIN);
                    } else {
                        Toast.makeText(getApplicationContext(), Constants.INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {

                progress.dismiss();
                if (wsResult != null) {
                    if (wsResult.getIdMessage() == 1) {
                        Gson gson = new Gson();
                        user = gson.fromJson(new Gson().toJson(wsResult.getResult()), UserResponse.class);
                        user.getUser().setUserlogin(edtUsername.getText().toString());
                        user.getUser().setPassword(edtPassword.getText().toString());
                        new SessionManager(getApplicationContext()).createDataSession(Helper.objectToString(user));
                        Helper.setItemParam(Constants.USER_DETAIL, user);
                        Intent intent = new Intent(getApplicationContext(),
                                MenuActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), wsResult.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), Constants.TOKEN_EXPIRED, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    LoginActivity.this);
            alertDialog.setTitle("Change Server");
            final EditText input = new EditText(LoginActivity.this);
            input.setText(Helper.getItemParam(Constants.URL).toString());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            input.setLayoutParams(lp);
            alertDialog.setView(input);
            alertDialog.setPositiveButton("Save",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            new SessionManager(getApplicationContext())
                                    .createUrlSession(input.getText()
                                            .toString());
                            Helper.setItemParam(Helper.getItemParam(Constants.URL).toString(), input.getText()
                                    .toString());
                            Toast.makeText(getApplicationContext(),
                                    "Server Successfully Changed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();
            return true;
        }
        return false;
    }
}
