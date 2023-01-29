package com.cursoandroid.ifood.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cursoandroid.ifood.R;
import com.cursoandroid.ifood.activity.ui.SwipeRecyclerView;
import com.cursoandroid.ifood.adapter.GroupAdapter;
import com.cursoandroid.ifood.databinding.ActivityPedidosBinding;
import com.cursoandroid.ifood.dialog.ProgressBarDialog;
import com.cursoandroid.ifood.helper.MoneyTextWatcher;
import com.cursoandroid.ifood.model.Address;
import com.cursoandroid.ifood.model.ItemPedido;
import com.cursoandroid.ifood.model.Pedido;
import com.cursoandroid.ifood.service.CompanyService;
import com.cursoandroid.ifood.service.PedidoService;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PedidosActivity extends AppCompatActivity {
    private final PedidoService pedidoService = new PedidoService();
    private final List<Pedido> pedidoList = new ArrayList<>();
    private GroupAdapter<Pedido> pedidoAdapter;
    private ActivityPedidosBinding binding;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPedidosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fetchPedido();
        initRecyclerView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pedidoService.finish();
    }

    private void initRecyclerView(){
        recyclerView = binding.rvPedidos.recyclerView;
        pedidoAdapter = new GroupAdapter<>(pedidoList,l-> R.layout.adapter_pedidos, this::bindPedido);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(pedidoAdapter);
        SwipeRecyclerView.onSwipe(recyclerView, this::removePedido);
    }

    private void fetchPedido(){
        AlertDialog alert = ProgressBarDialog.newDialog(this, getString(R.string.loading));
        alert.show();
        pedidoService.searchDemand(CompanyService.getCompanyId(),new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                pedidoList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    pedidoList.add(ds.getValue(Pedido.class));
                }
                pedidoAdapter.notifyDataSetChanged();
                alert.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
    }

    /**
     * TODO: ao clicar no telefone direcionar para o app Telefone (o app OLX tem esse código). Ao clicar no endereço abrir o map para navegação (o app UBER tem esse código).
     *
     */

    private void bindPedido(Pedido p, View v){
        TextView nome = v.findViewById(R.id.textPedidoNome);
        TextView tel = v.findViewById(R.id.textPedidoTel);
        TextView addr = v.findViewById(R.id.textPedidoEndereco);
        TextView pgto = v.findViewById(R.id.textPedidoPgto);
        TextView obs = v.findViewById(R.id.textPedidoObs);
        TextView itens = v.findViewById(R.id.textPedidoItens);

        NumberFormat nf = MoneyTextWatcher.numberFormat;
        Address address = p.getAddress();
        double precoTotalCarrinho = 0;
        int numeroItem = 1;
        int methodPay = p.getMetodoPagamento();
        String addressFull = String.format(Locale.getDefault(), "%s: %s %s, %s, %s %s, %s, %s %s",
                getString(R.string.address), getString(R.string.thoroughfare), address.getThoroughfare(),
                address.getNumber(), getString(R.string.sub_area), address.getSubAdmin(), address.getCity(),
                getString(R.string.zip_code), address.getCep());
        StringBuilder descricaoItem = new StringBuilder();
        for (ItemPedido i : p.getItemPedidos()) {
            int qnt = i.getQuandtidade();
            Double preco = i.getPreco();
            precoTotalCarrinho += (qnt * preco);
            String productName = i.getNome();
            descricaoItem.append(numeroItem).append(")").append(productName).append(" / (").append(qnt).append("x")
                    .append(nf.format(preco)).append(") \n");
            numeroItem++;
        }
        descricaoItem.append("Total: ").append(nf.format(precoTotalCarrinho));

        nome.setText(p.getUsername());
        tel.setText(String.format(Locale.getDefault(),"%s: %s",getString(R.string.tel), p.getTel()));
        pgto.setText(String.format(Locale.getDefault(),"%s: %s",getString(R.string.payment),
                getResources().getStringArray(R.array.payments)[methodPay]));
        addr.setText(addressFull);
        obs.setText(String.format(Locale.getDefault(), "%s: %s",getString(R.string.description), p.getObservacao()));
        itens.setText(descricaoItem.toString());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void removePedido(RecyclerView.ViewHolder viewHolder) {
        final int position = viewHolder.getAdapterPosition();
        final Pedido pedido = pedidoList.get(position);
        final boolean[] removido = {true};
        pedidoList.remove(position);
        pedidoAdapter.notifyItemRemoved(position);
        Snackbar.make(recyclerView, pedido.getUsername(), Snackbar.LENGTH_LONG).setAction(R.string.undo,
                v -> {
                    pedidoList.add(position, pedido);
                    pedidoAdapter.notifyItemInserted(position);
                    removido[0] = false;
                }).show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (removido[0]) {
                pedidoService.update(CompanyService.getCompanyId(),pedido.getId(),"status",Pedido.STATUS_FINALIZADO);
            }
        }, 2750);
    }
}