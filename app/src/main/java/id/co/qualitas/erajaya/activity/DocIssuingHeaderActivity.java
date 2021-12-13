package id.co.qualitas.erajaya.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import id.co.qualitas.erajaya.adapter.IssuingAdapter;
import id.co.qualitas.erajaya.constants.Constants;
import id.co.qualitas.erajaya.databinding.ActivityHeaderIssuingBinding;
import id.co.qualitas.erajaya.helper.Helper;
import id.co.qualitas.erajaya.model.Issuing;
import id.co.qualitas.erajaya.model.WSMessage;
import one.util.streamex.StreamEx;

public class DocIssuingHeaderActivity extends BaseActivity {
    private ActivityHeaderIssuingBinding binding;
    private ArrayList<Issuing> issuingArrayList;
    private ArrayList<Issuing> issuingHeaderList;
    private IssuingAdapter issuingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHeaderIssuingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.removeItemParam(Constants.LIST_MATERIAL);
                Intent intent = new Intent(DocIssuingHeaderActivity.this, CreateIssuingActivity.class);
                startActivity(intent);
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        initialize();
    }

    private void initialize() {
        binding.txtName.setText(user.getUser().getFullname());

        binding.edtSearchRev.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }

    private void filter(String text) {
        ArrayList<Issuing> filteredList = new ArrayList<>();
        for (Issuing item : issuingArrayList) {
            if (item.getDocNo().toLowerCase().contains(text.toLowerCase())
                    || item.getPostingDate().toLowerCase().contains(text.toLowerCase())
                    || item.getIdPlant().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        issuingAdapter.filterList(filteredList);
    }

    public void showDetail(String docNo) {
        Helper.setItemParam(Constants.LIST_RECEIVING, issuingArrayList);
        Helper.setItemParam(Constants.DOC_NO, docNo);
        Intent intent = new Intent(DocIssuingHeaderActivity.this, DocIssuingDetailActivity.class);
        startActivity(intent);
    }

    private class RequestUrl extends AsyncTask<Void, Void, WSMessage> {

        @Override
        protected WSMessage doInBackground(Void... voids) {
            try {

                String URL_ISSUING = Constants.API_GET_ISSUING;

                final String url = Helper.getItemParam(Constants.URL).toString().concat(URL_ISSUING);

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
                    Type userListType = new TypeToken<ArrayList<Issuing>>(){}.getType();
                    issuingArrayList = gson.fromJson(new Gson().toJson(results.getResult()), userListType);
                    setData();
                    progress.dismiss();
                }else{
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

    private void setData() {
        if(issuingArrayList != null && issuingArrayList.size() != 0) {
            binding.clIssuing.setVisibility(View.VISIBLE);
            binding.emptyLayout.setVisibility(View.GONE);
            issuingHeaderList = (ArrayList<Issuing>) StreamEx.of(issuingArrayList)
                    .distinct(Issuing::getDocNo)
                    .toList();

            issuingAdapter = new IssuingAdapter(DocIssuingHeaderActivity.this, issuingHeaderList);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DocIssuingHeaderActivity.this);

            binding.rcRev.setLayoutManager(layoutManager);

            binding.rcRev.setAdapter(issuingAdapter);
        }else{
            binding.clIssuing.setVisibility(View.GONE);
            binding.emptyLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new RequestUrl().execute();
        progress.show();
    }
}