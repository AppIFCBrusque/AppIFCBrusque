package com.ifcbrusque.app.di.component;

import com.ifcbrusque.app.di.PerActivity;
import com.ifcbrusque.app.di.module.ActivityModule;
import com.ifcbrusque.app.ui.home.HomeActivity;
import com.ifcbrusque.app.ui.home.lembretes.LembretesFragment;
import com.ifcbrusque.app.ui.home.noticias.NoticiasFragment;
import com.ifcbrusque.app.ui.home.sigaa.SIGAAFragment;
import com.ifcbrusque.app.ui.lembrete.InserirLembreteActivity;
import com.ifcbrusque.app.ui.login.LoginActivity;
import com.ifcbrusque.app.ui.main.MainActivity;
import com.ifcbrusque.app.ui.noticia.NoticiaActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(MainActivity activity);

    void inject(HomeActivity activity);

    void inject(LembretesFragment fragment);

    void inject(InserirLembreteActivity activity);

    void inject(SIGAAFragment fragment);

    void inject(NoticiasFragment fragment);

    void inject(NoticiaActivity activity);

    void inject(LoginActivity activity);
}