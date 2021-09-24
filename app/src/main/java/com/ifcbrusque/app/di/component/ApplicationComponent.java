package com.ifcbrusque.app.di.component;

import android.app.Application;
import android.content.Context;

import com.ifcbrusque.app.App;
import com.ifcbrusque.app.data.DataManager;
import com.ifcbrusque.app.data.db.DbHelper;
import com.ifcbrusque.app.data.network.NetworkHelper;
import com.ifcbrusque.app.data.notification.NotificationHelper;
import com.ifcbrusque.app.data.prefs.PreferencesHelper;
import com.ifcbrusque.app.di.ApplicationContext;
import com.ifcbrusque.app.di.module.ApplicationModule;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(App application);

    @ApplicationContext
    Context getContext();

    Application getApplication();

    DataManager getDataManager();

    DbHelper getDbHelper();

    NetworkHelper getNetworkHelper();

    NotificationHelper getNotificationHelper();

    PreferencesHelper getPreferencesHelper();

    OkHttpClient getOkHttpClient();

    Picasso getPicasso();
}