package com.ifcbrusque.app.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.ifcbrusque.app.R;

public class DialogUtils {
    private DialogUtils() {

    }

    public static TextView bsdAddDescricaoBelow(Context context, @StringRes int resId, RelativeLayout relativeLayout, View viewOnTop) {
        TextView textView = (TextView) LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog_descricao, null);
        textView.setId(View.generateViewId());
        textView.setText(resId);

        relativeLayout.addView(textView);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (viewOnTop != null) {
            params.addRule(RelativeLayout.BELOW, viewOnTop.getId());
        }
        textView.setLayoutParams(params);

        return textView;
    }

    public static TextView bsdAddOpcaoBelow(Context context, @StringRes int resId, @DrawableRes int left, RelativeLayout relativeLayout, View viewOnTop) {
        TextView textView = (TextView) LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog_opcao_clicavel, null);
        textView.setId(View.generateViewId());
        textView.setText(resId);
        textView.setCompoundDrawablesWithIntrinsicBounds(left, 0, 0, 0);

        relativeLayout.addView(textView);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, viewOnTop.getId());
        textView.setLayoutParams(params);

        return textView;
    }
}
