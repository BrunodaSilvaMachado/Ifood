package com.cursoandroid.ifood.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cursoandroid.ifood.R;
import com.cursoandroid.ifood.adapter.GroupAdapter;
import com.cursoandroid.ifood.databinding.ActivityCardapioBinding;
import com.cursoandroid.ifood.dialog.ProgressBarDialog;
import com.cursoandroid.ifood.helper.MoneyTextWatcher;
import com.cursoandroid.ifood.model.*;
import com.cursoandroid.ifood.service.PedidoService;
import com.cursoandroid.ifood.service.ProductService;
import com.cursoandroid.ifood.service.UsuarioService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CardapioActivity extends AppCompatActivity {
    private final ProductService productService = new ProductService();
    private final UsuarioService usuarioService = new UsuarioService();
    private final PedidoService pedidoService = new PedidoService();
    private final List<Product> produtoList = new ArrayList<>();
    private AlertDialog alertDialog;
    private List<ItemPedido> carrinho = new ArrayList<>();
    private ActivityCardapioBinding binding;
    private GroupAdapter<Product> produtoAdapter;
    private Company company;
    private Usuario usuario;
    private Pedido pedidoRecuperado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCardapioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (Build.VERSION.SDK_INT >= 33) {
                company = bundle.getParcelable("COMPANY", Company.class);
            } else {
                company = bundle.getParcelable("COMPANY");
            }
        }
        initComponents(company);
        initRecyclerView();
        fectchProduct();
        fectchUser();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        productService.finish();
        pedidoService.finish();
        usuarioService.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cardapio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.item_confirm) {
            confirmarPedido();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = binding.rvCardapio.recyclerView;
        produtoAdapter = new GroupAdapter<>(produtoList, l -> R.layout.adapter_product, (p, v) -> {
            TextView nome = v.findViewById(R.id.textNomeRefeicao);
            TextView desc = v.findViewById(R.id.textDescricaoRefeicao);
            TextView preco = v.findViewById(R.id.textPreco);

            nome.setText(p.getName());
            desc.setText(p.getDescription());
            preco.setText(MoneyTextWatcher.numberFormat.format(Double.parseDouble(p.getPrice())));
        });
        produtoAdapter.setOnItemClickListener(this::confirmarQuantidade);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(produtoAdapter);
    }

    private void initComponents(Company company) {
        binding.tvNameCompany.setText(company.getName());
        binding.tvCategory.setText(company.getCategory());
        if (company.getUrlPhoto() != null) {
            Picasso.get().load(company.getUrlPhoto()).into(binding.ivCompany);
        }
    }

    private void addressNotFoundMessage(){
        Toast.makeText(CardapioActivity.this, R.string.address_not_found, Toast.LENGTH_LONG).show();
    }

    private void fectchProduct() {
        productService.fetch(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                produtoList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    produtoList.add(ds.getValue(Product.class));
                }
                produtoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        }, company.getId());
    }

    private void fectchUser() {
        alertDialog = ProgressBarDialog.newDialog(this, getString(R.string.loading));
        alertDialog.show();
        usuarioService.currentUserSingleTask(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                usuario = snapshot.getValue(Usuario.class);
                if(usuario == null || usuario.getEndereco() == null){
                    addressNotFoundMessage();
                }
                fetchPedido();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }

    private void fetchPedido() {
        pedidoService.fetch(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                pedidoRecuperado = snapshot.getValue(Pedido.class);
                carrinho.clear();
                if (pedidoRecuperado != null) {
                    carrinho = pedidoRecuperado.getItemPedidos();
                }
                calcularPreco();
                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        }, company.getId(), usuario.getId());
    }

    private void calcularPreco(){
        int qntTotalCarrinho = 0;
        double precoTotalCarrinho = 0.0;
        for (ItemPedido i : carrinho) {
            int qnt = i.getQuandtidade();
            Double preco = i.getPreco();
            precoTotalCarrinho += (qnt * preco);
            qntTotalCarrinho += qnt;
        }
        updateUiPrice(qntTotalCarrinho, precoTotalCarrinho);
    }
    private void updateUiPrice(int qntTotalCarrinho, double precoTotalCarrinho){
        binding.tvQtd.setText(String.valueOf(qntTotalCarrinho));
        binding.tvPreco.setText(MoneyTextWatcher.numberFormat.format(precoTotalCarrinho));
    }
    private void salvarPedido(){
        if (pedidoRecuperado == null) {
            pedidoRecuperado = new Pedido(usuario.getId(), company.getId());
            pedidoRecuperado.setId(pedidoService.UUID());
        }
        pedidoRecuperado.setUsername(usuario.getNome());
        pedidoRecuperado.setTel(usuario.getTel());
        pedidoRecuperado.setAddress(usuario.getEndereco());
        pedidoRecuperado.setItemPedidos(carrinho);
        if (pedidoRecuperado.getAddress() == null){
            addressNotFoundMessage();
            return;
        }
        pedidoService.save(company.getId(), usuario.getId(), pedidoRecuperado);
    }
    public void verCarrinho(View v){
        List<String> itens = new ArrayList<>();
        boolean[] itemSelecionado = new boolean[carrinho.size()];
        List<ItemPedido> itemConfirmado = new ArrayList<>();

        for (int i = 0; i < carrinho.size(); i++){
            itens.add(String.format(Locale.getDefault(),"%s %d - %s: %s",getString(R.string.qtd), carrinho.get(i).getQuandtidade(),
                    getString(R.string.name), carrinho.get(i).getNome()));
            itemSelecionado[i] = true;
        }
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMultiChoiceItems(itens.toArray(new String[0]),itemSelecionado, (d, w, isChecked) -> itemSelecionado[w] = isChecked)
                .setPositiveButton(R.string.confirm, (d, w)->{
                    for (int i = 0; i < itemSelecionado.length; i++){
                        if(itemSelecionado[i]){
                            itemConfirmado.add(carrinho.get(i));
                        }
                    }
                    carrinho = itemConfirmado;
                    calcularPreco();
                    salvarPedido();
                }).create();
        dialog.show();
    }
    private void confirmarQuantidade(Product product) {
        EditText editText = new EditText(this);
        editText.setText("1");
        editText.setHint(R.string.qtd);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setFocusable(true);
        AlertDialog confirm = new AlertDialog.Builder(this)
                .setTitle(R.string.amount)
                .setView(editText)
                .setPositiveButton(android.R.string.ok, (d, w) -> {
                    int quant = Integer.parseInt(editText.getText().toString());
                    if (quant >= 0) {
                        ItemPedido itemPedido = new ItemPedido(product.getId(), product.getName(),
                                quant, Double.parseDouble(product.getPrice()));
                        int position = carrinho.indexOf(itemPedido);
                        if (position >= 0) {
                            itemPedido.setQuandtidade(itemPedido.getQuandtidade() + carrinho.get(position).getQuandtidade());
                            carrinho.set(position, itemPedido);
                        } else {
                            carrinho.add(itemPedido);
                        }
                        salvarPedido();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        confirm.show();
    }

    private void confirmarPedido(){
        EditText editText = new EditText(this);
        editText.setHint(R.string.description);
        AlertDialog pagamento = new AlertDialog.Builder(this)
                .setTitle(R.string.select_payment_method)
                .setSingleChoiceItems(R.array.payments,0,(d,w)-> pedidoRecuperado.setMetodoPagamento(w))
                .setView(editText)
                .setPositiveButton(R.string.confirm, (d,w)->{
                    String obs = editText.getText().toString();
                    pedidoRecuperado.setObservacao(obs);
                    pedidoRecuperado.setStatus(Pedido.STATUS_CONFIRMADO);
                    pedidoService.commit(pedidoRecuperado);
                    pedidoService.delete(company.getId(),usuario.getId());
                    pedidoRecuperado = null;
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        pagamento.show();
    }
}