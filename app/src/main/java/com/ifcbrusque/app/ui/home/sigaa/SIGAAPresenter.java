package com.ifcbrusque.app.ui.home.sigaa;

import com.ifcbrusque.app.data.DataManager;
import com.ifcbrusque.app.ui.base.BasePresenter;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class SIGAAPresenter<V extends SIGAAContract.SIGAAView> extends BasePresenter<V> implements SIGAAContract.SIGAAPresenter<V> {
    @Inject
    public SIGAAPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }
}