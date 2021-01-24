package com.sortscript.megastock.Vendor.ui_vendor.change_password;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChangePasswordViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ChangePasswordViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Change Password fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}