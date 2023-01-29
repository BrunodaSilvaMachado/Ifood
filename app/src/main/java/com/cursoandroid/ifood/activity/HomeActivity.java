package com.cursoandroid.ifood.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cursoandroid.ifood.R;
import com.cursoandroid.ifood.adapter.GroupAdapter;
import com.cursoandroid.ifood.config.FirebaseConfig;
import com.cursoandroid.ifood.databinding.ActivityHomeBinding;
import com.cursoandroid.ifood.helper.MoneyTextWatcher;
import com.cursoandroid.ifood.model.Company;
import com.cursoandroid.ifood.model.Usuario;
import com.cursoandroid.ifood.service.CompanyService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {
    private final CompanyService companyService = new CompanyService();
    private ActivityHomeBinding binding;
    private final List<Company> empresaList = new ArrayList<>();
    private GroupAdapter<Company> empresaAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupActionBar();
        initRecyclerView();
        fectchCompany();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        companyService.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        setupSearchView(menu.findItem(R.id.app_bar_search));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.item_exit){
            signOut();
        }else if (itemId == R.id.item_config) {
            updateUiSettings();
        }
        //TODO: Adicionar uma guia com todos os pedidos do usuário

        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(R.string.app_name);
    }

    private void setupSearchView(MenuItem searchMenuItem){
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        if (searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchCompany(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            searchView.setOnCloseListener(() -> {
                empresaAdapter.clearFilter();
                return false;
            });
        }
    }

    private void initRecyclerView(){
        empresaAdapter = new GroupAdapter<>(empresaList, l-> R.layout.adapter_company, (c, v)->{
            CircleImageView i = v.findViewById(R.id.imageEmpresa);
            TextView n = v.findViewById(R.id.textNomeEmpresa);
            TextView ct = v.findViewById(R.id.textCategoriaEmpresa);
            TextView t = v.findViewById(R.id.textTempoEmpresa);
            TextView e = v.findViewById(R.id.textEntregaEmpresa);

            n.setText(c.getName());
            ct.setText(c.getCategory());
            t.setText(c.getDeliveryTime());
            e.setText(MoneyTextWatcher.numberFormat.format(Double.parseDouble(c.getDeliveryTax())));
            if (c.getUrlPhoto() != null) {
                Picasso.get().load(c.getUrlPhoto()).into(i);
            }
        });
        empresaAdapter.setComparator((c, s)-> c.getName().toLowerCase().trim().contains(s));
        empresaAdapter.setOnItemClickListener(company -> {
            Intent i = new Intent(HomeActivity.this, CardapioActivity.class);
            i.putExtra("COMPANY", company);
            startActivity(i);
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.rvHome.recyclerView.setLayoutManager(layoutManager);
        binding.rvHome.recyclerView.setHasFixedSize(true);
        binding.rvHome.recyclerView.setAdapter(empresaAdapter);
    }

    private void signOut(){
        FirebaseConfig.getFirebaseAuth().signOut();
        finish();
    }

    private void updateUiSettings(){
        Intent i = new Intent(this, SettingsActivity.class);
        i.putExtra("TYPE", Usuario.TYPE_USER);
        startActivity(i);
    }

    private void fectchCompany(){
        companyService.fetch(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                List<Company> empresas = new ArrayList<>();
                for (DataSnapshot ds: snapshot.getChildren()){
                    empresas.add(ds.getValue(Company.class));
                }
                empresaAdapter.addAllItem(empresas);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
    }

    private void searchCompany(String query){
        empresaAdapter.getFilter().filter(query);
    }
}