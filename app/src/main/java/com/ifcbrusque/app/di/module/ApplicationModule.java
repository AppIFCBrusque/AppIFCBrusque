package com.ifcbrusque.app.di.module;

import android.app.Application;
import android.content.Context;

import com.ifcbrusque.app.data.AppDataManager;
import com.ifcbrusque.app.data.DataManager;
import com.ifcbrusque.app.data.db.AppDbHelper;
import com.ifcbrusque.app.data.db.DbHelper;
import com.ifcbrusque.app.data.network.AppNetworkHelper;
import com.ifcbrusque.app.data.network.NetworkHelper;
import com.ifcbrusque.app.data.network.NetworkInterceptor;
import com.ifcbrusque.app.data.network.noticias.AppPgNoticiasHelper;
import com.ifcbrusque.app.data.network.noticias.PgNoticiasHelper;
import com.ifcbrusque.app.data.network.sigaa.AppSIGAAHelper;
import com.ifcbrusque.app.data.network.sigaa.SIGAAHelper;
import com.ifcbrusque.app.data.notification.AppNotificationHelper;
import com.ifcbrusque.app.data.notification.NotificationHelper;
import com.ifcbrusque.app.data.prefs.AppPreferencesHelper;
import com.ifcbrusque.app.data.prefs.PreferencesHelper;
import com.ifcbrusque.app.di.ApplicationContext;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
public class ApplicationModule {
    private final Application mApplication;

    public ApplicationModule(Application app) {
        mApplication = app;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    DataManager provideDataManager(AppDataManager dataManager) {
        return dataManager;
    }

    @Provides
    @Singleton
    DbHelper provideDbHelper(AppDbHelper dbHelper) {
        return dbHelper;
    }

    @Provides
    @Singleton
    NetworkHelper provideNetworkHelper(AppNetworkHelper networkHelper) {
        return networkHelper;
    }

    @Provides
    @Singleton
    PgNoticiasHelper providePgNoticiasHelper(AppPgNoticiasHelper pgNoticiasHelper) {
        return pgNoticiasHelper;
    }

    @Provides
    @Singleton
    SIGAAHelper provideSIGAAHelper(AppSIGAAHelper SIGAAHelper) {
        return SIGAAHelper;
    }

    @Provides
    @Singleton
    NotificationHelper provideNotificationHelper(AppNotificationHelper notificationHelper) {
        return notificationHelper;
    }

    @Provides
    @Singleton
    PreferencesHelper providePreferencesHelper(AppPreferencesHelper preferencesHelper) {
        return preferencesHelper;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Application app) {
        return new OkHttpClient.Builder()
                .addInterceptor(new NetworkInterceptor(app))
                .connectTimeout(200, TimeUnit.SECONDS)
                .writeTimeout(200, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    Picasso providePicasso(Application app, OkHttpClient client) {
        return new Picasso.Builder(app)
                .downloader(new OkHttp3Downloader(client))
                .build();
    }
}