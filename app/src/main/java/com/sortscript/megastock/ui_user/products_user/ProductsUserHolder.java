package com.sortscript.megastock.ui_user.products_user;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sortscript.megastock.Holder.ProductsHolder;
import com.sortscript.megastock.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductsUserHolder extends RecyclerView.ViewHolder {

    public ImageView homeImageUserIv;
    public TextView homeProductTitleUserTv, homeBrandUserTv, homePriceUserTv;
    public View view;

    public ProductsUserHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;

        homeImageUserIv = itemView.findViewById(R.id.homeImageUserIvId);
        homeProductTitleUserTv = itemView.findViewById(R.id.homeProductTitleUserTvId);
        homeBrandUserTv = itemView.findViewById(R.id.homeBrandUserTvId);
        homePriceUserTv = itemView.findViewById(R.id.homePriceUserTvId);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });
    }

    public void setUserProducts(Context context, String image, String title, String brand, String price) {

        Glide.with(view).load(image).into(homeImageUserIv);
        homeProductTitleUserTv.setText(title);
        homeBrandUserTv.setText(brand);
        homePriceUserTv.setText(price);
    }

    private ProductsUserHolder.ClickListener mClickListener;

    public interface ClickListener {

        void onItemClick(View view, int position);
    }

    public void setOnclickListener(ProductsUserHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }

}
