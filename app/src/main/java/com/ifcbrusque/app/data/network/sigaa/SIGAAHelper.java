package com.ifcbrusque.app.data.network.sigaa;

import com.stacked.sigaa_ifc.Disciplina;
import com.stacked.sigaa_ifc.Nota;
import com.stacked.sigaa_ifc.Usuario;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Observable;

public interface SIGAAHelper {
    Usuario getUsuarioSIGAA();

    Observable<Boolean> logarSIGAA(String usuario, String senha);

    Observable<ArrayList<Nota>> getNotasDisciplina(Disciplina disciplina);
}
