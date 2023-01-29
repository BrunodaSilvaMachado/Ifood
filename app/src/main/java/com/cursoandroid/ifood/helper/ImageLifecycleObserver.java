package com.cursoandroid.ifood.helper;

import android.app.Activity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

//Passível de generalização
public class ImageLifecycleObserver implements DefaultLifecycleObserver {
    private final ActivityResultRegistry mRegistry;
    private final ImageCapture.ImageCaptureListener imageCaptureListener;
    private final Activity activity;
    private ActivityResultLauncher<String> mGetContent;

    public ImageLifecycleObserver(@NonNull Activity activity, ImageCapture.ImageCaptureListener imageCaptureListener) {
        mRegistry = ((AppCompatActivity) activity).getActivityResultRegistry();
        this.imageCaptureListener = imageCaptureListener;
        this.activity = activity;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        mGetContent = mRegistry.register("ImageLifecycleObserver", owner,
                new ActivityResultContracts.GetContent(),
                uri -> imageCaptureListener.onBitmapCapture(ImageCapture.getBitmapFromUri(activity, uri)));
    }

    public void selectImage() {
        mGetContent.launch("image/*");
    }
}
