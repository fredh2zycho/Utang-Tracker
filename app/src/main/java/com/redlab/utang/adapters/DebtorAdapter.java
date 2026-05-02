package com.redlab.utang.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.redlab.utang.R;
import com.redlab.utang.models.Debtor;
import com.redlab.utang.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class DebtorAdapter extends RecyclerView.Adapter<DebtorAdapter.DebtorViewHolder> {

    public interface OnDebtorClickListener     { void onDebtorClick(Debtor debtor); }
    public interface OnDebtorLongClickListener { void onDebtorLongClick(Debtor debtor); }

    private List<Debtor> debtors = new ArrayList<>();
    private final OnDebtorClickListener clickListener;
    private final OnDebtorLongClickListener longClickListener;
    private final Context context;

    private static final int COLOR_FULLY_PAID  = Color.parseColor("#2E7D32");
    private static final int COLOR_PARTIAL     = Color.parseColor("#C62828");
    private static final int COLOR_BG_PAID     = Color.parseColor("#E8F5E9");
    private static final int COLOR_BG_PARTIAL  = Color.parseColor("#FFEBEE");

    public DebtorAdapter(Context context,
                         OnDebtorClickListener clickListener,
                         OnDebtorLongClickListener longClickListener) {
        this.context           = context;
        this.clickListener     = clickListener;
        this.longClickListener = longClickListener;
    }

    public void setDebtors(List<Debtor> debtors) {
        this.debtors = debtors;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DebtorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_debtor, parent, false);
        return new DebtorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DebtorViewHolder holder, int position) {
        Debtor debtor = debtors.get(position);
        holder.bind(debtor);
        holder.itemView.setOnClickListener(v -> clickListener.onDebtorClick(debtor));
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onDebtorLongClick(debtor);
            return true;
        });
    }

    @Override
    public int getItemCount() { return debtors.size(); }

    class DebtorViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvName, tvBalance, tvStartDate, tvStatus, tvInitial;

        DebtorViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView    = itemView.findViewById(R.id.cardView);
            tvName      = itemView.findViewById(R.id.tvDebtorName);
            tvBalance   = itemView.findViewById(R.id.tvBalance);
            tvStartDate = itemView.findViewById(R.id.tvStartDate);
            tvStatus    = itemView.findViewById(R.id.tvStatus);
            tvInitial   = itemView.findViewById(R.id.tvInitial);
        }

        void bind(Debtor debtor) {
            tvName.setText(debtor.getName());
            if (debtor.getName() != null && !debtor.getName().isEmpty())
                tvInitial.setText(String.valueOf(debtor.getName().charAt(0)).toUpperCase());

            tvStartDate.setText("Simula: " + DateUtils.formatDateOnly(debtor.getStartDate()));

            if (debtor.isFullyPaid()) {
                tvName.setTextColor(COLOR_FULLY_PAID);
                tvBalance.setText("FULLY PAID");
                tvBalance.setTextColor(COLOR_FULLY_PAID);
                tvStatus.setText("✓ Bayad na");
                tvStatus.setTextColor(COLOR_FULLY_PAID);
                cardView.setCardBackgroundColor(COLOR_BG_PAID);
                tvInitial.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(COLOR_FULLY_PAID));
            } else {
                tvName.setTextColor(COLOR_PARTIAL);
                tvBalance.setText(String.format("₱%.2f natitira", debtor.getRemainingBalance()));
                tvBalance.setTextColor(COLOR_PARTIAL);
                tvStatus.setText("✗ May utang pa");
                tvStatus.setTextColor(COLOR_PARTIAL);
                cardView.setCardBackgroundColor(COLOR_BG_PARTIAL);
                tvInitial.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(COLOR_PARTIAL));
            }
        }
    }
}
