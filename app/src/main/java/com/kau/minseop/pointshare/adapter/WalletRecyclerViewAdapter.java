package com.kau.minseop.pointshare.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.model.WalletViewHolerModel;

import java.util.List;

/**
 * Created by minseop on 2018-06-08.
 */

public class WalletRecyclerViewAdapter extends  RecyclerView.Adapter<WalletRecyclerViewAdapter.WalletViewHoler>{

    private final List<WalletViewHolerModel> modelList;

    public WalletRecyclerViewAdapter(List<WalletViewHolerModel> modelList) {
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public WalletViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.model_wallet_viewholer, parent, false);
        return new WalletViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletViewHoler holder, int position) {
        final WalletViewHolerModel model = modelList.get(position);
        holder.txt_walletName.setText(model.getWalletName());
        holder.txt_walletBalance.setText(model.getWalletBalance());
        holder.txt_walletAddress.setText(model.getWalletAddress());
    }

    @Override
    public int getItemCount() {return modelList.size();}

    static class WalletViewHoler extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView txt_walletName, txt_walletAddress, txt_walletBalance;

        public WalletViewHoler(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.img_model_wallet_viewholder);
            txt_walletName = itemView.findViewById(R.id.txt_model_walletname);
            txt_walletAddress= itemView.findViewById(R.id.txt_model_walletaddress);
            txt_walletBalance= itemView.findViewById(R.id.txt_model_walletbalance);
        }
    }
}

