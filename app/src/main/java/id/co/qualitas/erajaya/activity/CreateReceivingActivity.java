package id.co.qualitas.erajaya.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.densowave.bhtsdk.barcode.BarcodeDataReceivedEvent;
import com.densowave.bhtsdk.barcode.BarcodeException;
import com.densowave.bhtsdk.barcode.BarcodeManager;
import com.densowave.bhtsdk.barcode.BarcodeScanner;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import id.co.qualitas.erajaya.R;
import id.co.qualitas.erajaya.adapter.CreateReceivingAdapter;
import id.co.qualitas.erajaya.constants.Constants;
import id.co.qualitas.erajaya.databinding.ActivityCreateReceivingBinding;
import id.co.qualitas.erajaya.helper.Helper;
import id.co.qualitas.erajaya.model.Material;
import id.co.qualitas.erajaya.model.MovType;
import id.co.qualitas.erajaya.model.Plant;
import id.co.qualitas.erajaya.model.Receiving;
import id.co.qualitas.erajaya.model.Sloc;
import id.co.qualitas.erajaya.model.WSMessage;
import id.co.qualitas.erajaya.session.SessionManager;

public class CreateReceivingActivity extends BaseActivity {
    private ActivityCreateReceivingBinding binding;
    //    private List<Plant> listPlant;
    private List<String> listPlant = new ArrayList<>();
    private List<String> listSloc = new ArrayList<>();
    private List<String> listMovTyp = new ArrayList<>();
    private List<Material> listMaterial = new ArrayList<>();
    private CreateReceivingAdapter adapter;
    private List<Receiving> listReceiving = new ArrayList<>();
    private int PARAM = 0;
    private String ean;
    private BottomSheetDialog bottomSheetDialog;
    private EditText edtRecvNo, edtRefDocNo, edtHeaderText;
    private Spinner spPlant, spMovType, spSloc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateReceivingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        setData();
        initialize();
        initBarcode(savedInstanceState);
    }

    private void initialize() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.imgSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogHeader();
            }
        });

    }


    public void showDialogHeader() {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Helper.showBottomSheetDialog(this, R.layout.bs_header_receiving, (itemView, bottomSheet) -> {
                Button btnSubmit = itemView.findViewById(R.id.btnSubmit);
                edtRecvNo = itemView.findViewById(R.id.edtRecvNo);
                edtRefDocNo = itemView.findViewById(R.id.edtRefDocNo);
                edtHeaderText = itemView.findViewById(R.id.edtHeaderText);
                spPlant = itemView.findViewById(R.id.spPlant);
                spMovType = itemView.findViewById(R.id.spMovType);
                spSloc = itemView.findViewById(R.id.spSloc);

                ArrayAdapter<String> dataPlantAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listPlant);
                dataPlantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spPlant.setAdapter(dataPlantAdapter);

                ArrayAdapter<String> dataSlocAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listMovTyp);
                dataSlocAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spMovType.setAdapter(dataSlocAdapter);

                ArrayAdapter<String> dataMovTypAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listSloc);
                dataMovTypAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spSloc.setAdapter(dataMovTypAdapter);

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String plant, sloc, movType;
                        plant = listPlant.get(spPlant.getSelectedItemPosition());
                        sloc = listSloc.get(spSloc.getSelectedItemPosition());
                        movType = listMovTyp.get(spMovType.getSelectedItemPosition());
                        if (edtRecvNo.getText().toString().isEmpty()) {
                            Snackbar.make(view, "Receiving doc number cannot empty", Snackbar.LENGTH_SHORT).show();
                        } else if (edtRefDocNo.getText().toString().isEmpty()) {
                            Snackbar.make(view, "Ref doc no. cannot empty", Snackbar.LENGTH_SHORT).show();
                        } else if (edtHeaderText.getText().toString().isEmpty()) {
                            Snackbar.make(view, "Header Text cannot empty", Snackbar.LENGTH_SHORT).show();
                        } else if (listMaterial.size() == 0) {
                            Snackbar.make(view, "Item line cannot empty", Snackbar.LENGTH_SHORT).show();
                        } else {
                            new AlertDialog.Builder(CreateReceivingActivity.this)
                                    .setMessage("Are you sure to submit this document?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            listReceiving = new ArrayList<>();
                                            for (Material mat : listMaterial) {
                                                Receiving receiving = new Receiving(edtRecvNo.getText().toString(), mat.getMaterialNumber()
                                                        , Helper.todayDate1("yyyy-MM-dd"), edtRefDocNo.getText().toString()
                                                        , edtHeaderText.getText().toString(), plant, sloc, movType, mat.getQty(), Build.ID + ";" + Build.MODEL);
                                                listReceiving.add(receiving);
                                            }
                                            PARAM = 0;
                                            new RequestUrl().execute();
                                            progress.show();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        }
                    }
                });
            });
        }
    }

    public void removeItem(Material material, int position) {
        listMaterial.remove(material);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, listMaterial.size());
        if(listMaterial.size() == 0){
            binding.emptyLayout.setVisibility(View.VISIBLE);
            binding.rcItem.setVisibility(View.GONE);
            binding.imgSubmit.setVisibility(View.GONE);
        }else{
            binding.emptyLayout.setVisibility(View.GONE);
            binding.rcItem.setVisibility(View.VISIBLE);
            binding.imgSubmit.setVisibility(View.VISIBLE);
        }
    }

    private class RequestUrl extends AsyncTask<Void, Void, WSMessage> {

        @Override
        protected WSMessage doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String URL_RECEIVING = Constants.API_INSERT_RECEIVING;

                    final String url = Helper.getItemParam(Constants.URL).toString().concat(URL_RECEIVING);

                    return (WSMessage) Helper.postWebserviceWithBody(url, WSMessage.class, listReceiving, getApplicationContext());
                } else {
                    String URL_MATERIAL = Constants.API_GET_MATERIAL_RECEIVING + Constants.QUESTION + "ean=" + ean;

                    final String url = Helper.getItemParam(Constants.URL).toString().concat(URL_MATERIAL);

                    return (WSMessage) Helper.getWebservice(url, WSMessage.class);
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
                if (results.getIdMessage() == 1) {
                    if (PARAM == 0) {
                        Toast.makeText(getApplicationContext(), results.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Gson gson = new Gson();
                        material = gson.fromJson(new Gson().toJson(results.getResult()), Material.class);
                        material.setQty(1);
                        setDataMaterial();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), results.getMessage(), Toast.LENGTH_SHORT).show();
                    progress.dismiss();
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

    private void setDataMaterial() {
        if (listMaterial == null) {
            listMaterial = new ArrayList<>();
        }
        if (listMaterial.size() == 0) {
            listMaterial.add(material);
        } else {
            int FLAG = 0;
            for (Material m : listMaterial) {
                if (m.getMaterialNumber().equals(material.getMaterialNumber())) {
                    m.setQty(m.getQty() + material.getQty());
                    FLAG = 1;
                    break;
                }
            }
            if (FLAG == 0) {
                listMaterial.add(material);
            }
        }
        Helper.setItemParam(Constants.LIST_MATERIAL, listMaterial);
        setDataItem();
        progress.dismiss();
    }

    private void setData() {
        for (Plant plant : user.getPlant()) {
            listPlant.add(plant.getId());
        }
//        listSloc = new ArrayList<>();
        for (Sloc sloc : user.getSloc()) {
            listSloc.add(sloc.getId());
        }
//        listMovTyp = new ArrayList<>();
        for (MovType movType : user.getMovType()) {
            listMovTyp.add(movType.getId());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDataItem();
        try {
            if (mBarcodeScanner != null) {
                if (mSettings == null) {
                    mSettings = mBarcodeScanner.getSettings();
                    setScanner();
                }
                mBarcodeScanner.setSettings(mSettings);
                mBarcodeScanner.claim();
            }
        } catch (BarcodeException e) {
            Log.e(TAG, "Error Code = " + e.getErrorCode(), e);
        } catch (java.lang.IllegalArgumentException e) {
            Toast toast = Toast.makeText(this, R.string.error_message_symbol_settings, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void setDataItem() {
        if (Helper.getItemParam(Constants.LIST_MATERIAL) != null) {
            binding.emptyLayout.setVisibility(View.GONE);
            binding.rcItem.setVisibility(View.VISIBLE);
            binding.imgSubmit.setVisibility(View.VISIBLE);
            listMaterial = (List<Material>) Helper.getItemParam(Constants.LIST_MATERIAL);
            if (listMaterial.size() != 0) {
                binding.rcItem.setVisibility(View.VISIBLE);
                adapter = new CreateReceivingAdapter(CreateReceivingActivity.this, listMaterial);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CreateReceivingActivity.this);
                binding.rcItem.setLayoutManager(layoutManager);
                binding.rcItem.setAdapter(adapter);
            } else {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    binding.rcItem.setVisibility(View.GONE);
                }
            }
        }else{
            binding.emptyLayout.setVisibility(View.VISIBLE);
            binding.rcItem.setVisibility(View.GONE);
            binding.imgSubmit.setVisibility(View.GONE);
        }
    }

    private void setDataScan(String scanData) {
        ean = scanData.replaceAll("[^\\x20-\\x7e]", "").replaceAll("\\s", "");
        PARAM = 1;
        new RequestUrl().execute();
        progress.show();

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
    public void onPause() {
        super.onPause();
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
                            setDataScan(this.data);
                        }
                    }.setData(data.getSymbologyDenso(), data.getData())
            );
        }
    }


}