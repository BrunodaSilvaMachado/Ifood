package com.cursoandroid.ifood.activity.ui;

import androidx.annotation.Nullable;

public class ProductFormState {
    @Nullable
    private Integer nameError;
    @Nullable
    private Integer descriptionError;
    @Nullable
    private Integer priceError;
    private boolean isValidate;

    public ProductFormState(@Nullable Integer nameError, @Nullable Integer descriptionError, @Nullable Integer priceError) {
        this.nameError = nameError;
        this.descriptionError = descriptionError;
        this.priceError = priceError;
    }

    public ProductFormState(boolean isValidate) {
        this.isValidate = isValidate;
    }

    @Nullable
    public Integer getNameError() {
        return nameError;
    }

    @Nullable
    public Integer getDescriptionError() {
        return descriptionError;
    }

    @Nullable
    public Integer getPriceError() {
        return priceError;
    }

    public boolean isValidate() {
        return isValidate;
    }
}
