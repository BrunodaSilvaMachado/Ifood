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
import com.cursoandroid.ifood.helper.MoneyTextWatcher;
import com.cursoandroid.ifood.helper.UploadPhoto;
import com.cursoandroid.ifood.model.Company;
import com.cursoandroid.ifood.service.CompanyService;
import com.cursoandroid.ifood.service.UsuarioService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SettingsCompanyFragment extends PreferenceFragmentCompat {
    private final CompanyService companyService = new CompanyService();
    private ImageLifecycleObserver imageFromGallery;
    private CircleImageViewPreference imagePreference;
    private EditTextPreference name;
    private EditTextPreference category;
    private EditTextPreference time;
    private EditTextPreference tax;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            imageFromGallery = new ImageLifecycleObserver(getActivity(), this::onBindBitmap);
            getLifecycle().addObserver(imageFromGallery);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        companyService.finish();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.company_preferences, rootKey);
        imagePreference = findPreference("image_preference");
        name = findPreference("nome_empresa");
        category = findPreference("categoria");
        time = findPreference("tempo_entrega");
        tax = findPreference("taxa_entrega");
        fetch();
        onSetupPreference();
    }

    private void onSetupPreference() {
        if (imagePreference != null) {
            imagePreference.setImageClickListener(l -> imageFromGallery.selectImage());
        }
        name.setOnPreferenceChangeListener((preference, newValue) -> updateCompanyField("name",
                ((EditTextPreference) preference).getText(), (String) newValue));

        category.setOnPreferenceChangeListener((preference, newValue) -> updateCompanyField("category",
                ((EditTextPreference) preference).getText(), (String) newValue));

        time.setOnPreferenceChangeListener((preference, newValue) -> updateCompanyField("deliveryTime",
                ((EditTextPreference) preference).getText(), (String) newValue));

        tax.setOnPreferenceChangeListener((preference, newValue) -> {
            String oldValue = ((EditTextPreference) preference).getText();
            String deliveryTax = (String) newValue;
            if (oldValue == null || !oldValue.equals(deliveryTax)) {
                deliveryTax = (deliveryTax.isEmpty()) ? "0" : MoneyTextWatcher.parseCurrencyValue(deliveryTax).toString();
                companyService.update(CompanyService.getCompanyId(), "deliveryTax", deliveryTax);
                return true;
            }
            return false;
        });
        tax.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editText.addTextChangedListener(new MoneyTextWatcher(editText));
        });
    }

    private void onBindBitmap(Bitmap bitmap) {
        StorageReference storageReference = FirebaseConfig.getStorageReference();
        if (bitmap != null) {
            imagePreference.setBitmap(bitmap);
            //Salvar imagem no firebase
            final StorageReference imageRef = storageReference.child("imagens").child("empresas")
                    .child(CompanyService.getCompanyId() + ".jpeg");
            UploadPhoto.upload(Objects.requireNonNull(getActivity()), imageRef,
                    bitmap, task -> updateUserPhoto(task.getResult()));
        }
    }

    private void updateUserPhoto(@NonNull Uri uri) {
        UsuarioService usuarioService = new UsuarioService();
        usuarioService.updateUserPhoto(uri);
        companyService.update(CompanyService.getCompanyId(), "urlPhoto", uri.toString());
    }

    private boolean updateCompanyField(String field, String oldValue, String newValue) {
        if (oldValue == null || !oldValue.equals(newValue)) {
            companyService.update(CompanyService.getCompanyId(), field, newValue);
            return true;
        }
        return false;
    }

    private void fetch() {
        companyService.singleTask(CompanyService.getCompanyId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Company company = snapshot.getValue(Company.class);
                if (company != null) {
                    name.setText(company.getName());
                    category.setText(company.getCategory());
                    time.setText(company.getDeliveryTime());
                    tax.setText(MoneyTextWatcher.numberFormat.format(Double.parseDouble(company.getDeliveryTax())));
                    if (company.getUrlPhoto() != null) {
                        Picasso.get().load(company.getUrlPhoto()).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                imagePreference.setBitmap(bitmap);
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }
}