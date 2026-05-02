package com.redlab.utang;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.redlab.utang.adapters.DebtorAdapter;
import com.redlab.utang.database.AppDatabase;
import com.redlab.utang.models.Debtor;
import com.redlab.utang.pro.LicenseManager;
import com.redlab.utang.pro.StoreSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity
        implements DebtorAdapter.OnDebtorClickListener,
                   DebtorAdapter.OnDebtorLongClickListener {

    private DebtorAdapter adapter;
    private List<Debtor> allDebtors = new ArrayList<>();
    private EditText etSearch;
    private TextView tvEmpty, tvStoreName;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewDebtors);
        FloatingActionButton fab  = findViewById(R.id.fabAddDebtor);
        etSearch    = findViewById(R.id.etSearch);
        tvEmpty     = findViewById(R.id.tvEmpty);
        tvStoreName = findViewById(R.id.tvStoreName);

        adapter = new DebtorAdapter(this, this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        AppDatabase db = AppDatabase.getInstance(this);
        db.debtorDao().getAllDebtorsAlphabetical().observe(this, debtors -> {
            allDebtors = debtors != null ? debtors : new ArrayList<>();
            filterList(etSearch.getText().toString());
        });

        fab.setOnClickListener(v ->
            startActivity(new Intent(this, AddDebtorActivity.class)));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String name = StoreSettings.getInstance(this).getStoreName();
        tvStoreName.setText(name);
    }

    private void filterList(String query) {
        List<Debtor> list;
        if (query == null || query.trim().isEmpty()) {
            list = allDebtors;
        } else {
            String lower = query.toLowerCase();
            list = new ArrayList<>();
            for (Debtor d : allDebtors) {
                if (d.getName() != null && d.getName().toLowerCase().contains(lower))
                    list.add(d);
            }
        }
        adapter.setDebtors(list);
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDebtorClick(Debtor debtor) {
        Intent intent = new Intent(this, DebtorDetailActivity.class);
        intent.putExtra("debtor_id", debtor.getId());
        startActivity(intent);
    }

    @Override
    public void onDebtorLongClick(Debtor debtor) {
        boolean isPro = LicenseManager.getInstance(this).isPro();

        if (!debtor.isFullyPaid() && !isPro) {
            Toast.makeText(this,
                "🔒 Pag-delete ng hindi pa bayad ay Pro feature lang.",
                Toast.LENGTH_LONG).show();
            return;
        }

        String message = debtor.isFullyPaid()
            ? "Sigurado bang i-delete si " + debtor.getName() + "? Fully paid na siya."
            : "⚠️ Hindi pa fully paid si " + debtor.getName() + "! Sigurado kang i-delete?";

        new AlertDialog.Builder(this)
            .setTitle("🗑️ I-delete si " + debtor.getName() + "?")
            .setMessage(message)
            .setPositiveButton("Oo, i-delete", (d, w) -> deleteDebtor(debtor))
            .setNegativeButton("Kanselahin", null)
            .show();
    }

    private void deleteDebtor(Debtor debtor) {
        AppDatabase db = AppDatabase.getInstance(this);
        executor.execute(() -> {
            db.debtorDao().delete(debtor);
            runOnUiThread(() ->
                Toast.makeText(this, debtor.getName() + " na-delete.", Toast.LENGTH_SHORT).show()
            );
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
