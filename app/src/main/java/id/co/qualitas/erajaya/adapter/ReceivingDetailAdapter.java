package id.co.qualitas.erajaya.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.co.qualitas.erajaya.activity.DocReceivingDetailActivity;
import id.co.qualitas.erajaya.databinding.RowViewReceivingDetailBinding;
import id.co.qualitas.erajaya.helper.Helper;
import id.co.qualitas.erajaya.model.Receiving;

public class ReceivingDetailAdapter extends RecyclerView.Adapter<ReceivingDetailAdapter.DataObjectHolder> {

    private List<Receiving> dataList;
    private DocReceivingDetailActivity mContext;

    public ReceivingDetailAdapter(DocReceivingDetailActivity context, List<Receiving> receivingList) {
        this.dataList = receivingList;
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewReceivingDetailBinding binding;

        DataObjectHolder(RowViewReceivingDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DataObjectHolder(RowViewReceivingDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DataObjectHolder holder, final int position) {
        final Receiving data = dataList.get(position);
        holder.binding.txtIdMat.setText(data.getMaterialNumber());
        holder.binding.txtMatDesc.setText(data.getMaterialDesc());
        holder.binding.txtEan.setText(data.getEan());
        holder.binding.txtQty.setText(String.valueOf(data.getQty()));
        holder.binding.txtCreatedBy.setText("Created by: "+data.getCreatedBy());
        holder.binding.txtCreatedDate.setText(Helper.getDateFromMilliSec(data.getCreatedDate(), "dd-MM-yyyy hh:mm:ss"));
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public void filterList(ArrayList<Receiving> filteredList) {
        dataList = filteredList;
        notifyDataSetChanged();
    }

    public static void hideKeyboard(Context context, View view) {
        if ((context == null) || (view == null)) {
            return;
        }
        InputMethodManager mgr =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}

