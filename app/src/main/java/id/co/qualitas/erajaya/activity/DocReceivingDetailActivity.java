package id.co.qualitas.erajaya.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.co.qualitas.erajaya.adapter.ReceivingDetailAdapter;
import id.co.qualitas.erajaya.constants.Constants;
import id.co.qualitas.erajaya.databinding.ActivityDetailReceivingBinding;
import id.co.qualitas.erajaya.helper.Helper;
import id.co.qualitas.erajaya.model.MovType;
import id.co.qualitas.erajaya.model.Plant;
import id.co.qualitas.erajaya.model.Receiving;
import id.co.qualitas.erajaya.model.Sloc;
import one.util.streamex.StreamEx;

public class DocReceivingDetailActivity extends BaseActivity {
    private ActivityDetailReceivingBinding binding;
    private ReceivingDetailAdapter adapter;
    private List<Receiving> receivingArrayList;
    private List<Receiving> receivingDetailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailReceivingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        setData();
        initialize();
    }

    private void initialize() {
        Plant plant = user.getPlant().stream()
                .filter(e -> e.getId().equals(receivingDetailList.get(0).getIdPlant()))
                .findAny()
                .orElse(null);
        Sloc sloc = user.getSloc().stream()
                .filter(e -> e.getId().equals(receivingDetailList.get(0).getIdSloc()))
                .findAny()
                .orElse(null);
        MovType movType = user.getMovType().stream()
                .filter(e -> e.getId().equals(receivingDetailList.get(0).getMovementType()))
                .findAny()
                .orElse(null);
        binding.txtPlant.setText(plant != null ? plant.getId() + " - " + plant.getName() : receivingDetailList.get(0).getIdPlant());
        binding.txtMovTyp.setText(movType != null ? movType.getId() + " - " + movType.getName() : receivingDetailList.get(0).getMovementType());
        binding.txtSloc.setText(sloc != null ? sloc.getId() + " - " + sloc.getName() : receivingDetailList.get(0).getIdSloc());
        binding.txtRefDocNo.setText(receivingDetailList.get(0).getRefDocNo());
        binding.txtHeaderText.setText(receivingDetailList.get(0).getHeaderText());
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
        ArrayList<Receiving> filteredList = new ArrayList<>();
        for (Receiving item : receivingDetailList) {
            if (item.getDocNo().toLowerCase().contains(text.toLowerCase())
                    || item.getEan().toLowerCase().contains(text.toLowerCase())
                    || item.getMaterialDesc().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    private void setData() {
        receivingArrayList = (ArrayList<Receiving>) Helper.getItemParam(Constants.LIST_RECEIVING);
        String docNo = (String) Helper.getItemParam(Constants.DOC_NO);
        receivingDetailList = StreamEx.of(receivingArrayList)
                .filter(rev -> rev.getDocNo().equals(docNo)).toList();
        adapter = new ReceivingDetailAdapter(DocReceivingDetailActivity.this, receivingDetailList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DocReceivingDetailActivity.this);

        binding.rcRev.setLayoutManager(layoutManager);
        binding.rcRev.setHasFixedSize(true);
        binding.rcRev.setNestedScrollingEnabled(false);

        binding.rcRev.setAdapter(adapter);
    }

}