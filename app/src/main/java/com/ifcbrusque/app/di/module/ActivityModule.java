package com.ifcbrusque.app.di.module;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.di.ActivityContext;
import com.ifcbrusque.app.di.PerActivity;
import com.ifcbrusque.app.ui.home.lembretes.LembretesAdapter;
import com.ifcbrusque.app.ui.home.lembretes.LembretesContract;
import com.ifcbrusque.app.ui.home.lembretes.LembretesPresenter;
import com.ifcbrusque.app.ui.home.noticias.NoticiasAdapter;
import com.ifcbrusque.app.ui.home.noticias.NoticiasContract;
import com.ifcbrusque.app.ui.home.noticias.NoticiasPresenter;
import com.ifcbrusque.app.ui.home.sigaa.SIGAAContract;
import com.ifcbrusque.app.ui.home.sigaa.SIGAAPresenter;
import com.ifcbrusque.app.ui.home.sigaa.noticias.NoticiasSIGAAAdapter;
import com.ifcbrusque.app.ui.home.sigaa.noticias.NoticiasSIGAAContract;
import com.ifcbrusque.app.ui.home.sigaa.noticias.NoticiasSIGAAPresenter;
import com.ifcbrusque.app.ui.lembrete.InserirLembreteContract;
import com.ifcbrusque.app.ui.lembrete.InserirLembretePresenter;
import com.ifcbrusque.app.ui.login.LoginContract;
import com.ifcbrusque.app.ui.login.LoginPresenter;
import com.ifcbrusque.app.ui.main.MainContract;
import com.ifcbrusque.app.ui.main.MainPresenter;
import com.ifcbrusque.app.ui.noticia.NoticiaContract;
import com.ifcbrusque.app.ui.noticia.NoticiaPresenter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class ActivityModule {
    private final AppCompatActivity mActivity;

    public ActivityModule(AppCompatActivity activity) {
        mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return mActivity;
    }

    @Provides
    AppCompatActivity provideActivity() {
        return mActivity;
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    @PerActivity
    MainContract.MainPresenter<MainContract.MainView> provideMainPresenter(MainPresenter<MainContract.MainView> presenter) {
        return presenter;
    }

    @Provides
    LembretesContract.LembretesPresenter<LembretesContract.LembretesView> provideLembretesPresenter(LembretesPresenter<LembretesContract.LembretesView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    InserirLembreteContract.InserirLembretePresenter<InserirLembreteContract.InserirLembreteView> provideInserirLembretePresenter(InserirLembretePresenter<InserirLembreteContract.InserirLembreteView> presenter) {
        return presenter;
    }

    @Provides
    SIGAAContract.SIGAAPresenter<SIGAAContract.SIGAAView> provideSIGAAPresenter(SIGAAPresenter<SIGAAContract.SIGAAView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    NoticiasSIGAAContract.NoticiasSIGAAPresenter<NoticiasSIGAAContract.NoticiasSIGAAView> provideNoticiasSIGAAPresenter(NoticiasSIGAAPresenter<NoticiasSIGAAContract.NoticiasSIGAAView> presenter) {
        return presenter;
    }

    @Provides
    NoticiasContract.NoticiasPresenter<NoticiasContract.NoticiasView> provideNoticiasPresenter(NoticiasPresenter<NoticiasContract.NoticiasView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    NoticiaContract.NoticiaPresenter<NoticiaContract.NoticiaView> provideNoticiaPresenter(NoticiaPresenter<NoticiaContract.NoticiaView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    LoginContract.LoginPresenter<LoginContract.LoginView> provideLoginPresenter(LoginPresenter<LoginContract.LoginView> presenter) {
        return presenter;
    }

    @Provides
    LembretesAdapter provideLembretesAdapter() {
        return new LembretesAdapter(mActivity, new ArrayList<>(), Lembrete.ESTADO_INCOMPLETO);
    }

    @Provides
    NoticiasSIGAAAdapter provideNoticiasSIGAAAdapter() {
        return new NoticiasSIGAAAdapter(mActivity);
    }

    @Provides
    NoticiasAdapter provideNoticiasAdapter(Picasso picasso) {
        return new NoticiasAdapter(new ArrayList<>(), picasso);
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(AppCompatActivity activity) {
        return new LinearLayoutManager(activity);
    }
}