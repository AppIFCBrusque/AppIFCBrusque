package com.ifcbrusque.app;

import android.app.Application;
import android.content.Context;

import com.ifcbrusque.app.data.DataManager;
import com.ifcbrusque.app.di.component.ApplicationComponent;
import com.ifcbrusque.app.di.component.DaggerApplicationComponent;
import com.ifcbrusque.app.di.module.ApplicationModule;

import javax.inject.Inject;

public class App extends Application {
    private ApplicationComponent mApplicationComponent;

    @Inject
    DataManager dataManager;

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();

        mApplicationComponent.inject(this);
    }

    public ApplicationComponent getComponent(){
        return mApplicationComponent;
    }
}
