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

public class HolderCart extends RecyclerView.ViewHolder {

    public ImageView cartImageIv;
    public TextView cartNameTv, cartPriceTv, cartUnitTv, cartQuantityTv, cartTotalTv;
    public Button singleCartProceedBtn, cartDeleteBtn, cartDecrementBtn, cartIncrementBtn;
    public int quantityCart = 1;
    View view;
    Context context;

    public HolderCart(@NonNull View itemView, final Context context1) {
        super(itemView);
        view = itemView;
        this.context = context1;

        cartImageIv = itemView.findViewById(R.id.cartImageId);
        cartNameTv = itemView.findViewById(R.id.cartNameId);
        cartPriceTv = itemView.findViewById(R.id.cartPriceId);
        cartUnitTv = itemView.findViewById(R.id.cartUnitId);
        cartQuantityTv = itemView.findViewById(R.id.cartQuantityId);
        cartTotalTv = itemView.findViewById(R.id.cartTotalId);
        singleCartProceedBtn = itemView.findViewById(R.id.singleCartProceedId);
        cartDeleteBtn = itemView.findViewById(R.id.cartDeleteId);
        cartDecrementBtn = itemView.findViewById(R.id.cartDecrementId);
        cartIncrementBtn = itemView.findViewById(R.id.cartIncrementId);

        cartDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onDeleteClick(view, getAdapterPosition());
            }
        });

        cartIncrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onIncrementClick(view, getAdapterPosition());
            }
        });

        cartDecrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onDecrementClick(view, getAdapterPosition());
            }
        });

        singleCartProceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onSingleProceedClick(view, getAdapterPosition());
            }
        });
    }

    public void setCartDetails(Context context, String cartName, String cartPrice, String cartImage, String cartUnit, String cartPId) {

        Glide.with(context).load(cartImage).into(cartImageIv);
        cartNameTv.setText(cartName);
        cartPriceTv.setText(cartPrice);
        cartUnitTv.setText(cartUnit);
        cartTotalTv.setText(cartPrice);
    }

    private HolderCart.ClickListener mClickListener;

    public interface ClickListener {

        void onSingleProceedClick(View view, int position);

        void onIncrementClick(View view, int position);

        void onDecrementClick(View view, int position);

        void onDeleteClick(View view, int position);
    }

    public void setOnclickListener(HolderCart.ClickListener clickListener) {
        mClickListener = clickListener;
    }

}
