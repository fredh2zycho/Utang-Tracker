package com.redlab.utang;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.redlab.utang.adapters.DebtorAdapter;
import com.redlab.utang.database.AppDatabase;
import com.redlab.utang.models.Debtor;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DebtorAdapter.OnDebtorClickListener {

    private DebtorAdapter adapter;
    private List<Debtor> allDebtors = new ArrayList<>();
    private EditText etSearch;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("📋 Listahan ng Utang");
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewDebtors);
        FloatingActionButton fab   = findViewById(R.id.fabAddDebtor);
        etSearch = findViewById(R.id.etSearch);
        tvEmpty  = findViewById(R.id.tvEmpty);

        adapter = new DebtorAdapter(this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Observe all debtors alphabetically
        AppDatabase db = AppDatabase.getInstance(this);
        db.debtorDao().getAllDebtorsAlphabetical().observe(this, debtors -> {
            allDebtors = debtors != null ? debtors : new ArrayList<>();
            filterList(etSearch.getText().toString());
        });

        fab.setOnClickListener(v -> {
            startActivity(new Intent(this, AddDebtorActivity.class));
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterList(String query) {
        if (query == null || query.trim().isEmpty()) {
            adapter.setDebtors(allDebtors);
            tvEmpty.setVisibility(allDebtors.isEmpty() ? View.VISIBLE : View.GONE);
        } else {
            String lower = query.toLowerCase();
            List<Debtor> filtered = new ArrayList<>();
            for (Debtor d : allDebtors) {
                if (d.getName() != null && d.getName().toLowerCase().contains(lower)) {
                    filtered.add(d);
                }
            }
            adapter.setDebtors(filtered);
            tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDebtorClick(Debtor debtor) {
        Intent intent = new Intent(this, DebtorDetailActivity.class);
        intent.putExtra("debtor_id", debtor.getId());
        startActivity(intent);
    }
}
