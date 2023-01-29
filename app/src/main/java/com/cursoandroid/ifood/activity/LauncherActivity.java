package com.cursoandroid.ifood.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import com.cursoandroid.ifood.activity.ui.ActivityUpdate;
import com.cursoandroid.ifood.config.FirebaseConfig;
import com.google.firebase.auth.FirebaseUser;

public class LauncherActivity extends AppCompatActivity {
    private boolean isReady = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        checkAuth();
        splashScreen.setKeepOnScreenCondition(() -> !isReady);
    }

    private void checkAuth(){
        FirebaseUser user = FirebaseConfig.getFirebaseAuth().getCurrentUser();
        if (user != null) {
            ActivityUpdate.fetchUserType(LauncherActivity.this, ()->isReady = true);
        }else{
            isReady = true;
            startActivity(new Intent(LauncherActivity.this, AutenticacaoActivity.class));
            finish();
        }
    }
}