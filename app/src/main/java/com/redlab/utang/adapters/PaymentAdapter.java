package com.redlab.utang.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

    public PaymentAdapter(Context context) { this.context = context; }

    public void setPayments(List<PaymentRecord> payments) {
        this.payments = payments != null ? payments : new ArrayList<>();
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
        holder.bind(payments.get(position));
    }

    @Override
    public int getItemCount() { return payments.size(); }

    class PaymentViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvAmount, tvDate, tvType, tvPaymentNote;
        ImageView ivPhoto;
        View layoutNote;

        PaymentViewHolder(@NonNull View v) {
            super(v);
            cardView      = v.findViewById(R.id.cardPayment);
            tvAmount      = v.findViewById(R.id.tvPaymentAmount);
            tvDate        = v.findViewById(R.id.tvPaymentDate);
            tvType        = v.findViewById(R.id.tvReceiptType);
            tvPaymentNote = v.findViewById(R.id.tvPaymentNote);
            layoutNote    = v.findViewById(R.id.layoutNote);
            ivPhoto       = v.findViewById(R.id.ivReceiptPhoto);
        }

        void bind(PaymentRecord pr) {
            boolean isPenalty = "PENALTY".equals(pr.getRecordType());

            tvDate.setText(DateUtils.formatForDisplay(pr.getPaymentDate()));

            if (isPenalty) {
                tvAmount.setText(String.format("+₱%.2f", pr.getAmount()));
                tvAmount.setTextColor(Color.parseColor("#C62828"));
                tvType.setText("⚠️ Penalty / Interest");
                tvType.setTextColor(Color.parseColor("#C62828"));
                cardView.setCardBackgroundColor(Color.parseColor("#FFF3E0"));
                ivPhoto.setVisibility(View.GONE);
            } else {
                tvAmount.setText(String.format("₱%.2f", pr.getAmount()));
                tvAmount.setTextColor(Color.parseColor("#2E7D32"));
                tvType.setText("📷 Photo Receipt");
                tvType.setTextColor(Color.parseColor("#1565C0"));
                cardView.setCardBackgroundColor(Color.WHITE);

                if (pr.getPhotoPath() != null && !pr.getPhotoPath().isEmpty()) {
                    File f = new File(pr.getPhotoPath());
                    if (f.exists()) {
                        Bitmap bm = BitmapFactory.decodeFile(pr.getPhotoPath());
                        if (bm != null) {
                            ivPhoto.setImageBitmap(bm);
                            ivPhoto.setVisibility(View.VISIBLE);
                        } else {
                            ivPhoto.setVisibility(View.GONE);
                        }
                    } else {
                        ivPhoto.setVisibility(View.GONE);
                    }
                } else {
                    ivPhoto.setVisibility(View.GONE);
                }
            }

            // Show payment note (items/products) for both payments and penalties
            String note = pr.getPaymentNote();
            if (note != null && !note.trim().isEmpty()) {
                tvPaymentNote.setText(note);
                layoutNote.setVisibility(View.VISIBLE);
            } else {
                layoutNote.setVisibility(View.GONE);
            }
        }
    }
}
