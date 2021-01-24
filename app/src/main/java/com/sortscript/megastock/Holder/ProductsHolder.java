package com.sortscript.megastock.Holder;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sortscript.megastock.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductsHolder extends RecyclerView.ViewHolder {

    public ImageView homeImageIv;
    public TextView homeNameTv, homeCostPriceTv, homeQuantityTv, homeDescriptionTv, homeSalePriceTv;
    public Button homeDeleteBtn;
    View view;

    public ProductsHolder(@NonNull View itemView) {
        super(itemView);

        view = itemView;

        homeImageIv = itemView.findViewById(R.id.admin_homeImageId);
        homeDescriptionTv = itemView.findViewById(R.id.admin_homeDescriptionId);
        homeNameTv = itemView.findViewById(R.id.admin_homeNameId);
        homeCostPriceTv = itemView.findViewById(R.id.admin_homeCostPriceId);
        homeQuantityTv = itemView.findViewById(R.id.admin_homeQuantityId);
        homeSalePriceTv = itemView.findViewById(R.id.admin_homeSalePriceId);
        homeDeleteBtn = itemView.findViewById(R.id.admin_homeDeleteBtnId);

        homeDeleteBtn.setOnClickListener(v -> mClickListener.onDeleteClick(v, getAdapterPosition()));

        itemView.setOnClickListener(view -> mClickListener.onItemClick(view, getAdapterPosition()));
    }

    public void setAdminProductDetails(Context context, String title, String costPrice, String image,
                                       String description, String salePrice, String stock) {

        Glide.with(view).load(image).into(homeImageIv);
        homeNameTv.setText(title);
        homeDescriptionTv.setText(description);
        homeCostPriceTv.setText(costPrice);
        homeSalePriceTv.setText(salePrice);
        homeQuantityTv.setText(stock);
    }

    private ProductsHolder.ClickListener mClickListener;

    public interface ClickListener {

        void onDeleteClick(View view, int position);

        void onItemClick(View view, int position);
    }

    public void setOnclickListener(ProductsHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }

}
