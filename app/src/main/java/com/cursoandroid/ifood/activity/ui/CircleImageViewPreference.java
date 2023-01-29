package com.cursoandroid.ifood.activity.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.cursoandroid.ifood.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class CircleImageViewPreference extends Preference {

    private CircleImageView imageView;
    private View.OnClickListener imageClickListener;
    public CircleImageViewPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        imageView = (CircleImageView) holder.findViewById(R.id.ivPerfil);
        imageView.setOnClickListener(imageClickListener);
    }

    public void setImageClickListener(View.OnClickListener imageClickListener) {
        this.imageClickListener = imageClickListener;
    }

    public void setBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
