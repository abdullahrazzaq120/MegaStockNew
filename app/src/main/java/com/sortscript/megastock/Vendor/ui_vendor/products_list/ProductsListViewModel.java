package com.sortscript.megastock.Vendor.ui_vendor.products_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProductsListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ProductsListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is products_list fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}