package com.ifcbrusque.app.di.module;

import android.app.Service;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class ServiceModule {
    private final Service mService;

    public ServiceModule(Service service) {
        mService = service;
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}