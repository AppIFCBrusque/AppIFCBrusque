package com.ifcbrusque.app.ui.login;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.DataManager;
import com.ifcbrusque.app.data.db.Converters;
import com.ifcbrusque.app.data.network.model.NoInternetException;
import com.ifcbrusque.app.ui.base.BasePresenter;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import timber.log.Timber;

public class LoginPresenter<V extends LoginContract.LoginView> extends BasePresenter<V> implements LoginContract.LoginPresenter<V> {
    @Inject
    public LoginPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void onEntrarClick(String usuario, String senha) {
        getMvpView().desativarBotaoEntrar();
        getMvpView().mostrarLoading();

        getCompositeDisposable().add(getDataManager().logarSIGAA(usuario, senha)
                .flatMapCompletable(logado -> {
                    getMvpView().esconderLoading();

                    if (logado) {
                        Timber.d("Logado como: %s", getDataManager().getUsuarioSIGAA().getNome());

                        return getDataManager().deletarTudoSIGAA()
                                .doOnComplete(() -> {
                                    getDataManager().setLoginSIGAA(usuario);
                                    getDataManager().setSenhaSIGAA(senha);
                                    getDataManager().setNomeDoUsuarioSIGAA(getDataManager().getUsuarioSIGAA().getNome());
                                    getDataManager().setUrlAvatarSIGAA(getDataManager().getUsuarioSIGAA().getUrlAvatar());
                                    getDataManager().setCursoSIGAA(getDataManager().getUsuarioSIGAA().getCurso());
                                    getDataManager().setPrimeiraInicializacao(false);
                                    getDataManager().setSIGAAConectado(true);
                                    getDataManager().setPrefSincronizarSIGAA(true);
                                    getDataManager().setDataUltimaSincronizacaoCompleta(Converters.fromTimestamp((long) 0));

                                    getMvpView().sair(true);
                                });
                    } else {
                        // Credenciais incorretas
                        getMvpView().setMensagemErro(R.string.sigaa_dados_invalidos);
                        getMvpView().mostrarMensagemErro();
                        getMvpView().ativarBotaoEntrar();
                        return Completable.complete();
                    }
                })
                .subscribe(() -> {
                    //
                }, erro -> {
                    Timber.d("Erro no login");
                    getMvpView().esconderLoading();

                    if (erro.getClass() == NoInternetException.class) {
                        // Sem internet
                        getMvpView().onError(R.string.erro_sem_internet);
                    } else {
                        // Erro com o SIGAA
                        getMvpView().setMensagemErro(R.string.sigaa_erro_conexao);
                        getMvpView().mostrarMensagemErro();
                    }
                }));
    }

    @Override
    public void onPularClick() {
        getDataManager().setPrimeiraInicializacao(false);
        getDataManager().setSIGAAConectado(false);
        getDataManager().setPrefSincronizarSIGAA(false);
        getMvpView().sair(false);
    }
}
