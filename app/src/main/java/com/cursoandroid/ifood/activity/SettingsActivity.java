package com.cursoandroid.ifood.activity;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import com.cursoandroid.ifood.R;
import com.cursoandroid.ifood.fragment.SettingsCompanyFragment;
import com.cursoandroid.ifood.fragment.SettingsHomeFragment;
import com.cursoandroid.ifood.model.Usuario;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                startSettings((bundle.getString("TYPE").equals(Usuario.TYPE_COMPANY)) ?
                        new SettingsCompanyFragment() : new SettingsHomeFragment());
            }
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
    }

    private void startSettings(PreferenceFragmentCompat p) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, p)
                .commit();
    }
}