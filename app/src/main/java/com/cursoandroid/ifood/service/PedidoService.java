package com.cursoandroid.ifood.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cursoandroid.ifood.config.FirebaseConfig;
import com.cursoandroid.ifood.model.Pedido;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PedidoService extends DatabaseService<Pedido> {
    private static DatabaseReference baseRef = null;
    private static DatabaseReference pedidoRef = null;

    public PedidoService(){
        if (pedidoRef == null){
            pedidoRef = FirebaseConfig.getDatabaseReference().child("pedido");
        }
    }
    @NonNull
    @NotNull
    @Override
    protected DatabaseReference newReference() {
        if (baseRef == null){
            baseRef = FirebaseConfig.getDatabaseReference().child("pedido_usuario");
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

    public void save(String idCompany, String idUsuario, Pedido pedido){
        DatabaseReference ref = baseRef.child(idCompany).child(idUsuario);
        ref.setValue(pedido);
    }

    public void delete(String idCompany, String idUsuario){
        DatabaseReference ref = baseRef.child(idCompany).child(idUsuario);
        ref.removeValue();
    }

    public void commit(Pedido pedido){
        DatabaseReference ref = pedidoRef.child(pedido.getIdCompany()).child(pedido.getId());
        ref.setValue(pedido);
    }

    public void searchDemand(String idCompany, ValueEventListener eventListener){
        DatabaseReference pRef = pedidoRef.child(idCompany);
        Query query = pRef.orderByChild("status").equalTo(Pedido.STATUS_CONFIRMADO);
        query.addValueEventListener(eventListener);
    }

    public void update(@NonNull String idCompany, @NonNull String idPedido, @NonNull String field, @NonNull Object vaule){
        Map<String, Object> mUser = new HashMap<>();
        mUser.put(field, vaule);
        this.update(pedidoRef.child(idCompany), idPedido, mUser);
    }
}
