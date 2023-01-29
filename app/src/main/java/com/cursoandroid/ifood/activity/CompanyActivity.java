package com.cursoandroid.ifood.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cursoandroid.ifood.R;
import com.cursoandroid.ifood.activity.ui.SwipeRecyclerView;
import com.cursoandroid.ifood.adapter.GroupAdapter;
import com.cursoandroid.ifood.config.FirebaseConfig;
import com.cursoandroid.ifood.databinding.ActivityCompanyBinding;
import com.cursoandroid.ifood.helper.MoneyTextWatcher;
import com.cursoandroid.ifood.model.Product;
import com.cursoandroid.ifood.model.Usuario;
import com.cursoandroid.ifood.service.CompanyService;
import com.cursoandroid.ifood.service.ProductService;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CompanyActivity extends AppCompatActivity {
    /**
     * TODO: inverter a exibição com PedidoActivity
     */
    private final ProductService productService = new ProductService();
    private final List<Product> produtoList = new ArrayList<>();
    private ActivityCompanyBinding binding;
    private GroupAdapter<Product> produtoAdapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompanyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupActionBar();
        initRecyclerView();
        fetchProduct();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        productService.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_company, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.item_exit){
            signOut();
        } else if (itemId == R.id.item_config) {
            updateUiSettings();
        } else if (itemId == R.id.item_add) {
            updateUiAdd();
        } else if (itemId == R.id.item_demand){
            updateUiDemand();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(String.format("%s - %s", getString(R.string.app_name), getString(R.string.company)));
    }

    private void initRecyclerView(){
        recyclerView = binding.rvCompany.recyclerView;
        produtoAdapter = new GroupAdapter<>(produtoList,l-> R.layout.adapter_product, (p, v)->{
            TextView nome = v.findViewById(R.id.textNomeRefeicao);
            TextView desc = v.findViewById(R.id.textDescricaoRefeicao);
            TextView preco = v.findViewById(R.id.textPreco);

            nome.setText(p.getName());
            desc.setText(p.getDescription());
            preco.setText(MoneyTextWatcher.numberFormat.format(Double.parseDouble(p.getPrice())));
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(produtoAdapter);
        SwipeRecyclerView.onSwipe(recyclerView, this::deleteProduct);
    }

    private void signOut(){
        FirebaseConfig.getFirebaseAuth().signOut();
        finish();
    }
    private void updateUiSettings(){
        Intent i = new Intent(this, SettingsActivity.class);
        i.putExtra("TYPE", Usuario.TYPE_COMPANY);
        startActivity(i);
    }

    private void updateUiAdd(){
        startActivity(new Intent(this, AddProductActivity.class));
    }

    private void updateUiDemand(){
    startActivity(new Intent(CompanyActivity.this, PedidosActivity.class));
    }

    private void fetchProduct(){
        productService.fetch(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                produtoList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    produtoList.add(ds.getValue(Product.class));
                }
                produtoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        },CompanyService.getCompanyId());
    }
    @SuppressLint("NotifyDataSetChanged")
    private void deleteProduct(RecyclerView.ViewHolder viewHolder) {
        final int position = viewHolder.getAdapterPosition();
        final Product product = produtoList.get(position);
        final boolean[] removido = {true};
        produtoList.remove(position);
        produtoAdapter.notifyItemRemoved(position);
        Snackbar.make(recyclerView, product.getName(), Snackbar.LENGTH_LONG).setAction(R.string.undo,
                v -> {
                    produtoList.add(position, product);
                    produtoAdapter.notifyItemInserted(position);
                    removido[0] = false;
                }).show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (removido[0]) {
                productService.delete(product);
            }
        }, 2750);
    }

}