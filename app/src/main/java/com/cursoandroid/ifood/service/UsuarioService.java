package com.cursoandroid.ifood.service;

import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cursoandroid.ifood.R;
import com.cursoandroid.ifood.config.FirebaseConfig;
import com.cursoandroid.ifood.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UsuarioService extends DatabaseService<Usuario> {
    private static final String TAG = UsuarioService.class.getSimpleName();
    private static final FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
    private static DatabaseReference usuarioRef = null;

    public UsuarioService(){
    }

    @NonNull
    @Override
    protected DatabaseReference newReference() {
        if (usuarioRef == null) {
            usuarioRef = FirebaseConfig.getDatabaseReference().child("usuarios");
        }
        return usuarioRef;
    }

    @Nullable
    @Override
    protected DatabaseReference newUidReference() {
        return null;
    }

    @Override
    public void finish() {
        super.finish();
        usuarioRef = null;
    }

    @NonNull
    public static String getCurrentUserId(){
        FirebaseUser user =  auth.getCurrentUser();
        return (user != null)?user.getUid(): "unknow";
    }
    @NonNull
    public static Usuario getCurrentUser(){
        FirebaseUser user =  auth.getCurrentUser();
         if (user == null) {
             throw new RuntimeException(TAG+": user not found or disconnect");
         }
        Usuario usuario = new Usuario(
                user.getDisplayName(),
                user.getEmail(),
                null
        );
        usuario.setId(getCurrentUserId());
        Uri url = user.getPhotoUrl();
        if (url != null) {
            usuario.setFoto(url.toString());
        }
        return usuario;
    }

    public void save(@NonNull Usuario usuario){
        super.save(usuarioRef, usuario);
        updateUserName(usuario.getNome());
    }

    public void update(@NonNull Usuario usuario){
        Map<String, Object> mUser = new HashMap<>();
        mUser.put("nome", usuario.getNome());
        mUser.put("email", usuario.getEmail());
        mUser.put("foto", usuario.getFoto());
        mUser.put("tipo", usuario.getTipo());
        super.update(usuarioRef,usuario.getId(),mUser);
    }

    public void updateAddress(@NonNull  String id, @NonNull String field, String value){
        Map<String, Object> mAddr = new HashMap<>();
        mAddr.put(field, value);
        super.update(usuarioRef.child(id), "endereco", mAddr);
    }

    public void updateUserPhoto(Uri uri){
        try{
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(uri).build();
                user.updateProfile(profile).addOnCompleteListener(task->{
                    if (!task.isSuccessful()){
                        Log.d(TAG, String.valueOf(R.string.upload_failed));
                    }
                });
            }

        }catch (Exception ignored){}
    }

    public void updateUserName(String name){
        try{
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name).build();
                user.updateProfile(profile).addOnCompleteListener(task->{
                    if (!task.isSuccessful()){
                        Log.d(TAG, String.valueOf(R.string.invalid_username));
                    }
                });
            }
        }catch (Exception e){e.printStackTrace();}
    }

    public void updateUserProfile(String name, Uri photo){
        Usuario usuario = getCurrentUser();
        if (name != null) {
            usuario.setNome(name);
            updateUserName(name);
        }
        if (photo != null){
            usuario.setFoto(photo.toString());
            updateUserPhoto(photo);
        }
        update(usuario);
    }

    public void findUsers(String textQuery, ValueEventListener eventListener){
        Query query = usuarioRef.orderByChild("nome")
                .startAt(textQuery)
                .endAt(textQuery + "\uf8ff");//melhorar pesquisa no firebase
        query.addListenerForSingleValueEvent(eventListener);
    }

    public void currentUserSingleTask(ValueEventListener eventListener){
        super.singleTask(getCurrentUserId(), eventListener);
    }
}
