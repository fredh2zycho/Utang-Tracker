package com.redlab.utang;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.redlab.utang.database.AppDatabase;
import com.redlab.utang.models.Debtor;
import com.redlab.utang.utils.BiometricHelper;
import com.redlab.utang.utils.DateUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddDebtorActivity extends AppCompatActivity {

    private EditText etName, etAmount, etNotes;
    private TextView tvFingerprintStatus;
    private Button btnScanFingerprint, btnSave;
    private boolean fingerprintRegistered = false;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_debtor);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Magdagdag ng Nagkakautang");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etName              = findViewById(R.id.etName);
        etAmount            = findViewById(R.id.etAmount);
        etNotes             = findViewById(R.id.etNotes);
        tvFingerprintStatus = findViewById(R.id.tvFingerprintStatus);
        btnScanFingerprint  = findViewById(R.id.btnScanFingerprint);
        btnSave             = findViewById(R.id.btnSave);

        btnScanFingerprint.setOnClickListener(v -> registerFingerprint());
        btnSave.setOnClickListener(v -> saveDebtor());
    }

    private void registerFingerprint() {
        if (!BiometricHelper.isBiometricAvailable(this)) {
            Toast.makeText(this,
                    "Hindi available ang biometric sa device na ito. Maaari pa ring mag-save.",
                    Toast.LENGTH_LONG).show();
            fingerprintRegistered = false;
            tvFingerprintStatus.setText("⚠️ Walang biometric — maaari pa ring mag-save");
            return;
        }

        BiometricHelper.showBiometricPrompt(
                this,
                "I-register ang Fingerprint",
                etName.getText().toString().trim().isEmpty() ? "Bagong Customer" : etName.getText().toString().trim(),
                "I-scan ang fingerprint ng customer para sa verification.",
                new BiometricHelper.BiometricCallback() {
                    @Override
                    public void onSuccess() {
                        fingerprintRegistered = true;
                        tvFingerprintStatus.setText("✅ Fingerprint na-register!");
                        tvFingerprintStatus.setTextColor(getColor(R.color.green_700));
                        Toast.makeText(AddDebtorActivity.this,
                                "Fingerprint na-register nang matagumpay!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        tvFingerprintStatus.setText("❌ " + errorMessage);
                        tvFingerprintStatus.setTextColor(getColor(R.color.red_700));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        tvFingerprintStatus.setText("⚠️ " + errorMessage);
                    }
                }
        );
    }

    private void saveDebtor() {
        String name = etName.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

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

        String startDate = DateUtils.getCurrentDateTimeISO();
        Debtor debtor = new Debtor(name, amount, startDate, notes);
        if (fingerprintRegistered) {
            debtor.setFingerprintKey("biometric_" + System.currentTimeMillis());
        }

        AppDatabase db = AppDatabase.getInstance(this);
        executor.execute(() -> {
            db.debtorDao().insert(debtor);
            runOnUiThread(() -> {
                Toast.makeText(this, "Nai-register na si " + name + "!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
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
