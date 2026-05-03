package com.redlab.utang;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.redlab.utang.utils.DateUtils;
import com.redlab.utang.utils.PdfExporter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
                     tvFullyPaidDate, tvStatusBadge, tvInterestInfo, tvPenaltyInfo;
    private Button btnPayPhoto, btnAddPenalty, btnExportPdf;
    private RecyclerView recyclerPayments;

    private Uri photoUri;
    private String currentPhotoPath;
    private double pendingPaymentAmount = 0;
    private String pendingPaymentNote   = "";
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debtor_detail);

        debtorId = getIntent().getLongExtra("debtor_id", -1);
        if (debtorId == -1) { finish(); return; }

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
        setupCamera();

        AppDatabase db = AppDatabase.getInstance(this);
        db.debtorDao().getDebtorById(debtorId).observe(this, debtor -> {
            if (debtor == null) { finish(); return; }
            currentDebtor = debtor;
            bindDebtorInfo(debtor);
        });
        db.paymentRecordDao().getPaymentsForDebtor(debtorId).observe(this, payments ->
            paymentAdapter.setPayments(payments));

        btnPayPhoto.setOnClickListener(v -> showPaymentDialog());
        btnAddPenalty.setOnClickListener(v -> showPenaltyDialog());
        btnExportPdf.setOnClickListener(v -> exportToPdf());
    }

    private void initViews() {
        tvName          = findViewById(R.id.tvDetailName);
        tvTotal         = findViewById(R.id.tvDetailTotal);
        tvPaid          = findViewById(R.id.tvDetailPaid);
        tvRemaining     = findViewById(R.id.tvDetailRemaining);
        tvStartDate     = findViewById(R.id.tvDetailStartDate);
        tvFullyPaidDate = findViewById(R.id.tvDetailFullyPaidDate);
        tvStatusBadge   = findViewById(R.id.tvStatusBadge);
        tvInterestInfo  = findViewById(R.id.tvInterestInfo);
        tvPenaltyInfo   = findViewById(R.id.tvPenaltyInfo);
        btnPayPhoto     = findViewById(R.id.btnPayPhoto);
        btnAddPenalty   = findViewById(R.id.btnAddPenalty);
        btnExportPdf    = findViewById(R.id.btnExportPdf);
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
                    processPhotoPayment();
                } else {
                    Toast.makeText(this, "Kinansela ang pagkuha ng larawan.", Toast.LENGTH_SHORT).show();
                    pendingPaymentAmount = 0;
                    currentPhotoPath = null;
                }
            });
    }

    private void bindDebtorInfo(Debtor d) {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(d.getName());

        tvName.setText(d.getName());
        tvTotal.setText(String.format("Kabuuang Utang: ₱%.2f", d.getTotalAmount()));
        tvPaid.setText(String.format("Nabayad na: ₱%.2f", d.getAmountPaid()));
        tvRemaining.setText(String.format("Natitira: ₱%.2f", d.getRemainingBalance()));
        tvStartDate.setText("📅 Simula: " + DateUtils.formatForDisplay(d.getStartDate()));

        if (d.getInterestRate() > 0) {
            tvInterestInfo.setVisibility(View.VISIBLE);
            tvInterestInfo.setText(String.format("📈 Interest: %.1f%% bawat buwan", d.getInterestRate()));
        } else {
            tvInterestInfo.setVisibility(View.GONE);
        }

        if (d.getPenaltyAmount() > 0) {
            tvPenaltyInfo.setVisibility(View.VISIBLE);
            tvPenaltyInfo.setText(String.format("⚠️ Penalty: ₱%.2f", d.getPenaltyAmount()));
        } else {
            tvPenaltyInfo.setVisibility(View.GONE);
        }

        if (d.isFullyPaid()) {
            tvStatusBadge.setText("✅ FULLY PAID");
            tvStatusBadge.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(getColor(R.color.green_700)));
            tvFullyPaidDate.setText("🏆 Fully Paid noong: " + DateUtils.formatForDisplay(d.getFullyPaidDate()));
            tvFullyPaidDate.setVisibility(View.VISIBLE);
            btnPayPhoto.setEnabled(false); btnPayPhoto.setAlpha(0.4f);
            btnAddPenalty.setEnabled(false); btnAddPenalty.setAlpha(0.4f);
        } else {
            tvStatusBadge.setText("⚠️ MAY UTANG PA");
            tvStatusBadge.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(getColor(R.color.red_700)));
            tvFullyPaidDate.setVisibility(View.GONE);
            btnPayPhoto.setEnabled(true); btnPayPhoto.setAlpha(1f);
            btnAddPenalty.setEnabled(true); btnAddPenalty.setAlpha(1f);
        }
    }

    // ── Payment Dialog ────────────────────────────────────────────────────

    private void showPaymentDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_payment, null);
        EditText etAmount = dialogView.findViewById(R.id.etPaymentAmount);
        EditText etNote   = dialogView.findViewById(R.id.etPaymentNote);

        // Pre-fill note with debtor's original notes as reference
        if (currentDebtor.getNotes() != null && !currentDebtor.getNotes().isEmpty()) {
            etNote.setHint("Ref: " + currentDebtor.getNotes());
        }

        new AlertDialog.Builder(this)
            .setTitle("📷 Magbayad (may Photo Receipt)")
            .setView(dialogView)
            .setPositiveButton("Kumuha ng Larawan", (dialog, which) -> {
                String s = etAmount.getText().toString().trim();
                if (TextUtils.isEmpty(s)) {
                    Toast.makeText(this, "Ilagay ang halaga.", Toast.LENGTH_SHORT).show();
                    return;
                }
                double amount;
                try {
                    amount = Double.parseDouble(s);
                    if (amount <= 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Hindi valid ang halaga.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (amount > currentDebtor.getRemainingBalance()) {
                    Toast.makeText(this, String.format("Sobra! Natitira lang ₱%.2f",
                        currentDebtor.getRemainingBalance()), Toast.LENGTH_LONG).show();
                    return;
                }
                pendingPaymentAmount = amount;
                pendingPaymentNote   = etNote.getText().toString().trim();
                launchCamera();
            })
            .setNegativeButton("Kanselahin", null)
            .show();
    }

    // ── Penalty Dialog ────────────────────────────────────────────────────

    private void showPenaltyDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_penalty, null);
        EditText etPenalty = dialogView.findViewById(R.id.etPenaltyAmount);
        EditText etReason  = dialogView.findViewById(R.id.etPenaltyReason);

        // Pre-fill computed interest if rate is set
        if (currentDebtor.getInterestRate() > 0) {
            double computed = currentDebtor.getRemainingBalance() * (currentDebtor.getInterestRate() / 100.0);
            etPenalty.setText(String.format(Locale.getDefault(), "%.2f", computed));
            etReason.setText(String.format(Locale.getDefault(),
                "Interest %.1f%% sa ₱%.2f", currentDebtor.getInterestRate(), currentDebtor.getRemainingBalance()));
        }

        new AlertDialog.Builder(this)
            .setTitle("⚠️ Magdagdag ng Penalty / Interest")
            .setView(dialogView)
            .setPositiveButton("I-apply", (dialog, which) -> {
                String s = etPenalty.getText().toString().trim();
                if (TextUtils.isEmpty(s)) return;
                double penalty;
                try {
                    penalty = Double.parseDouble(s);
                    if (penalty <= 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Hindi valid.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String reason = etReason.getText().toString().trim();
                applyPenalty(penalty, reason.isEmpty() ? "Penalty" : reason);
            })
            .setNegativeButton("Kanselahin", null)
            .show();
    }

    private void applyPenalty(double penalty, String reason) {
        String date = DateUtils.getCurrentDateTimeISO();
        PaymentRecord record = new PaymentRecord(debtorId, penalty, date, null, "PENALTY", reason);
        AppDatabase db = AppDatabase.getInstance(this);
        executor.execute(() -> {
            db.paymentRecordDao().insert(record);
            db.debtorDao().addPenalty(debtorId, penalty);
            runOnUiThread(() ->
                Toast.makeText(this, String.format("⚠️ ₱%.2f penalty na-apply. (%s)", penalty, reason),
                    Toast.LENGTH_SHORT).show());
        });
    }

    // ── Camera ────────────────────────────────────────────────────────────

    private void launchCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }
        try {
            File photoFile = createImageFile();
            photoUri = FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".fileprovider", photoFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            cameraLauncher.launch(intent);
        } catch (IOException e) {
            Toast.makeText(this, "Hindi ma-open ang camera.", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File f = File.createTempFile("RECEIPT_" + debtorId + "_" + ts, ".jpg", dir);
        currentPhotoPath = f.getAbsolutePath();
        return f;
    }

    private void processPhotoPayment() {
        if (currentPhotoPath == null || pendingPaymentAmount <= 0) return;
        String date = DateUtils.getCurrentDateTimeISO();
        PaymentRecord record = new PaymentRecord(debtorId, pendingPaymentAmount,
            date, currentPhotoPath, "PAYMENT", pendingPaymentNote);
        AppDatabase db = AppDatabase.getInstance(this);
        executor.execute(() -> {
            db.paymentRecordDao().insert(record);
            db.debtorDao().addPayment(debtorId, pendingPaymentAmount, date);
            runOnUiThread(() -> {
                Toast.makeText(this, String.format("📷 ₱%.2f nai-record may photo receipt!",
                    pendingPaymentAmount), Toast.LENGTH_SHORT).show();
                pendingPaymentAmount = 0;
                pendingPaymentNote   = "";
                currentPhotoPath = null;
            });
        });
    }

    // ── Export PDF ────────────────────────────────────────────────────────

    private void exportToPdf() {
        if (currentDebtor == null) return;
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<PaymentRecord> payments = db.paymentRecordDao()
                .getPaymentsForDebtorSync(debtorId);
            runOnUiThread(() ->
                PdfExporter.exportSingleDebtor(this, currentDebtor, payments));
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        }
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
