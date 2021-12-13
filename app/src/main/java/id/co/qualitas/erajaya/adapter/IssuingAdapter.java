package id.co.qualitas.erajaya.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.co.qualitas.erajaya.activity.DocIssuingHeaderActivity;
import id.co.qualitas.erajaya.databinding.RowViewIssuingBinding;
import id.co.qualitas.erajaya.model.Issuing;

public class IssuingAdapter extends RecyclerView.Adapter<IssuingAdapter.DataObjectHolder> {

    private List<Issuing> dataList;
    private DocIssuingHeaderActivity mContext;

    public IssuingAdapter(DocIssuingHeaderActivity context, List<Issuing> issuingList) {
        this.dataList = issuingList;
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewIssuingBinding binding;

        DataObjectHolder(RowViewIssuingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DataObjectHolder(RowViewIssuingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DataObjectHolder holder, final int position) {
        final Issuing data = dataList.get(position);
        holder.binding.txtDocNo.setText(data.getDocNo());
        holder.binding.txtPostingDate.setText(data.getPostingDate());
        holder.binding.txtPlant.setText(data.getIdPlant());
        holder.binding.rootCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.showDetail(data.getDocNo());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public void filterList(ArrayList<Issuing> filteredList) {
        dataList = filteredList;
        notifyDataSetChanged();
    }


}
