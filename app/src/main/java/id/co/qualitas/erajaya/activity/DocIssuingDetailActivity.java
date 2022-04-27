package id.co.qualitas.erajaya.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.co.qualitas.erajaya.adapter.IssuingDetailAdapter;
import id.co.qualitas.erajaya.constants.Constants;
import id.co.qualitas.erajaya.databinding.ActivityDetailIssuingBinding;
import id.co.qualitas.erajaya.helper.Helper;
import id.co.qualitas.erajaya.model.Issuing;
import id.co.qualitas.erajaya.model.Plant;
import one.util.streamex.StreamEx;

public class DocIssuingDetailActivity extends BaseActivity {
    private ActivityDetailIssuingBinding binding;
    private IssuingDetailAdapter adapter;
    private List<Issuing> issuingArrayList;
    private List<Issuing> issuingDetailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailIssuingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        setData();
        initialize();
    }

    private void initialize() {
        Plant plant = user.getPlant().stream()
                .filter(e -> e.getId().equals(issuingDetailList.get(0).getIdPlant()))
                .findAny()
                .orElse(null);
//        Sloc sloc = user.getSloc().stream()
//                .filter(e -> e.getId().equals(issuingDetailList.get(0).getIdSloc()))
//                .findAny()
//                .orElse(null);
//        MovType movType = user.getMovType().stream()
//                .filter(e -> e.getId().equals(issuingDetailList.get(0).getMovementType()))
//                .findAny()
//                .orElse(null);


        binding.txtPlant.setText(plant != null ? plant.getId() + " - " + plant.getName() : issuingDetailList.get(0).getIdPlant());
//        binding.txtMovTyp.setText(movType != null ? movType.getId() + " - " + movType.getName() : issuingDetailList.get(0).getMovementType());
//        binding.txtSloc.setText(sloc != null ? sloc.getId() + " - " + plant.getName() : issuingDetailList.get(0).getIdSloc());
        binding.txtRevDocNo.setText(issuingDetailList.get(0).getDocNo());
//        binding.txtRefDocNo.setText(issuingDetailList.get(0).getRefDocNo());
//        binding.txtHeaderText.setText(issuingDetailList.get(0).getHeaderText());
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

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void filter(String text) {
        ArrayList<Issuing> filteredList = new ArrayList<>();
        for (Issuing item : issuingDetailList) {
            if (item.getDocNo().toLowerCase().contains(text.toLowerCase())
                    || item.getEan().toLowerCase().contains(text.toLowerCase())
                    || item.getMaterialDesc().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    private void setData() {
        issuingArrayList = (ArrayList<Issuing>) Helper.getItemParam(Constants.LIST_RECEIVING);
        String docNo = (String) Helper.getItemParam(Constants.DOC_NO);
        issuingDetailList = StreamEx.of(issuingArrayList)
                .filter(rev -> rev.getDocNo().equals(docNo)).toList();
        adapter = new IssuingDetailAdapter(DocIssuingDetailActivity.this, issuingDetailList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DocIssuingDetailActivity.this);

        binding.rcRev.setLayoutManager(layoutManager);
        binding.rcRev.setHasFixedSize(true);
        binding.rcRev.setNestedScrollingEnabled(false);

        binding.rcRev.setAdapter(adapter);
    }

}