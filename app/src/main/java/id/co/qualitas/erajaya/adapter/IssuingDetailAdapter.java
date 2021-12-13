package id.co.qualitas.erajaya.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.co.qualitas.erajaya.activity.DocIssuingDetailActivity;
import id.co.qualitas.erajaya.databinding.RowViewIssuingDetailBinding;
import id.co.qualitas.erajaya.helper.Helper;
import id.co.qualitas.erajaya.model.Issuing;

public class IssuingDetailAdapter extends RecyclerView.Adapter<IssuingDetailAdapter.DataObjectHolder> {

    private List<Issuing> dataList;
    private DocIssuingDetailActivity mContext;

    public IssuingDetailAdapter(DocIssuingDetailActivity context, List<Issuing> issuingList) {
        this.dataList = issuingList;
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewIssuingDetailBinding binding;

        DataObjectHolder(RowViewIssuingDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DataObjectHolder(RowViewIssuingDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DataObjectHolder holder, final int position) {
        final Issuing data = dataList.get(position);
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

    public void filterList(ArrayList<Issuing> filteredList) {
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

