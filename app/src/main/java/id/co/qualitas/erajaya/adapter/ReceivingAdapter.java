package id.co.qualitas.erajaya.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.co.qualitas.erajaya.activity.DocReceivingHeaderActivity;
import id.co.qualitas.erajaya.databinding.RowViewReceivingBinding;
import id.co.qualitas.erajaya.model.Receiving;

public class ReceivingAdapter extends RecyclerView.Adapter<ReceivingAdapter.DataObjectHolder> {

    private List<Receiving> dataList;
    private DocReceivingHeaderActivity mContext;

    public ReceivingAdapter(DocReceivingHeaderActivity context, List<Receiving> receivingList) {
        this.dataList = receivingList;
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewReceivingBinding binding;

        DataObjectHolder(RowViewReceivingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DataObjectHolder(RowViewReceivingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DataObjectHolder holder, final int position) {
        final Receiving data = dataList.get(position);
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

    public void filterList(ArrayList<Receiving> filteredList) {
        dataList = filteredList;
        notifyDataSetChanged();
    }


}
