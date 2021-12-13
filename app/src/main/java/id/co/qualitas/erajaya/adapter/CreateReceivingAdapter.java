package id.co.qualitas.erajaya.adapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.co.qualitas.erajaya.activity.CreateReceivingActivity;
import id.co.qualitas.erajaya.databinding.RowViewItemReceivingBinding;
import id.co.qualitas.erajaya.model.Material;


public class CreateReceivingAdapter extends RecyclerView.Adapter<CreateReceivingAdapter.DataObjectHolder> {

    private List<Material> dataList;
    private CreateReceivingActivity mContext;

    public CreateReceivingAdapter(CreateReceivingActivity context, List<Material> listMaterial) {
        this.dataList = listMaterial;
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewItemReceivingBinding binding;

        DataObjectHolder(RowViewItemReceivingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DataObjectHolder(RowViewItemReceivingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DataObjectHolder holder, final int position) {
        final Material data = dataList.get(position);
        holder.binding.txtIdMat.setText(data.getMaterialNumber());
        holder.binding.txtMatDesc.setText(data.getMaterialDesc());
        holder.binding.txtEan.setText(data.getEan());
        holder.binding.edtQty.setText(String.valueOf(data.getQty()));
        holder.binding.edtQty.setTag(position);
        holder.binding.edtQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()){
                    dataList.get((Integer) holder.binding.edtQty.getTag()).setQty(Integer.parseInt(editable.toString()));
                }
            }
        });

        holder.binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.removeItem(dataList.get((Integer) holder.binding.edtQty.getTag()), (Integer) holder.binding.edtQty.getTag());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public void filterList(List<Material> filteredList) {
        dataList = filteredList;
        notifyDataSetChanged();
    }


}
