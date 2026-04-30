package com.redlab.utang.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.redlab.utang.R;
import com.redlab.utang.models.PaymentRecord;
import com.redlab.utang.utils.DateUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {

    private List<PaymentRecord> payments = new ArrayList<>();
    private final Context context;

    public PaymentAdapter(Context context) {
        this.context = context;
    }

    public void setPayments(List<PaymentRecord> payments) {
        this.payments = payments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_payment, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        PaymentRecord record = payments.get(position);
        holder.bind(record);
    }

    @Override
    public int getItemCount() { return payments.size(); }

    class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvDate, tvReceiptType;
        ImageView ivReceiptPhoto;
        View badgeBiometric, badgePhoto;

        PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount      = itemView.findViewById(R.id.tvPaymentAmount);
            tvDate        = itemView.findViewById(R.id.tvPaymentDate);
            tvReceiptType = itemView.findViewById(R.id.tvReceiptType);
            ivReceiptPhoto = itemView.findViewById(R.id.ivReceiptPhoto);
            badgeBiometric = itemView.findViewById(R.id.badgeBiometric);
            badgePhoto    = itemView.findViewById(R.id.badgePhoto);
        }

        void bind(PaymentRecord record) {
            tvAmount.setText(String.format("₱%.2f", record.getAmount()));
            tvDate.setText(DateUtils.formatForDisplay(record.getPaymentDate()));

            if ("BIOMETRIC".equals(record.getReceiptType())) {
                tvReceiptType.setText("🔏 Biometric Receipt");
                badgeBiometric.setVisibility(View.VISIBLE);
                badgePhoto.setVisibility(View.GONE);
                ivReceiptPhoto.setVisibility(View.GONE);
            } else {
                tvReceiptType.setText("📷 Photo Receipt");
                badgeBiometric.setVisibility(View.GONE);
                badgePhoto.setVisibility(View.VISIBLE);

                // Load photo thumbnail
                if (record.getPhotoPath() != null && !record.getPhotoPath().isEmpty()) {
                    File photoFile = new File(record.getPhotoPath());
                    if (photoFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(record.getPhotoPath());
                        if (bitmap != null) {
                            ivReceiptPhoto.setImageBitmap(bitmap);
                            ivReceiptPhoto.setVisibility(View.VISIBLE);
                        } else {
                            ivReceiptPhoto.setVisibility(View.GONE);
                        }
                    } else {
                        ivReceiptPhoto.setVisibility(View.GONE);
                    }
                } else {
                    ivReceiptPhoto.setVisibility(View.GONE);
                }
            }
        }
    }
}
