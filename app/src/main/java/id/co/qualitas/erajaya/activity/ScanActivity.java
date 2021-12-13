package id.co.qualitas.erajaya.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import id.co.qualitas.erajaya.R;
import id.co.qualitas.erajaya.constants.Constants;
import id.co.qualitas.erajaya.databinding.ActivityCreateReceivingBinding;
import id.co.qualitas.erajaya.databinding.ActivityScanBinding;
import id.co.qualitas.erajaya.helper.Helper;
import id.co.qualitas.erajaya.model.LoginResponse;
import id.co.qualitas.erajaya.model.Material;
import id.co.qualitas.erajaya.model.UserResponse;
import id.co.qualitas.erajaya.model.WSMessage;
import id.co.qualitas.erajaya.session.SessionManager;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class ScanActivity extends BaseActivity implements ZXingScannerView.ResultHandler {
    private ActivityScanBinding binding;
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    String scanResult;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
//        scannerView = new ZXingScannerView(this);
//        setContentView(scannerView);
        init();

        initialize();
    }

    private void initialize() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.edtBarcode.getText().toString().isEmpty()){
                    Snackbar.make(view, "Please input barcode or scan a barcode", Snackbar.LENGTH_SHORT).show();
                }else{
                    scanResult = binding.edtBarcode.getText().toString();
                    new RequestUrl().execute();
                    progress.show();
                }
            }
        });

        ViewGroup contentFrame = findViewById(R.id.content_frame);

        scannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        contentFrame.addView(scannerView);
    }

    private static class CustomViewFinderView extends ViewFinderView {
        public static final String TRADE_MARK_TEXT = "Scanning...";
        public static final int TRADE_MARK_TEXT_SIZE_SP = 20;
        public final Paint PAINT = new Paint();

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            PAINT.setColor(Color.WHITE);
            PAINT.setAntiAlias(true);
            float textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    TRADE_MARK_TEXT_SIZE_SP, getResources().getDisplayMetrics());
            PAINT.setTextSize(textPixelSize);
            setSquareViewFinder(true);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawTradeMark(canvas);
        }

        private void drawTradeMark(Canvas canvas) {
            Rect framingRect = getFramingRect();
            float tradeMarkTop;
            float tradeMarkLeft;
            if (framingRect != null) {
                tradeMarkTop = framingRect.bottom + PAINT.getTextSize() + 10;
                tradeMarkLeft = framingRect.left;
            } else {
                tradeMarkTop = 10;
                tradeMarkLeft = canvas.getHeight() - PAINT.getTextSize() - 10;
            }
            canvas.drawText(TRADE_MARK_TEXT, tradeMarkLeft, tradeMarkTop, PAINT);
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(ScanActivity.this, CAMERA)
                == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        setCamera();
//                        Toast.makeText(ScanActivity.this, "Permission granted", Toast.LENGTH_LONG).show();
                    } else {
//                        Toast.makeText(ScanActivity.this, "Permission denied", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M  && !shouldShowRequestPermissionRationale(permissions[0])) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                            alertDialogBuilder.setTitle("Change Permissions in Settings");
                            alertDialogBuilder
                                    .setMessage("" +
                                            "\nClick SETTINGS to Manually Set\n"+"Permissions to use Camera")
                                    .setCancelable(false)
                                    .setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                                            intent.setData(uri);
                                            startActivityForResult(intent, 1000);     // Comment 3.
                                        }
                                    })
                                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            onBackPressed();
                                        }
                                    });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    }
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                setCamera();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
            }
        } else {
            setCamera();
        }
    }

    private void setCamera() {
        if (scannerView == null) {
            scannerView = new ZXingScannerView(this);
            setContentView(scannerView);
        }
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(ScanActivity.this)
                .setMessage(message)
                .setPositiveButton("Ok", listener)
                .setNegativeButton("Cancel", null)
                .create().show();
    }

    @Override
    public void handleResult(Result result) {
        scanResult = result.getText();
//        Toast.makeText(ScanActivity.this, scanResult, Toast.LENGTH_LONG).show();
//        scannerView.resumeCameraPreview(ScanActivity.this);
        new RequestUrl().execute();
        progress.show();
    }

    private class RequestUrl extends AsyncTask<Void, Void, WSMessage> {

        @Override
        protected WSMessage doInBackground(Void... voids) {
            try {

                String URL_MATERIAL = Constants.API_GET_MATERIAL_RECEIVING + Constants.QUESTION + "ean=" + scanResult;

                final String url = Helper.getItemParam(Constants.URL).toString().concat(URL_MATERIAL);

                return (WSMessage) Helper.getWebservice(url, WSMessage.class);
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
                if(results.getIdMessage() == 1) {
                    Gson gson = new Gson();
                    material = gson.fromJson(new Gson().toJson(results.getResult()), Material.class);
                    material.setQty(1);
                    setData();
                }else{
                    Toast.makeText(getApplicationContext(), results.getMessage(), Toast.LENGTH_SHORT).show();
                    scannerView.resumeCameraPreview(ScanActivity.this);
                    progress.dismiss();
                }
            } else {
                progress.dismiss();
                if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                    Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), Constants.TOKEN_EXPIRED, Toast.LENGTH_SHORT).show();
                }
                scannerView.resumeCameraPreview(ScanActivity.this);
            }
        }

    }

    private void setData() {
        List<Material> materialList = (List<Material>) Helper.getItemParam(Constants.LIST_MATERIAL);
        if(materialList == null){
            materialList = new ArrayList<>();
        }else{
            if(materialList.size() == 0){
                materialList.add(material);
            }else{
                int FLAG = 0;
                for(Material m : materialList){
                    if(m.getMaterialNumber().equals(material.getMaterialNumber())){
                        m.setQty(m.getQty() + material.getQty());
                        FLAG = 1;
                        break;
                    }
                }
                if(FLAG == 0){
                    materialList.add(material);
                }
            }
            Helper.setItemParam(Constants.LIST_MATERIAL, materialList);
        }
        progress.dismiss();
        onBackPressed();

    }
}
