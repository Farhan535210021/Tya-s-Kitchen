package com.example.foodapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapplication.KeranjangActivity;
import com.example.foodapplication.R;
import com.example.foodapplication.model.RecommendedProduct;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder> {

    private Context context;
    private List<RecommendedProduct> recommendationList;
    private Map<Integer, Integer> productQuantities = new HashMap<>();
    private KeranjangActivity keranjangActivity;

    public RecommendationAdapter(Context context, List<RecommendedProduct> recommendationList, KeranjangActivity keranjangActivity) {
        this.context = context;
        this.recommendationList = recommendationList;
        this.keranjangActivity = keranjangActivity;
    }

    @NonNull
    @Override
    public RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_rekomendasi, parent, false);
        return new RecommendationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationViewHolder holder, int position) {
        RecommendedProduct product = recommendationList.get(position);
        holder.tvProductName.setText(product.getName());

        // Get and display product price formatted with formatCurrency
        double productPrice = product.getPrice();
        holder.tvProductPrice.setText(formatCurrency(productPrice));

        Picasso.get()
                .load(product.getImageUrl())
                .resize(150, 150)
                .centerCrop()
                .into(holder.ivProductImage);

        // Display product rating
        if (product.getRating() != null) {
            holder.tvRating.setText(String.format("%.1f", product.getRating()));
        } else {
            holder.tvRating.setText("-");
        }

        // Set initial quantity to 0 or existing value
        int initialQuantity = productQuantities.getOrDefault(product.getId(), 0);
        holder.tvQuantity.setText(String.valueOf(initialQuantity));

        // Display total price if initial quantity is greater than 0
        if (initialQuantity > 0) {
            double initialTotalPrice = productPrice * initialQuantity;
            holder.tvProductPrice.setText(formatCurrency(initialTotalPrice));
        } else {
            holder.tvProductPrice.setText(formatCurrency(productPrice));
        }

        // Handle quantity increment
        holder.imageAdd.setOnClickListener(v -> {
            int currentQuantity = productQuantities.getOrDefault(product.getId(), 0);
            currentQuantity++;
            productQuantities.put(product.getId(), currentQuantity);
            holder.tvQuantity.setText(String.valueOf(currentQuantity));

            double totalPrice = productPrice * currentQuantity;
            holder.tvProductPrice.setText(formatCurrency(totalPrice));

            if (keranjangActivity != null) {
                keranjangActivity.updateTotalProductAndPrice();
            }
        });

        // Handle quantity decrement
        holder.imageMin.setOnClickListener(v -> {
            int currentQuantity = productQuantities.getOrDefault(product.getId(), 0);
            if (currentQuantity > 0) {
                currentQuantity--;
                productQuantities.put(product.getId(), currentQuantity);
                holder.tvQuantity.setText(String.valueOf(currentQuantity));

                double totalPrice = currentQuantity > 0 ? productPrice * currentQuantity : productPrice;
                holder.tvProductPrice.setText(formatCurrency(totalPrice));

                if (keranjangActivity != null) {
                    keranjangActivity.updateTotalProductAndPrice();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return recommendationList.size();
    }

    public static class RecommendationViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductName, tvProductPrice, tvQuantity, tvRating ;
        ImageView ivProductImage, imageAdd, imageMin;

        public RecommendationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            imageAdd = itemView.findViewById(R.id.imageAdd);
            imageMin = itemView.findViewById(R.id.imageMin);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }

    // Helper method to format currency without decimals
    private String formatCurrency(double value) {
        return String.format("Rp %,.0f", value);
    }

    // Method to return the selected quantities for each product
    public Map<Integer, Integer> getSelectedQuantities() {
        return productQuantities;
    }

    // Method to clear selected quantities
    public void clearSelectedQuantities() {
        productQuantities.clear();
        notifyDataSetChanged();
    }

    public RecommendedProduct getProductById(int productId) {
        for (RecommendedProduct product : recommendationList) {
            if (product.getId() == productId) {
                return product;
            }
        }
        return null;
    }
}
