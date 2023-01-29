package com.cursoandroid.ifood.helper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static android.app.Activity.RESULT_OK;

public class ImageCapture {
    public interface ImageCaptureListener{
        void onBitmapCapture(Bitmap bitmap);
    }
    public ImageCapture(){}

    @NonNull
    public static ActivityResultLauncher<Intent> registerCameraForActivityResult(Activity activity,
                                                                                 ImageCaptureListener imageCaptureListener){
        return ((AppCompatActivity)activity).registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        imageCaptureListener.onBitmapCapture(bitmap);
                    }
                });
    }

    @NonNull
    public static ActivityResultLauncher<String> registerGalleryForActivityResult(Activity activity,
                                                                                  ImageCaptureListener imageCaptureListener){
        return ((AppCompatActivity)activity).registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> imageCaptureListener.onBitmapCapture(getBitmapFromUri(activity, uri)));
    }

    public static Bitmap getBitmapFromUri(Activity activity, Uri uri) {
        Bitmap bitmap = null;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                ImageDecoder.Source source = ImageDecoder.createSource(activity.getContentResolver(), uri);
                bitmap = ImageDecoder.decodeBitmap(source);
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
