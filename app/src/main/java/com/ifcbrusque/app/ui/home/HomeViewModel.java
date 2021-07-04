package com.ifcbrusque.app.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    /*public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }*/

    public LiveData<String> getText() {
        if (mText == null) {
            mText = new MutableLiveData<>();
            mText.setValue("This is home fragment");
        }

        return mText;
    }
    public void setmText(String mText) {
        if (this.mText == null) {
            this.mText = new MutableLiveData<>();
            //this.mText.setValue("This is home fragment");
        }
        this.mText.setValue(mText);
    }
}