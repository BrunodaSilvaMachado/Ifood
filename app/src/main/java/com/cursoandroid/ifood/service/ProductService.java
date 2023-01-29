package com.cursoandroid.ifood.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cursoandroid.ifood.config.FirebaseConfig;
import com.cursoandroid.ifood.model.Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

public class ProductService extends DatabaseService<Product> {
    private static DatabaseReference baseRef = null;
    public ProductService(){}
    @NonNull
    @NotNull
    @Override
    protected DatabaseReference newReference() {
        if (baseRef == null) {
            baseRef = FirebaseConfig.getDatabaseReference().child("product");
        }
        return baseRef;
    }

    @Nullable
    @Override
    protected DatabaseReference newUidReference() {
        return null;
    }

    @Override
    public void finish() {
        super.finish();
        baseRef = null;
    }

    public void save(@NonNull Product product){
        DatabaseReference ref = baseRef.child(product.getIdCompany());
        super.save(ref, product);
    }

    public void delete(@NonNull Product product){
        DatabaseReference ref = baseRef.child(product.getIdCompany());
        super.delete(ref, product);
    }
}
