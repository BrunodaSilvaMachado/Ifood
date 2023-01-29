package com.cursoandroid.ifood.dialog;

import android.content.Context;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class ProgressBarDialog {
    @NonNull
    public static AlertDialog newDialog(Context context, String title){
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setCancelable(false)
                .setView(new ProgressBar(context))
                .create();
    }
}
