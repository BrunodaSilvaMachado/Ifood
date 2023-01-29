package com.cursoandroid.ifood.activity.ui;

import android.util.Patterns;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.cursoandroid.ifood.R;


public class AutenticacaoViewModel extends ViewModel {
    private final MutableLiveData<FormState> autenticacaoFormState = new MutableLiveData<>();

    AutenticacaoViewModel() {
    }

    public LiveData<FormState> getAutenticacaoFormState() {
        return autenticacaoFormState;
    }

    public void autenticacaoDataChanged(String name, String email, String password) {
        if (!isUserNameValid(name)){
            autenticacaoFormState.setValue(new FormState(R.string.invalid_username,null, null));
        }else if (!isEmailValid(email)){
            autenticacaoFormState.setValue(new FormState(null, R.string.invalid_email, null));
        } else if (!isPasswordValid(password)) {
            autenticacaoFormState.setValue(new FormState(null, null,  R.string.invalid_password));
        } else {
            autenticacaoFormState.setValue(new FormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        return username != null && username.trim().length() > 0;
    }

    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        if (email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return !email.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    public final static class AutenticacaoViewModelFactory implements ViewModelProvider.Factory {

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(AutenticacaoViewModel.class)) {
                return (T) new AutenticacaoViewModel();
            } else {
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }
    }
}
