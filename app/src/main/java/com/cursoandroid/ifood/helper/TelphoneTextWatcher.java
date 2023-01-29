package com.cursoandroid.ifood.helper;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;

public class TelphoneTextWatcher implements TextWatcher {
    private final WeakReference<EditText> editTextWeakReference;
    private final String regex;
    private final String pattern;

    public TelphoneTextWatcher(EditText editText, String regex, String pattern) {
        this.editTextWeakReference = new WeakReference<>(editText);
        this.regex = regex;
        this.pattern = pattern;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        EditText editText = editTextWeakReference.get();
        if (editText == null || editText.getText().toString().equals("")) {
            return;
        }
        editText.removeTextChangedListener(this);

        String input = editText.getText().toString();
        String formatted = input.replaceFirst(regex, pattern);

        editText.setText(formatted);
        editText.setSelection(formatted.length());
        editText.addTextChangedListener(this);
    }
}
