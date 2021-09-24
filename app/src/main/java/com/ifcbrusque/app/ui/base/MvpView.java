package com.ifcbrusque.app.ui.base;

import androidx.annotation.StringRes;

public interface MvpView {
    void onError(@StringRes int resId);

    void onError(String message);

    void showMessage(String message);

    void showMessage(@StringRes int resId);

    boolean isNetworkConnected();
}
