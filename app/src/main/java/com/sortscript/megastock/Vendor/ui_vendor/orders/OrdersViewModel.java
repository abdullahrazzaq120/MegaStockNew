package com.sortscript.megastock.Vendor.ui_vendor.orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OrdersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public OrdersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is orders fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}