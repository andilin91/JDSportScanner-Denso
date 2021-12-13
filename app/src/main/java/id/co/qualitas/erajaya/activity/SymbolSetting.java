//**************************************************************************//
//***                                                                    ***//
//***    Product Name    : Scanner Sample for BHT                        ***//
//***    File Name       : SymbolSetting.java                            ***//
//***                                                                    ***//
//***    Edition History                                                 ***//
//***    Version  Date      Comments                                     ***//
//***    -------  --------  ---------------------------------------------***//
//***    1.0.0    03/31/18  Original                                     ***//
//***                                                                    ***//
//***                    Copyright (C) 2018 DENSO WAVE INCORPORATED      ***//
//***                    All rights reserved.                            ***//
//**************************************************************************//
//+------------------------------------------------------------------------+//
//|                                                                        |//
//|  DENSO WAVE INCORPORATED retains the copyright to this source code,    |//
//|  but allows users to use it, in whole or in part, and make             |//
//|  modifications without prior permission of DENSO WAVE INCORPORATED.    |//
//|  DENSO WAVE INCORPORATED shall not, however, be held responsible for   |//
//|  any damages arising from such use or modifications.                   |//
//|                                                                        |//
//+------------------------------------------------------------------------+//
package id.co.qualitas.erajaya.activity;

import com.densowave.bhtsdk.barcode.BarcodeScannerSettings;

class SymbolSetting {

    private boolean[] Settings = new boolean[19];

    public boolean[] getSettings(){
        return this.Settings;
    }
    public void setSettings(BarcodeScannerSettings scannerSettings){
        // load Symbology settings to Array
        Settings[0] = scannerSettings.decode.symbologies.ean13UpcA.enabled;
        Settings[1] = scannerSettings.decode.symbologies.ean8.enabled;
        Settings[2] = scannerSettings.decode.symbologies.upcE.enabled;
        Settings[3] = scannerSettings.decode.symbologies.itf.enabled;
        Settings[4] = scannerSettings.decode.symbologies.stf.enabled;
        Settings[5] = scannerSettings.decode.symbologies.codabar.enabled;
        Settings[6] = scannerSettings.decode.symbologies.code39.enabled;
        Settings[7] = scannerSettings.decode.symbologies.code93.enabled;
        Settings[8] = scannerSettings.decode.symbologies.code128.enabled;
        Settings[9] = scannerSettings.decode.symbologies.msi.enabled;
        Settings[10] = scannerSettings.decode.symbologies.gs1DataBar.enabled;
        Settings[11] = scannerSettings.decode.symbologies.qrCode.enabled;
        Settings[12] = scannerSettings.decode.symbologies.microQr.enabled;
        Settings[13] = scannerSettings.decode.symbologies.iqrCode.enabled;
        Settings[14] = scannerSettings.decode.symbologies.pdf417.enabled;
        Settings[15] = scannerSettings.decode.symbologies.microPdf417.enabled;
        Settings[16] = scannerSettings.decode.symbologies.maxiCode.enabled;
        Settings[17] = scannerSettings.decode.symbologies.dataMatrix.enabled;
        Settings[18] = scannerSettings.decode.symbologies.aztecCode.enabled;
    }
    public void setSettings(boolean[] _Setting)
    {
        this.Settings = _Setting;
    }

    public SymbolSetting(BarcodeScannerSettings scannerSettings){
        this.setSettings(scannerSettings);
   }

   public BarcodeScannerSettings applySettings(BarcodeScannerSettings scannerSettings){
        // Apply BarcodeScannerSettings from Symbology Array
        scannerSettings.decode.symbologies.ean13UpcA.enabled = Settings[0];
        scannerSettings.decode.symbologies.ean8.enabled = Settings[1];
        scannerSettings.decode.symbologies.upcE.enabled = Settings[2];
        scannerSettings.decode.symbologies.itf.enabled = Settings[3];
        scannerSettings.decode.symbologies.stf.enabled = Settings[4];
        scannerSettings.decode.symbologies.codabar.enabled = Settings[5];
        scannerSettings.decode.symbologies.code39.enabled = Settings[6];
        scannerSettings.decode.symbologies.code93.enabled = Settings[7];
        scannerSettings.decode.symbologies.code128.enabled = Settings[8];
        scannerSettings.decode.symbologies.msi.enabled = Settings[9];
        scannerSettings.decode.symbologies.gs1DataBar.enabled = Settings[10];
        scannerSettings.decode.symbologies.qrCode.enabled = Settings[11];
        scannerSettings.decode.symbologies.microQr.enabled = Settings[12];
        scannerSettings.decode.symbologies.iqrCode.enabled = Settings[13];
        scannerSettings.decode.symbologies.pdf417.enabled = Settings[14];
       scannerSettings.decode.symbologies.microPdf417.enabled = Settings[15];
        scannerSettings.decode.symbologies.maxiCode.enabled = Settings[16];
        scannerSettings.decode.symbologies.dataMatrix.enabled = Settings[17];
       scannerSettings.decode.symbologies.aztecCode.enabled = Settings[18];

       return scannerSettings;
   }
}
