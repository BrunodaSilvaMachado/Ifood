package com.cursoandroid.ifood.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import com.cursoandroid.ifood.R;
import com.cursoandroid.ifood.activity.ui.ProductViewModel;
import com.cursoandroid.ifood.databinding.ActivityAddProductBinding;
import com.cursoandroid.ifood.helper.MoneyTextWatcher;
import com.cursoandroid.ifood.model.Product;
import com.cursoandroid.ifood.service.CompanyService;
import com.cursoandroid.ifood.service.ProductService;


public class AddProductActivity extends AppCompatActivity {

    private ActivityAddProductBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupActionBar();

        EditText productName = binding.etProductNome;
        EditText productDescription = binding.etProductDescription;
        EditText productPrice = binding.etProductPrice;

        ProductViewModel productViewModel = new ViewModelProvider(this, new ProductViewModel.ProductViewModelFactory())
                .get(ProductViewModel.class);

        productViewModel.getProductFormState().observe(this, formState -> {
            if (formState == null) {
                return;
            }
            binding.btnRegister.setEnabled(formState.isValidate());
            if (formState.getNameError() != null) {
                productName.setError(getString(formState.getNameError()));
            }
            if (formState.getDescriptionError() != null) {
                productDescription.setError(getString(formState.getDescriptionError()));
            }
            if (formState.getPriceError() != null) {
                productPrice.setError(getString(formState.getPriceError()));
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
                productViewModel.productDataChanged(productName.getText().toString(), productDescription.getText().toString(), productPrice.getText().toString());
            }
        };

        productName.addTextChangedListener(afterTextChangedListener);
        productDescription.addTextChangedListener(afterTextChangedListener);
        productPrice.addTextChangedListener(afterTextChangedListener);
        productPrice.addTextChangedListener(new MoneyTextWatcher(productPrice));
        productPrice.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                register(productName.getText().toString(), productDescription.getText().toString(),
                        productPrice.getText().toString());
            }
            return false;
        });
        binding.btnRegister.setOnClickListener(l->register(productName.getText().toString(), productDescription.getText().toString(),
                productPrice.getText().toString()));
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(String.format("%s - %s", getString(R.string.app_name), getString(R.string.add)));
    }

    private void register(String pName, String pDescription, String pPrice){
        String cleanPriceString = MoneyTextWatcher.parseCurrencyValue(pPrice).toString();
        Product product = new Product(pName, pDescription, cleanPriceString);
        ProductService productService = new ProductService();
        product.setIdCompany(CompanyService.getCompanyId());
        product.setId(productService.UUID());
        productService.save(product);
        productService.finish();
        finish();
    }


}