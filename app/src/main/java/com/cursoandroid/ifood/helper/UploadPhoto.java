package com.cursoandroid.ifood.helper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.cursoandroid.ifood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class UploadPhoto {
    public static void upload(@NonNull Activity activity, @NonNull final StorageReference imageRef,
                              @NonNull Bitmap bitmap, OnCompleteListener<Uri> onCompleteListener){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        //converter o BAOS para píxel numa matriz de ‘bytes’
        //(dados da imagem)
        byte[] dadosImagem = baos.toByteArray();
        //Retona objeto que ira controlar o upload
        UploadTask uploadTask = imageRef.putBytes(dadosImagem);
        uploadTask.addOnFailureListener(activity, exception -> Toast.makeText(activity, R.string.upload_failed
                        + exception.getMessage(), Toast.LENGTH_LONG).show())
                .addOnSuccessListener(t -> imageRef.getDownloadUrl()
                        .addOnCompleteListener(onCompleteListener));
    }
}
