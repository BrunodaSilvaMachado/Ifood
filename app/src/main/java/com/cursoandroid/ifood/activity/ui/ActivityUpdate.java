package com.cursoandroid.ifood.activity.ui;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.cursoandroid.ifood.activity.CompanyActivity;
import com.cursoandroid.ifood.activity.HomeActivity;
import com.cursoandroid.ifood.model.Usuario;
import com.cursoandroid.ifood.service.UsuarioService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

public class ActivityUpdate {
    public static void updateUiWithUser(Activity activity, String userType) {
        if (userType.equals(Usuario.TYPE_COMPANY)) {
            activity.startActivity(new Intent(activity, CompanyActivity.class));
            activity.finish();
        } else {
            activity.startActivity(new Intent(activity, HomeActivity.class));
            activity.finish();
        }
    }

    public static void fetchUserType(Activity activity, TaskVoid taskVoid) {
        UsuarioService usuarioService = new UsuarioService();
        usuarioService.currentUserSingleTask(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                taskVoid.task();
                if (usuario != null) {
                    updateUiWithUser(activity, usuario.getTipo());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }

    public interface TaskVoid{
        void task();
    }
}
