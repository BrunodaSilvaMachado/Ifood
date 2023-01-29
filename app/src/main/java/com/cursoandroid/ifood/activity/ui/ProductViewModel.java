package com.cursoandroid.ifood.activity.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.cursoandroid.ifood.R;
import com.cursoandroid.ifood.helper.MoneyTextWatcher;


public class ProductViewModel extends ViewModel {
    private final MutableLiveData<ProductFormState> productFormState = new MutableLiveData<>();

    ProductViewModel() {
    }

    public LiveData<ProductFormState> getProductFormState() {
        return productFormState;
    }

    public void productDataChanged(String name, String description, String price) {
        if (!isProductNameValid(name)){
            productFormState.setValue(new ProductFormState(R.string.invalid_product_name,null, null));
        }else if (!isDescriptionValid(description)){
            productFormState.setValue(new ProductFormState(null, R.string.invalid_description, null));
        } else if (!isPriceValid(price)) {
            productFormState.setValue(new ProductFormState(null, null,  R.string.invalid_price));
        } else {
            productFormState.setValue(new ProductFormState(true));
        }
    }

    private boolean isProductNameValid(String productName) {
        return productName != null && productName.trim().length() > 0;
    }

    private boolean isDescriptionValid(String description) {
        return description != null && description.trim().length() > 0;
    }

    private boolean isPriceValid(String price) {
        double d;
        try{
            d = MoneyTextWatcher.parseCurrencyValue(price).doubleValue();
        }
        catch (Exception ignored){
            d = -1;
        }
        return price != null && price.trim().length() > 0 && d >= 0;
    }

    public final static class ProductViewModelFactory implements ViewModelProvider.Factory {

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ProductViewModel.class)) {
                return (T) new ProductViewModel();
            } else {
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }
    }
}
