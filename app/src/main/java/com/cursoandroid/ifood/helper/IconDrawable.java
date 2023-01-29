package com.cursoandroid.ifood.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

public class IconDrawable {
    public static Drawable getIcon(@NonNull Context context, @DrawableRes int res, @ColorRes int color){
        Drawable icon = context.getDrawable(res);
        int intColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intColor = context.getColor(color);
        }else {
            intColor = context.getResources().getColor(color);
        }
        DrawableCompat.setTint(icon, intColor);
        icon.setBounds(0 ,0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());

        return icon;
    }
}
