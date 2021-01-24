package com.sortscript.megastock.Vendor.ui_vendor.add_products;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddProductsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AddProductsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is add_products fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}