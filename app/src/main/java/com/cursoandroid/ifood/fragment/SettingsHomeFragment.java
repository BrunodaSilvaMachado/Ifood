package com.cursoandroid.ifood.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import com.cursoandroid.ifood.R;
import com.cursoandroid.ifood.activity.ui.CircleImageViewPreference;
import com.cursoandroid.ifood.config.FirebaseConfig;
import com.cursoandroid.ifood.helper.ImageLifecycleObserver;
import com.cursoandroid.ifood.helper.TelphoneTextWatcher;
import com.cursoandroid.ifood.helper.UploadPhoto;
import com.cursoandroid.ifood.model.Address;
import com.cursoandroid.ifood.model.Usuario;
import com.cursoandroid.ifood.service.UsuarioService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SettingsHomeFragment extends PreferenceFragmentCompat {
    private final UsuarioService usuarioService = new UsuarioService();
    private ImageLifecycleObserver imageFromGallery;
    private CircleImageViewPreference imagePreference;
    private EditTextPreference name;
    private EditTextPreference city;
    private EditTextPreference subArea;
    private EditTextPreference thoroughfare;
    private EditTextPreference number;
    private EditTextPreference cep;
    private EditTextPreference email;
    private EditTextPreference tel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() != null){
            imageFromGallery = new ImageLifecycleObserver(getActivity(), this::onBindBitmap);
            getLifecycle().addObserver(imageFromGallery);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        usuarioService.finish();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.user_preferences, rootKey);
        imagePreference = findPreference("image_preference");
        name = findPreference("nome_usuario");
        city = findPreference("cidade");
        subArea = findPreference("bairro");
        thoroughfare = findPreference("rua");
        number = findPreference("numero");
        cep = findPreference("cep");
        email = findPreference("email");
        tel = findPreference("tel");

        fetch();
        onSetupPreference();
    }

    private void onSetupPreference(){
        if (imagePreference != null){
            imagePreference.setImageClickListener(l-> imageFromGallery.selectImage());
        }
        name.setOnPreferenceChangeListener((preference, newValue) -> {
            String oldValue = ((EditTextPreference) preference).getText();
            String userName = (String) newValue;
            if (oldValue == null || !oldValue.equals(newValue)){
                usuarioService.update(UsuarioService.getCurrentUserId(), "name", userName);
                usuarioService.updateUserName(userName);
                return true;
            }
            return false;
        });
        tel.setOnPreferenceChangeListener((preference, newValue) -> updateUserField("tel",
                ((EditTextPreference) preference).getText(), (String) newValue));
        tel.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.addTextChangedListener(new TelphoneTextWatcher(editText,
                    "(\\d{2})(\\d{5})(\\d+)", "($1) $2-$3"));
        });
        city.setOnPreferenceChangeListener((preference, newValue) -> updateUserFieldAddress("city",
                ((EditTextPreference) preference).getText(), (String) newValue));
        subArea.setOnPreferenceChangeListener((preference, newValue) -> updateUserFieldAddress("subAdmin",
                ((EditTextPreference) preference).getText(), (String) newValue));
        thoroughfare.setOnPreferenceChangeListener((preference, newValue) -> updateUserFieldAddress("thoroughfare",
                ((EditTextPreference) preference).getText(), (String) newValue));
        number.setOnPreferenceChangeListener((preference, newValue) -> updateUserFieldAddress("number",
                ((EditTextPreference) preference).getText(), (String) newValue));
        cep.setOnPreferenceChangeListener((preference, newValue) -> updateUserFieldAddress("cep",
                ((EditTextPreference) preference).getText(), (String) newValue));
    }

    private void onBindBitmap(Bitmap bitmap){
        StorageReference storageReference = FirebaseConfig.getStorageReference();
        if (bitmap != null){
            imagePreference.setBitmap(bitmap);
            //Salvar imagem no firebase
            final StorageReference imageRef = storageReference.child("imagens").child("usuarios")
                    .child(UsuarioService.getCurrentUserId() + ".jpeg");
            UploadPhoto.upload(Objects.requireNonNull(getActivity()), imageRef,
                    bitmap, task -> updateUserPhoto(task.getResult()));
        }
    }

    private void updateUserPhoto(@NonNull Uri uri) {
        UsuarioService usuarioService = new UsuarioService();
        usuarioService.updateUserPhoto(uri);
        usuarioService.update(UsuarioService.getCurrentUserId(), "foto", uri.toString());
    }

    private boolean updateUserField(String field, String oldValue, String newValue){
        if (oldValue == null || !oldValue.equals(newValue)){
            usuarioService.update(UsuarioService.getCurrentUserId(), field, newValue);
            return true;
        }
        return false;
    }
    private boolean updateUserFieldAddress(String field, String oldValue, String newValue){
        if (oldValue == null || !oldValue.equals(newValue)){
            usuarioService.updateAddress(UsuarioService.getCurrentUserId(), field, newValue);
            return true;
        }
        return false;
    }
    private void fetch(){
        usuarioService.singleTask(UsuarioService.getCurrentUserId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                if (usuario != null){
                    name.setText(usuario.getNome());
                    email.setText(usuario.getEmail());
                    tel.setText(usuario.getTel());
                    Address address = usuario.getEndereco();
                    if (address != null){
                        city.setText(address.getCity());
                        subArea.setText(address.getSubAdmin());
                        number.setText(address.getNumber());
                        cep.setText(address.getCep());
                        thoroughfare.setText(address.getThoroughfare());
                    }

                    if (usuario.getFoto() != null) {
                        Picasso.get().load(usuario.getFoto()).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                imagePreference.setBitmap(bitmap);
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {}

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {}
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
    }
}
