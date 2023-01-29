package com.cursoandroid.ifood.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cursoandroid.ifood.config.FirebaseConfig;
import com.cursoandroid.ifood.model.Company;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

public class CompanyService extends DatabaseService<Company> {
    private static DatabaseReference baseRef = null;
    public CompanyService(){}
    @NonNull
    @NotNull
    @Override
    protected DatabaseReference newReference() {
        if (baseRef == null) {
            baseRef = FirebaseConfig.getDatabaseReference().child("company");
        }
        return baseRef;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    protected DatabaseReference newUidReference() {
        return null;
    }

    @Override
    public void finish() {
        super.finish();
        baseRef = null;
    }

    public void save(@NonNull @NotNull Company company) {
        super.save(baseRef, company);
    }

    public static String getCompanyId() {
        return getCurrenteAuthId();
    }

    public void findCompany(String textQuery, ValueEventListener eventListener){
        Query query = baseRef.orderByChild("name")
                .startAt(textQuery)
                .endAt(textQuery + "\uf8ff");//melhorar pesquisa no firebase
        query.addListenerForSingleValueEvent(eventListener);
    }
}
