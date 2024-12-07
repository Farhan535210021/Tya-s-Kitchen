package com.example.foodapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapplication.R;
import com.example.foodapplication.model.TransactionDetail;
import com.example.foodapplication.model.TransactionGroup;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RekapAdapter extends RecyclerView.Adapter<RekapAdapter.TransactionViewHolder> {

    private final Context context;
    private List<TransactionGroup> transactionGroups;

    public RekapAdapter(Context context, List<TransactionGroup> transactionGroups) {
        this.context = context;
        this.transactionGroups = transactionGroups != null ? transactionGroups : new ArrayList<>();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rekap, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionGroup transactionGroup = transactionGroups.get(position);

        holder.tvTransactionId.setText("Transaction ID: " + transactionGroup.getTransactionId());

        // Build product details string
        StringBuilder productDetails = new StringBuilder();
        for (TransactionDetail product : transactionGroup.getProducts()) {
            productDetails.append(product.getNamaProduk())
                    .append(" - Qty: ")
                    .append(product.getQtyProduk())
                    .append(" - Total: ")
                    .append(formatToCurrency(product.getTotalHarga()))
                    .append("\n");
        }

        holder.tvProducts.setText(productDetails.toString().trim());
    }

    @Override
    public int getItemCount() {
        return transactionGroups.size();
    }

    // Update adapter data and refresh RecyclerView
    public void updateData(List<TransactionGroup> transactionGroups) {
        this.transactionGroups = transactionGroups != null ? transactionGroups : new ArrayList<>();
        notifyDataSetChanged();
    }

    private String formatToCurrency(double amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        currencyFormat.setMaximumFractionDigits(0); // Remove decimals
        return currencyFormat.format(amount);
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionId, tvProducts;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransactionId = itemView.findViewById(R.id.tvTransactionId);
            tvProducts = itemView.findViewById(R.id.tvProducts);
        }
    }
}
