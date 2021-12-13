package id.co.qualitas.erajaya.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;

import id.co.qualitas.erajaya.BuildConfig;
import id.co.qualitas.erajaya.R;
import id.co.qualitas.erajaya.constants.Constants;
import id.co.qualitas.erajaya.databinding.ActivityMenuBinding;
import id.co.qualitas.erajaya.helper.Helper;
import id.co.qualitas.erajaya.model.Issuing;
import id.co.qualitas.erajaya.model.Material;
import id.co.qualitas.erajaya.model.User;
import id.co.qualitas.erajaya.model.WSMessage;
import id.co.qualitas.erajaya.session.SessionManager;


/**
 * Created by TED on 17-Jul-20
 */
public class MenuActivity extends BaseActivity {
    ActivityMenuBinding binding;
    String[] singleChoiceItems;
    String[] bin = {"Bin 001", "Bin 002", "Bin 003", "Bin 004"};
    int itemSelected = 0;
    public int PARAM = 0;
    private BottomSheetDialog bottomSheetDialog;
    private EditText edtOldPass, edtNewPass, edtConfirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        binding.btnReceiving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, DocReceivingHeaderActivity.class);
                startActivity(intent);
            }
        });

        binding.btnIssuing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, DocIssuingHeaderActivity.class);
                startActivity(intent);
            }
        });

        binding.btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBSChangePass();
            }
        });

        binding.imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MenuActivity.this)
                        .setMessage("Are you sure to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new SessionManager(getApplicationContext()).clearData();
                                Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        });
        TextView txtVersion = findViewById(R.id.txtVersion);
        String versionName = BuildConfig.VERSION_NAME;
        txtVersion.setText("JDSport-EAN Version " + versionName);
    }

    public void showBSChangePass() {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Helper.showBottomSheetDialog(this, R.layout.bs_change_pass, (itemView, bottomSheet) -> {
                Button btnSubmit = itemView.findViewById(R.id.btnSubmit);
                edtOldPass = itemView.findViewById(R.id.edtOldPass);
                edtNewPass = itemView.findViewById(R.id.edtNewPass);
                edtConfirmPass = itemView.findViewById(R.id.edtConfirmPass);

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!user.getUser().getPassword().equals(edtOldPass.getText().toString())) {
                            Toast.makeText(getApplicationContext(), "Current password wrong", Toast.LENGTH_SHORT).show();
                        } else if (user.getUser().getPassword().equals(edtNewPass.getText().toString())) {
                            Toast.makeText(getApplicationContext(), "Current password and new password cannot be same", Toast.LENGTH_SHORT).show();
                        } else if (!edtNewPass.getText().toString().equals(edtConfirmPass.getText().toString())) {
                            Toast.makeText(getApplicationContext(), "New password and confirm password does not match", Toast.LENGTH_SHORT).show();
                        } else {
                            PARAM = 0;
                            new RequestUrl().execute();
                            progress.show();
                        }
                    }
                });
            });
        }
    }

    private class RequestUrl extends AsyncTask<Void, Void, WSMessage> {

        @Override
        protected WSMessage doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String URL_CHANGE_PASS = Constants.API_CHANGE_PASSWORD;

                    final String url = Helper.getItemParam(Constants.URL).toString().concat(URL_CHANGE_PASS);
                    User userDetails = new User();
                    userDetails.setPassword(edtNewPass.getText().toString());
                    userDetails.setOldPassword(edtOldPass.getText().toString());
                    userDetails.setUsername(user.getUser().getUsername());

                    return (WSMessage) Helper.postWebserviceWithBody(url, WSMessage.class, userDetails, getApplicationContext());
                } else {
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
        protected void onPostExecute(WSMessage results) {
            if (results != null) {
                progress.dismiss();
                if (results.getIdMessage() == 1) {
                    if (PARAM == 0) {
                        user.getUser().setPassword(edtNewPass.getText().toString());
                        Helper.setItemParam(Constants.USER_DETAIL, user);
                        new SessionManager(getApplicationContext()).createDataSession(Helper.objectToString(user));
                        Toast.makeText(getApplicationContext(), results.getMessage(), Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), results.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                progress.dismiss();
                if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                    Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), Constants.TOKEN_EXPIRED, Toast.LENGTH_SHORT).show();
                }
            }

        }

    }

}
