package id.co.qualitas.erajaya.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.densowave.bhtsdk.barcode.BarcodeDataReceivedEvent;
import com.densowave.bhtsdk.barcode.BarcodeException;
import com.densowave.bhtsdk.barcode.BarcodeManager;
import com.densowave.bhtsdk.barcode.BarcodeScanner;
import com.densowave.bhtsdk.barcode.BarcodeScannerInfo;
import com.densowave.bhtsdk.barcode.BarcodeScannerSettings;
import com.densowave.bhtsdk.barcode.DecodeSettings;
import com.densowave.bhtsdk.barcode.NotificationSettings;
import com.densowave.bhtsdk.barcode.ScanSettings;
import com.densowave.bhtsdk.barcode.Symbologies;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import id.co.qualitas.erajaya.constants.Constants;
import id.co.qualitas.erajaya.helper.Helper;
import id.co.qualitas.erajaya.model.Material;
import id.co.qualitas.erajaya.model.UserResponse;
import id.co.qualitas.erajaya.model.WSMessage;

public class BaseActivity extends AppCompatActivity implements BarcodeManager.BarcodeManagerListener, BarcodeScanner.BarcodeDataListener {
    protected Context context;
    protected View rootView;
    protected int flagInit;
    protected ProgressDialog progress;
    protected String result = "";
    protected boolean connection = false;
    protected String idUser;
    public UserResponse user;
    public WSMessage wsResult;
    public Material material;
    public int PARAM = 0;
    public int index = 0;

    protected boolean loading = true;
    protected int pastVisiblesItems, visibleItemCount, totalItemCount;
    public TextView loadMore;

    private String statusString = "";
    private boolean bContinuousMode = false;
    private int dataLength = 0;

    public EditText edtBarcode;
    public String resultBarcode;

    public BarcodeManager mBarcodeManager = null;
    public BarcodeScanner mBarcodeScanner = null;
    public BarcodeScannerSettings mSettings = null;
    public BarcodeScannerInfo.BarcodeScannerType mScannerType = null;
    public final String TAG = "BarcodeWM";
    public final List<ScanSettings.TriggerMode> TRIGGER_MODE = Arrays.asList(ScanSettings.TriggerMode.AUTO_OFF, ScanSettings.TriggerMode.MOMENTARY, ScanSettings.TriggerMode.ALTERNATE, ScanSettings.TriggerMode.CONTINUOUS, ScanSettings.TriggerMode.TRIGGER_RELEASE);
    public final List<BarcodeScannerInfo.BarcodeScannerType> SCANNER_TYPE = Arrays.asList(BarcodeScannerInfo.BarcodeScannerType.TYPE_1D, BarcodeScannerInfo.BarcodeScannerType.TYPE_2D, BarcodeScannerInfo.BarcodeScannerType.TYPE_2D_LONG);

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

                        }
                    }.setData(data.getSymbologyDenso(), data.getData())
            );
        }
    }

    public void setScanner() {
        // Scanner default settings

        BarcodeScannerSettings settings = mBarcodeScanner.getSettings();

        //Trigger Mode
        settings.scan.triggerMode = ScanSettings.TriggerMode.AUTO_OFF;
        //settings.scan.triggerMode = ScanSettings.TriggerMode.MOMENTARY;
        //settings.scan.triggerMode = ScanSettings.TriggerMode.ALTERNATE;
        //settings.scan.triggerMode = ScanSettings.TriggerMode.CONTINUOUS;
        //settings.scan.triggerMode = ScanSettings.TriggerMode.TRIGGER_RELEASE;

        //For 2D Module Settings
        if (mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_2D) {
            // Light Mode
            settings.scan.lightMode = ScanSettings.LightMode.AUTO;
            //settings.scan.lightMode = ScanSettings.LightMode.ALWAYS_ON;
            //settings.scan.lightMode = ScanSettings.LightMode.OFF;

            //Marker Mode
            settings.scan.markerMode = ScanSettings.MarkerMode.NORMAL;
            //settings.scan.markerMode = ScanSettings.MarkerMode.AHEAD;
            //settings.scan.markerMode = ScanSettings.MarkerMode.OFF;
        }

        //For 2D_LONG Module Settings
        if (mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_2D_LONG) {
            settings.scan.sideLightMode = ScanSettings.SideLightMode.OFF;
            //settings.scan.sideLightMode = ScanSettings.SideLightMode.ON;
        }


        // Notification Sound Settings
        settings.notification.sound.enabled = true;
        //settings.notification.sound.enabled = false;

        if (settings.notification.sound.enabled) {
            settings.notification.sound.usageType = NotificationSettings.UsageType.RINGTONE;
            //settings.notification.sound.usageType = NotificationSettings.UsageType.MEDIA;
            //settings.notification.sound.usageType = NotificationSettings.UsageType.ALARM;

            if (settings.notification.sound.usageType == NotificationSettings.UsageType.MEDIA) {
                //TO BE Implement
                settings.notification.sound.goodDecodeFilePath = "";
            }
        }

        //Notification Vibrator
        settings.notification.vibrate.enabled = false;
        //settings.notification.vibrate.enabled = false;

        // Decode Settings

        // Decode interval
        settings.decode.sameBarcodeIntervalTime = 10; // 1,000msec

        // Decode Level
        settings.decode.decodeLevel = 4;              // Decode Level

        // Invert Mode
        settings.decode.invertMode = DecodeSettings.InvertMode.DISABLED;
        //settings.decode.invertMode = DecodeSettings.InvertMode.INVERSION_ONLY;
        //settings.decode.invertMode = DecodeSettings.InvertMode.AUTO;

        // Point Scan Mode
        settings.decode.pointScanMode = DecodeSettings.PointScanMode.DISABLED;
        //settings.decode.pointScanMode = DecodeSettings.PointScanMode.ENABLED;

        // Reverse Mode
        settings.decode.reverseMode = DecodeSettings.ReverseMode.DISABLED;
        //settings.decode.reverseMode = DecodeSettings.ReverseMode.ENABLED;

        // Encode Charset
        settings.decode.charset = Charset.forName("Shift-JIS");
        //settings.decode.charset = Charset.forName("UTF-8");

        // Symbology Settings

        // Multi Line
        settings.decode.multiLineMode.enabled = false;

        //JAN-13(EAN-13), UPC-A
        settings.decode.symbologies.ean13UpcA.enabled = true;
        if ((mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_1D) ||
                (mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_2D)) {
            settings.decode.symbologies.ean13UpcA.firstCharacter = "";
            settings.decode.symbologies.ean13UpcA.secondCharacter = "";
        }
        settings.editing.ean13.reportCheckDigit = true;
        settings.editing.upcA.reportCheckDigit = true;
        settings.editing.upcA.addLeadingZero = true;

        // EAN-13 add on
        settings.decode.symbologies.ean13UpcA.addOn.enabled = false;
        settings.decode.symbologies.ean13UpcA.addOn.onlyWithAddOn = false;
        if ((mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_1D) ||
                (mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_2D)) {
            settings.decode.symbologies.ean13UpcA.addOn.addOn2Digit = false;
            settings.decode.symbologies.ean13UpcA.addOn.addOn5Digit = false;
        }

        // JAN-8(EAN-8)
        settings.decode.symbologies.ean8.enabled = true;
        if ((mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_1D) ||
                (mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_2D)) {
            settings.decode.symbologies.ean8.firstCharacter = "";
            settings.decode.symbologies.ean8.secondCharacter = "";
        }
        settings.editing.ean8.reportCheckDigit = true;

        // EAN-8 add on
        settings.decode.symbologies.ean8.addOn.enabled = false;
        settings.decode.symbologies.ean8.addOn.onlyWithAddOn = false;
        if ((mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_1D) ||
                (mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_2D)) {
            settings.decode.symbologies.ean8.addOn.addOn2Digit = false;
            settings.decode.symbologies.ean8.addOn.addOn5Digit = false;
        }

        // UPC-E
        settings.decode.symbologies.upcE.enabled = true;
        if ((mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_1D) ||
                (mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_2D)) {
            settings.decode.symbologies.upcE.firstCharacter = "";
            settings.decode.symbologies.upcE.secondCharacter = "";
        }
        settings.editing.upcE.reportCheckDigit = true;
        settings.editing.upcE.addLeadingZero = false;
        settings.editing.upcE.convertToUpcA = false;
        settings.editing.upcE.reportNumberSystemCharacterOfConvertedUpcA = true;

        // UPC-E add on
        settings.decode.symbologies.upcE.addOn.enabled = false;
        settings.decode.symbologies.upcE.addOn.onlyWithAddOn = false;
        if ((mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_1D) ||
                (mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_2D)) {
            settings.decode.symbologies.upcE.addOn.addOn2Digit = false;
            settings.decode.symbologies.upcE.addOn.addOn5Digit = false;
        }

        // ITF
        settings.decode.symbologies.itf.enabled = true;
        settings.decode.symbologies.itf.lengthMin = 4;
        settings.decode.symbologies.itf.lengthMax = 99;
        settings.decode.symbologies.itf.verifyCheckDigit = false;
        //settings.decode.symbologies.itf.verifyCheckDigit = true;
        settings.editing.itf.reportCheckDigit = true;

        // STF
        settings.decode.symbologies.stf.enabled = true;
        settings.decode.symbologies.stf.lengthMin = 4;
        settings.decode.symbologies.stf.lengthMax = 99;
        settings.decode.symbologies.stf.verifyCheckDigit = false;
        //settings.decode.symbologies.stf.verifyCheckDigit = true;
        if ((mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_1D) ||
                (mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_2D_LONG)) {
            settings.decode.symbologies.stf.startStopCharacter = "";
            //settings.decode.symbologies.stf.startStopCharacter = "S";
            //settings.decode.symbologies.stf.startStopCharacter = "N";
        }
        settings.editing.stf.reportCheckDigit = true;

        // Codabar
        settings.decode.symbologies.codabar.enabled = true;
        settings.decode.symbologies.codabar.lengthMin = 4;
        settings.decode.symbologies.codabar.lengthMax = 99;
        settings.decode.symbologies.codabar.verifyCheckDigit = false;
        //settings.decode.symbologies.codabar.verifyCheckDigit = true;
        if ((mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_1D) ||
                (mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_2D)) {
            settings.decode.symbologies.codabar.startStopCharacter = "";
        }
        settings.editing.codabar.reportCheckDigit = true;
        settings.editing.codabar.reportStartStopCharacter = true;
        settings.editing.codabar.convertToUpperCase = false;

        // Code39
        settings.decode.symbologies.code39.enabled = true;
        settings.decode.symbologies.code39.lengthMin = 1;
        settings.decode.symbologies.code39.lengthMax = 99;
        settings.decode.symbologies.code39.verifyCheckDigit = false;
        //settings.decode.symbologies.code39.verifyCheckDigit = true;
        settings.editing.code39.reportCheckDigit = true;
        settings.editing.code39.reportStartStopCharacter = false;

        // Code93
        settings.decode.symbologies.code93.enabled = true;
        settings.decode.symbologies.code93.lengthMin = 1;
        settings.decode.symbologies.code93.lengthMax = 99;

        // Code128
        settings.decode.symbologies.code128.enabled = true;
        settings.decode.symbologies.code128.lengthMin = 1;
        settings.decode.symbologies.code128.lengthMax = 99;

        // MSI
        if (mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_1D) {
            settings.decode.symbologies.msi.enabled = true;
            settings.decode.symbologies.msi.lengthMin = 1;
            settings.decode.symbologies.msi.lengthMax = 99;
            settings.decode.symbologies.msi.numberOfCheckDigitVerification = 1;
            //settings.decode.symbologies.msi.numberOfCheckDigitVerification = 2;
        }

        // GS1 Databar
        settings.decode.symbologies.gs1DataBar.enabled = true;
        if ((mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_1D) ||
                (mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_2D)) {
            settings.decode.symbologies.gs1DataBar.stacked = false;
        }

        // Gs1 Databar Limited
        settings.decode.symbologies.gs1DataBarLimited.enabled = false;

        // Gs1 Databar Expanded
        settings.decode.symbologies.gs1DataBarExpanded.enabled = false;
        settings.decode.symbologies.gs1DataBarExpanded.lengthMin = 1;
        settings.decode.symbologies.gs1DataBarExpanded.lengthMax = 99;
        if ((mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_1D) ||
                (mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_2D)) {
            settings.decode.symbologies.gs1DataBarExpanded.stacked = false;
        }

        // Gs1 Composite
        settings.decode.symbologies.gs1Composite.enabled = false;

        if ((mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_2D) ||
                (mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_2D_LONG)) {

            // QR Code
            settings.decode.symbologies.qrCode.enabled = true;

            if (mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_2D) {
                settings.decode.symbologies.qrCode.splitMode = Symbologies.SplitModeQr.DISABLED;
                //settings.decode.symbologies.qrCode.splitMode = Symbologies.SplitModeQr.EDIT;
                //settings.decode.symbologies.qrCode.splitMode = Symbologies.SplitModeQr.BATCH_EDIT;
                //settings.decode.symbologies.qrCode.splitMode = Symbologies.SplitModeQr.NON_EDIT;

                // QR Code Model1
                settings.decode.symbologies.qrCode.model1.enabled = true;
                settings.decode.symbologies.qrCode.model1.versionMin = 1;
                settings.decode.symbologies.qrCode.model1.versionMax = 22;

                // QR Code Model2
                settings.decode.symbologies.qrCode.model2.enabled = true;
                settings.decode.symbologies.qrCode.model2.versionMin = 1;
                settings.decode.symbologies.qrCode.model2.versionMax = 40;

                // Micro QR Code
                settings.decode.symbologies.microQr.enabled = true;
                settings.decode.symbologies.microQr.versionMin = 1;
                settings.decode.symbologies.microQr.versionMax = 4;

                // iQR Code
                settings.decode.symbologies.iqrCode.enabled = true;
                settings.decode.symbologies.iqrCode.splitMode = Symbologies.SplitModeIqr.DISABLED;
                //settings.decode.symbologies.iqrCode.splitMode = Symbologies.SplitModeIqr.EDIT;
                //settings.decode.symbologies.iqrCode.splitMode = Symbologies.SplitModeIqr.NON_EDIT;

                // Square iQR Code
                settings.decode.symbologies.iqrCode.square.enabled = true;
                settings.decode.symbologies.iqrCode.square.versionMin = 1;
                settings.decode.symbologies.iqrCode.square.versionMax = 61;

                // Rectangle iQR Code
                settings.decode.symbologies.iqrCode.rectangle.enabled = true;
                settings.decode.symbologies.iqrCode.rectangle.versionMin = 1;
                settings.decode.symbologies.iqrCode.rectangle.versionMax = 15;

                //Aztec
                settings.decode.symbologies.aztecCode.enabled = true;
                settings.decode.symbologies.aztecCode.fullRange.enabled = true;
                settings.decode.symbologies.aztecCode.fullRange.layerMin = 1;
                settings.decode.symbologies.aztecCode.fullRange.layerMax = 32;
                settings.decode.symbologies.aztecCode.compact.enabled = true;
                settings.decode.symbologies.aztecCode.compact.layerMin = 1;
                settings.decode.symbologies.aztecCode.compact.layerMax = 4;

            } else {
                //For 2D Long model
                settings.decode.symbologies.microQr.enabled = false;
                settings.decode.symbologies.iqrCode.enabled = false;
                settings.decode.symbologies.aztecCode.enabled = false;
            }

            // Data Matrix
            settings.decode.symbologies.dataMatrix.enabled = true;

            if (mScannerType == BarcodeScannerInfo.BarcodeScannerType.TYPE_2D) {
                // DataMatrix Square
                settings.decode.symbologies.dataMatrix.square.enabled = true;
                settings.decode.symbologies.dataMatrix.square.codeNumberMin = 1;
                settings.decode.symbologies.dataMatrix.square.codeNumberMax = 24;

                // DataMatrix ReactAngle
                settings.decode.symbologies.dataMatrix.rectangle.enabled = true;
                settings.decode.symbologies.dataMatrix.rectangle.codeNumberMin = 1;
                settings.decode.symbologies.dataMatrix.rectangle.codeNumberMax = 6;
            }

            // PDF417
            settings.decode.symbologies.pdf417.enabled = true;

            // Micro PDF 417
            settings.decode.symbologies.microPdf417.enabled = true;

            // Maxi
            settings.decode.symbologies.maxiCode.enabled = true;
        } else {
            //For 1D model
            settings.decode.symbologies.qrCode.enabled = false;
            settings.decode.symbologies.microQr.enabled = false;
            settings.decode.symbologies.iqrCode.enabled = false;
            settings.decode.symbologies.pdf417.enabled = false;
            settings.decode.symbologies.microPdf417.enabled = false;
            settings.decode.symbologies.maxiCode.enabled = false;
            settings.decode.symbologies.dataMatrix.enabled = false;
            settings.decode.symbologies.aztecCode.enabled = false;
        }

        mBarcodeScanner.setSettings(settings);
    }


    public void init() {
        Helper.trustSSL();
        user = (UserResponse) Helper.getItemParam(Constants.USER_DETAIL);
        context = this.getApplicationContext();
        rootView = findViewById(android.R.id.content);
        progress = new ProgressDialog(this);
        progress.setMessage(Constants.STR_WAIT);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
//        Helper.trustSSL();

    }

    public void initBarcode(Bundle savedInstanceState){
        if (savedInstanceState != null) {
            // Reload Settings from Saved Parameter
            try {
                // Type of Scanner
                mScannerType = SCANNER_TYPE.get(savedInstanceState.getInt("SCANNER_TYPE"));

                // Scanner Settings
                mSettings = new BarcodeScannerSettings();
                this.setScanner();
                mSettings.scan.triggerMode = TRIGGER_MODE.get(savedInstanceState.getInt("TRIGGER_MODE"));
                SymbolSetting setting = new SymbolSetting(mSettings);
                setting.setSettings(savedInstanceState.getBooleanArray("SYMBOL_SETTING"));
                mSettings = setting.applySettings(mSettings);

            } catch (NullPointerException ex) {
                // Set to default if Wrong Parameters has been reloaded.
                mScannerType = null;
                mSettings = null;
            }
        }
        try {
            // Create BarcodeManager
            BarcodeManager.create(this, this);
        } catch (BarcodeException e) {
            Log.e(TAG, "Error Code = " + e.getErrorCode(), e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Release scanner resources
        if (mBarcodeScanner != null) {
            try {
                // Disable Scanner
                mBarcodeScanner.close();
            } catch (BarcodeException e) {
                Log.e(TAG, "Error Code = " + e.getErrorCode(), e);
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        // Release scanner resources
        if (mBarcodeScanner != null) {
            try {
                // Remove Scanner instance
                mBarcodeScanner.destroy();
            } catch (BarcodeException e) {
                Log.e(TAG, "Error Code = " + e.getErrorCode(), e);
            }
        }

        if (mBarcodeManager != null) {
            // Remove Scanner Manager
            mBarcodeManager.destroy();
            mBarcodeManager = null;
        }
    }


    public ProgressDialog getProgress() {
        return progress;
    }

    public void setProgress(ProgressDialog progress) {
        this.progress = progress;
    }

    public void setButton(Button btn, String text, int id) {
        btn.setBackgroundDrawable(ContextCompat
                .getDrawable(getApplicationContext(),
                        id));
        btn.setText(text);

    }

    public void setImage(ImageView img, int id) {
        img.setImageDrawable(ContextCompat
                .getDrawable(getApplicationContext(),
                        id));
    }

}
