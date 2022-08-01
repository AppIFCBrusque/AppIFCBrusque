package com.ifcbrusque.app.ui.home.sigaa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.imageview.ShapeableImageView;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.di.component.ActivityComponent;
import com.ifcbrusque.app.ui.base.BaseFragment;
import com.ifcbrusque.app.ui.home.HomeActivity;
import com.ifcbrusque.app.ui.home.sigaa.noticias.NoticiasSIGAAActivity;
import com.squareup.picasso.Picasso;
import com.winterhazel.sigaaforkotlin.entities.Disciplina;

import java.util.List;

import javax.inject.Inject;

public class SIGAAFragment extends BaseFragment implements SIGAAContract.SIGAAView {
    @Inject
    SIGAAContract.SIGAAPresenter<SIGAAContract.SIGAAView> mPresenter;
    @Inject
    Picasso mPicasso;
    private TextView mTvNome, mTvCurso;
    private ShapeableImageView mIvAvatarSIGAA;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sigaa, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            mPresenter.onAttach(this);
        }

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Configuração da toolbar
        ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setElevation(0);
        actionBar.setTitle(R.string.title_sigaa);
        ImageButton ibFiltros = getBaseActivity().findViewById(R.id.image_button_filtros);
        ibFiltros.setVisibility(View.GONE);
    }

    @Override
    protected void setUp() {
        mTvNome = getView().findViewById(R.id.sigaa_nome);
        mTvCurso = getView().findViewById(R.id.sigaa_curso);
        mIvAvatarSIGAA = getView().findViewById(R.id.sigaa_avatar);
        mPresenter.onViewPronta();
    }

    @Override
    public void addMenuItems(List<Disciplina> disciplinas) {
        // Adicionar itens do menu
        FlexboxLayout flexboxLayout = getView().findViewById(R.id.sigaa_flexbox_layout);

        TextView tvMenu = getView().findViewById(R.id.sigaa_menu_titulo);
        CardView itemMenu = getView().findViewById(R.id.sigaa_item_menu); // View utilizada como base
        CardView itemDisciplina = getView().findViewById(R.id.sigaa_item_disciplina); // View utilizada como base

        CardView noticiasCardView = (CardView) LayoutInflater.from(getContext()).inflate(R.layout.item_menu_sigaa, null);
        noticiasCardView.setId(View.generateViewId());
        ((TextView) noticiasCardView.findViewById(R.id.item_menu_titulo)).setText(R.string.title_noticias);
        ((ImageView) noticiasCardView.findViewById(R.id.item_menu_imagem)).setImageDrawable(getContext().getDrawable(R.drawable.outline_announcement_black_24));
        noticiasCardView.setOnClickListener(v -> startActivity(NoticiasSIGAAActivity.getStartIntent(getContext())));
        flexboxLayout.addView(noticiasCardView, itemMenu.getLayoutParams());

        // Adicionar disciplinas
        TextView tvDisciplinas = new TextView(getContext());
        tvDisciplinas.setId(View.generateViewId());
        tvDisciplinas.setText(R.string.sigaa_disciplinas);
        tvDisciplinas.setTextAppearance(R.style.TextAppearance_TituloItemLista);
        flexboxLayout.addView(tvDisciplinas, tvMenu.getLayoutParams());

        for (Disciplina disciplina : disciplinas) {
            CardView disciplinaCardView = (CardView) LayoutInflater.from(getContext()).inflate(R.layout.item_disciplina_sigaa, null);
            disciplinaCardView.setId(View.generateViewId());
            ((TextView) disciplinaCardView.findViewById(R.id.item_menu_titulo)).setText(disciplina.getNome());
            // noticiasCardView.setOnClickListener(v -> startActivity(NoticiasSIGAAActivity.getStartIntent(getContext())));
            flexboxLayout.addView(disciplinaCardView, itemDisciplina.getLayoutParams());
        }

        flexboxLayout.removeView(itemMenu); // Remover a view base
        flexboxLayout.removeView(itemDisciplina);
    }

    @Override
    public void setNomeText(String string) {
        mTvNome.setText(string);
    }

    @Override
    public void setCursoText(String string) {
        mTvCurso.setText(string);
    }

    @Override
    public void setAvatarSIGAA(String url) {
        mPicasso.load(url)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.no_picture)
                .into(mIvAvatarSIGAA);
    }
}