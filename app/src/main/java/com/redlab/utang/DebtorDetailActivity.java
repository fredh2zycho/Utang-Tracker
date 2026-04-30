package com.redlab.utang;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.redlab.utang.adapters.PaymentAdapter;
import com.redlab.utang.database.AppDatabase;
import com.redlab.utang.models.Debtor;
import com.redlab.utang.models.PaymentRecord;
import com.redlab.utang.utils.BiometricHelper;
import com.redlab.utang.utils.DateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DebtorDetailActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 101;

    private long debtorId;
    private Debtor currentDebtor;
    private PaymentAdapter paymentAdapter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private TextView tvName, tvTotal, tvPaid, tvRemaining, tvStartDate,
                     tvFullyPaidDate, tvStatusBadge, tvFingerprintNote;
    private Button btnPayBiometric, btnPayPhoto, btnEditNotes;
    private RecyclerView recyclerPayments;

    // For photo capture
    private Uri photoUri;
    private String currentPhotoPath;
    private double pendingPaymentAmount = 0;

    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debtor_detail);

        debtorId = getIntent().getLongExtra("debtor_id", -1);
        if (debtorId == -1) { finish(); return; }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();
        setupCamera();

        AppDatabase db = AppDatabase.getInstance(this);

        // Observe debtor
        db.debtorDao().getDebtorById(debtorId).observe(this, debtor -> {
            if (debtor == null) { finish(); return; }
            currentDebtor = debtor;
            bindDebtorInfo(debtor);
        });

        // Observe payments
        db.paymentRecordDao().getPaymentsForDebtor(debtorId).observe(this, payments -> {
            paymentAdapter.setPayments(payments);
        });

        btnPayBiometric.setOnClickListener(v -> showPaymentDialog("BIOMETRIC"));
        btnPayPhoto.setOnClickListener(v -> showPaymentDialog("PHOTO"));
    }

    private void initViews() {
        tvName           = findViewById(R.id.tvDetailName);
        tvTotal          = findViewById(R.id.tvDetailTotal);
        tvPaid           = findViewById(R.id.tvDetailPaid);
        tvRemaining      = findViewById(R.id.tvDetailRemaining);
        tvStartDate      = findViewById(R.id.tvDetailStartDate);
        tvFullyPaidDate  = findViewById(R.id.tvDetailFullyPaidDate);
        tvStatusBadge    = findViewById(R.id.tvStatusBadge);
        tvFingerprintNote= findViewById(R.id.tvFingerprintNote);
        btnPayBiometric  = findViewById(R.id.btnPayBiometric);
        btnPayPhoto      = findViewById(R.id.btnPayPhoto);
        recyclerPayments = findViewById(R.id.recyclerPayments);

        paymentAdapter = new PaymentAdapter(this);
        recyclerPayments.setLayoutManager(new LinearLayoutManager(this));
        recyclerPayments.setAdapter(paymentAdapter);
    }

    private void setupCamera() {
        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Photo taken successfully — save payment
                    processPhotoPayment();
                } else {
                    Toast.makeText(this, "Kinansela ang pagkuha ng larawan.", Toast.LENGTH_SHORT).show();
                    pendingPaymentAmount = 0;
                    currentPhotoPath = null;
                }
            }
        );
    }

    private void bindDebtorInfo(Debtor debtor) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(debtor.getName());
        }
        tvName.setText(debtor.getName());
        tvTotal.setText(String.format("Kabuuang Utang: ₱%.2f", debtor.getTotalAmount()));
        tvPaid.setText(String.format("Nabayad na: ₱%.2f", debtor.getAmountPaid()));
        tvRemaining.setText(String.format("Natitira: ₱%.2f", debtor.getRemainingBalance()));
        tvStartDate.setText("📅 Simula ng Utang: " + DateUtils.formatForDisplay(debtor.getStartDate()));

        // Fingerprint note
        if (debtor.getFingerprintKey() != null && !debtor.getFingerprintKey().isEmpty()) {
            tvFingerprintNote.setText("🔏 May naka-register na fingerprint");
            tvFingerprintNote.setVisibility(View.VISIBLE);
        } else {
            tvFingerprintNote.setText("⚠️ Walang fingerprint — photo receipt lang");
            tvFingerprintNote.setVisibility(View.VISIBLE);
        }

        if (debtor.isFullyPaid()) {
            tvStatusBadge.setText("✅ FULLY PAID");
            tvStatusBadge.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(getColor(R.color.green_700)));
            tvFullyPaidDate.setText("🏆 Fully Paid noong: " + DateUtils.formatForDisplay(debtor.getFullyPaidDate()));
            tvFullyPaidDate.setVisibility(View.VISIBLE);
            btnPayBiometric.setEnabled(false);
            btnPayPhoto.setEnabled(false);
            btnPayBiometric.setAlpha(0.4f);
            btnPayPhoto.setAlpha(0.4f);
        } else {
            tvStatusBadge.setText("⚠️ MAY UTANG PA");
            tvStatusBadge.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(getColor(R.color.red_700)));
            tvFullyPaidDate.setVisibility(View.GONE);
            btnPayBiometric.setEnabled(true);
            btnPayPhoto.setEnabled(true);
            btnPayBiometric.setAlpha(1f);
            btnPayPhoto.setAlpha(1f);
        }
    }

    private void showPaymentDialog(String receiptType) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_payment, null);
        EditText etPaymentAmount = dialogView.findViewById(R.id.etPaymentAmount);

        String title = "BIOMETRIC".equals(receiptType)
                ? "💳 Magbayad (Biometric Receipt)"
                : "📷 Magbayad (Photo Receipt)";

        new AlertDialog.Builder(this)
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton("Ituloy", (dialog, which) -> {
                String amountStr = etPaymentAmount.getText().toString().trim();
                if (TextUtils.isEmpty(amountStr)) {
                    Toast.makeText(this, "Ilagay ang halaga ng bayad.", Toast.LENGTH_SHORT).show();
                    return;
                }
                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                    if (amount <= 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Hindi valid ang halaga.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (amount > currentDebtor.getRemainingBalance()) {
                    Toast.makeText(this,
                        String.format("Sobra ang bayad! Natitira lang ₱%.2f",
                            currentDebtor.getRemainingBalance()),
                        Toast.LENGTH_LONG).show();
                    return;
                }

                pendingPaymentAmount = amount;
                if ("BIOMETRIC".equals(receiptType)) {
                    processBiometricPayment(amount);
                } else {
                    launchCamera(amount);
                }
            })
            .setNegativeButton("Kanselahin", null)
            .show();
    }

    private void processBiometricPayment(double amount) {
        if (!BiometricHelper.isBiometricAvailable(this)) {
            Toast.makeText(this,
                "Hindi available ang biometric. Gamitin ang Photo Receipt.",
                Toast.LENGTH_LONG).show();
            return;
        }

        BiometricHelper.showBiometricPrompt(
            this,
            "I-verify ang Pagbabayad",
            currentDebtor.getName(),
            String.format("I-scan ang fingerprint para i-confirm ang ₱%.2f na bayad.", amount),
            new BiometricHelper.BiometricCallback() {
                @Override
                public void onSuccess() {
                    savePayment(amount, "BIOMETRIC", null, true);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(DebtorDetailActivity.this,
                        "Hindi naverify: " + errorMessage, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(DebtorDetailActivity.this,
                        errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    private void launchCamera(double amount) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }

        pendingPaymentAmount = amount;
        try {
            File photoFile = createImageFile();
            photoUri = FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".fileprovider", photoFile);

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            cameraLauncher.launch(takePictureIntent);
        } catch (IOException e) {
            Toast.makeText(this, "Hindi ma-open ang camera: " + e.getMessage(),
                Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "RECEIPT_" + debtorId + "_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void processPhotoPayment() {
        if (currentPhotoPath != null && pendingPaymentAmount > 0) {
            savePayment(pendingPaymentAmount, "PHOTO", currentPhotoPath, false);
        }
    }

    private void savePayment(double amount, String receiptType, String photoPath, boolean biometricVerified) {
        String paymentDate = DateUtils.getCurrentDateTimeISO();
        PaymentRecord record = new PaymentRecord(debtorId, amount, paymentDate,
                receiptType, photoPath, biometricVerified);

        AppDatabase db = AppDatabase.getInstance(this);
        executor.execute(() -> {
            db.paymentRecordDao().insert(record);
            db.debtorDao().addPayment(debtorId, amount, paymentDate);

            runOnUiThread(() -> {
                pendingPaymentAmount = 0;
                currentPhotoPath = null;
                String msg = "BIOMETRIC".equals(receiptType)
                    ? String.format("✅ ₱%.2f na-verify ng biometric!", amount)
                    : String.format("📷 ₱%.2f nai-record may photo receipt!", amount);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera(pendingPaymentAmount);
            } else {
                Toast.makeText(this,
                    "Kailangan ng camera permission para sa photo receipt.", Toast.LENGTH_SHORT).show();
            }
        }
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
