package com.cursoandroid.ifood.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.cursoandroid.ifood.R;
import com.cursoandroid.ifood.activity.ui.ActivityUpdate;
import com.cursoandroid.ifood.activity.ui.AutenticacaoViewModel;
import com.cursoandroid.ifood.config.FirebaseConfig;
import com.cursoandroid.ifood.databinding.ActivityAutenticacaoBinding;
import com.cursoandroid.ifood.model.Company;
import com.cursoandroid.ifood.model.Usuario;
import com.cursoandroid.ifood.service.CompanyService;
import com.cursoandroid.ifood.service.UsuarioService;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.*;

import java.util.Objects;

public class AutenticacaoActivity extends AppCompatActivity {
    private final FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
    private final UsuarioService usuarioService = new UsuarioService();
    private ActivityAutenticacaoBinding binding;
    private ProgressBar progressBar;
    private Switch tipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAutenticacaoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextInputLayout nameLayout = binding.textInputLayoutName;
        EditText campoNome = binding.etAutenticacaoNome;
        EditText campoEmail = binding.etAutenticacaoEmail;
        EditText campoSenha = binding.etAutenticacaoPassword;
        Button btnAcesso = binding.btnAcesso;
        final Switch tipoAcesso = binding.swAccess;
        tipoUsuario = binding.swType;
        final TextView tvUser = binding.tvUser;
        final TextView tvCompany = binding.tvCompany;
        progressBar = binding.progressBarAutenticacao;
        progressBar.setVisibility(View.GONE);

        AutenticacaoViewModel autenticacaoViewModel = new ViewModelProvider(this, new AutenticacaoViewModel.AutenticacaoViewModelFactory())
                .get(AutenticacaoViewModel.class);

        autenticacaoViewModel.getAutenticacaoFormState().observe(this, formState -> {
            if (formState == null) {
                return;
            }
            btnAcesso.setEnabled(formState.isDataValid());
            if (formState.getUsernameError() != null) {
                campoNome.setError(getString(formState.getUsernameError()));
            }
            if (formState.getEmailError() != null) {
                campoEmail.setError(getString(formState.getEmailError()));
            }
            if (formState.getPasswordError() != null) {
                campoSenha.setError(getString(formState.getPasswordError()));
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                autenticacaoViewModel.autenticacaoDataChanged(campoNome.getText().toString(), campoEmail.getText().toString(), campoSenha.getText().toString());
            }
        };
        campoNome.addTextChangedListener(afterTextChangedListener);
        campoEmail.addTextChangedListener(afterTextChangedListener);
        campoSenha.addTextChangedListener(afterTextChangedListener);
        campoSenha.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                access(campoNome.getText().toString(), campoEmail.getText().toString(),
                        campoSenha.getText().toString(),tipoAcesso.isChecked());
            }
            return false;
        });

        btnAcesso.setOnClickListener(view -> access(campoNome.getText().toString(),
                campoEmail.getText().toString(), campoSenha.getText().toString(),tipoAcesso.isChecked()));
        tipoUsuario.setContentDescription("switch user or company");
        tipoAcesso.setContentDescription("switch login or register");
        tipoAcesso.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                nameLayout.setVisibility(View.VISIBLE);
                campoNome.setText("");
                tipoUsuario.setVisibility(View.VISIBLE);
                tvUser.setVisibility(View.VISIBLE);
                tvCompany.setVisibility(View.VISIBLE);
            }
            else {
                campoNome.setText(R.string.name);
                nameLayout.setVisibility(View.GONE);
                tipoUsuario.setVisibility(View.GONE);
                tvUser.setVisibility(View.GONE);
                tvCompany.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        usuarioService.finish();
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void accessFailed(@NonNull Task<AuthResult> task){
        String excecao;
        try {
            throw Objects.requireNonNull(task.getException());
        }catch (FirebaseAuthInvalidCredentialsException ignored){
            excecao = getString(R.string.invalid_credentials);
        }catch (FirebaseAuthInvalidUserException | FirebaseAuthUserCollisionException ignored){
            excecao = getString(R.string.invalid_username);
        } catch (Exception e){
            excecao = getString(R.string.login_failed) + ": " + e.getMessage();
            e.printStackTrace();
        }
        showLoginFailed(excecao);
    }

    private void access(String name, String email, String password, boolean isRegister) {
        final Usuario usuario = new Usuario(name, email, password);
        progressBar.setVisibility(View.VISIBLE);

        if (isRegister){
            auth.createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha())
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()){
                            String utype = checkUserType(tipoUsuario.isChecked());
                            usuario.setId(Objects.requireNonNull(task.getResult().getUser()).getUid());
                            usuario.setTipo(utype);
                            usuarioService.save(usuario);
                            if (utype.equals(Usuario.TYPE_COMPANY)){
                                CompanyService companyService = new CompanyService();
                                Company company = new Company(usuario.getId(),null,usuario.getNome(),
                                        "","0","0");
                                companyService.save(company);
                            }
                            ActivityUpdate.updateUiWithUser(AutenticacaoActivity.this, usuario.getTipo());
                        }else {
                            accessFailed(task);
                        }
                    });
        }else{
            auth.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            ActivityUpdate.fetchUserType(AutenticacaoActivity.this, ()->progressBar.setVisibility(View.GONE));
                        }else {
                            accessFailed(task);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private String checkUserType(boolean isCompany){
        return (isCompany)?Usuario.TYPE_COMPANY:Usuario.TYPE_USER;
    }
}