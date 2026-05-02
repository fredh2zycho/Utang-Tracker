package com.redlab.utang;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.redlab.utang.database.AppDatabase;
import com.redlab.utang.models.Debtor;
import com.redlab.utang.utils.DateUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddDebtorActivity extends AppCompatActivity {

    private EditText etName, etAmount, etNotes, etInterestRate;
    private Button btnSave;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_debtor);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Magdagdag ng Nagkakautang");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etName         = findViewById(R.id.etName);
        etAmount       = findViewById(R.id.etAmount);
        etNotes        = findViewById(R.id.etNotes);
        etInterestRate = findViewById(R.id.etInterestRate);
        btnSave        = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> saveDebtor());
    }

    private void saveDebtor() {
        String name      = etName.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String notes     = etNotes.getText().toString().trim();
        String rateStr   = etInterestRate.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Kailangan ang pangalan");
            etName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(amountStr)) {
            etAmount.setError("Kailangan ang halaga ng utang");
            etAmount.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            etAmount.setError("Ilagay ang tamang halaga");
            etAmount.requestFocus();
            return;
        }

        double interestRate = 0;
        if (!TextUtils.isEmpty(rateStr)) {
            try {
                interestRate = Double.parseDouble(rateStr);
                if (interestRate < 0) interestRate = 0;
            } catch (NumberFormatException ignored) {}
        }

        String startDate = DateUtils.getCurrentDateTimeISO();
        Debtor debtor = new Debtor(name, amount, startDate, notes);
        debtor.setInterestRate(interestRate);

        AppDatabase db = AppDatabase.getInstance(this);
        double finalInterestRate = interestRate;
        executor.execute(() -> {
            db.debtorDao().insert(debtor);
            runOnUiThread(() -> {
                String msg = "Nai-register na si " + name + "!";
                if (finalInterestRate > 0)
                    msg += " (Interest: " + finalInterestRate + "% bawat buwan)";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
