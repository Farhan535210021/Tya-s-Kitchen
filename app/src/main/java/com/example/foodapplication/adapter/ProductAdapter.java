package com.example.foodapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapplication.OrderActivity;
import com.example.foodapplication.R;
import com.example.foodapplication.model.Produk;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Produk> produkList;
    private Map<Integer, Integer> selectedQuantities = new HashMap<>(); // Store product ID and selected quantity

    public ProductAdapter(Context context, List<Produk> produkList) {
        this.context = context;
        this.produkList = produkList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_produk, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Produk produk = produkList.get(holder.getAdapterPosition());
        holder.tvProductName.setText(produk.getName());

        holder.tvProductPrice.setText(formatCurrency(produk.getPrice()));
        holder.tvQuantity.setText(String.valueOf(produk.getQty_in_cart()));
        holder.tvProductDescription.setText(produk.getDescription());

        // Menampilkan average rating dengan format tanpa trailing zero
        double averageRating = produk.getAverageRating();
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedRating = df.format(averageRating);
        holder.tvAverageRating.setText(formattedRating);

        Picasso.get()
                .load(produk.getImageUrl())
                .resize(300, 300)
                .centerCrop()
                .into(holder.ivProductImage);

        selectedQuantities.put(produk.getId(), produk.getQty_in_cart());

        // Handle click to increase quantity
        holder.imageAdd.setOnClickListener(v -> {
            int currentQuantity = selectedQuantities.get(produk.getId());
            currentQuantity++;
            selectedQuantities.put(produk.getId(), currentQuantity);
            holder.tvQuantity.setText(String.valueOf(currentQuantity));
            updatePrice(holder, produk, currentQuantity);
        });

        // Handle click to decrease quantity
        holder.imageMin.setOnClickListener(v -> {
            int currentQuantity = selectedQuantities.get(produk.getId());
            if (currentQuantity > 0) {
                currentQuantity--;
                selectedQuantities.put(produk.getId(), currentQuantity);
                holder.tvQuantity.setText(String.valueOf(currentQuantity));
                updatePrice(holder, produk, currentQuantity);
            }
        });
    }



    // Function to update the displayed price for the product
    private void updatePrice(ProductViewHolder holder, Produk produk, int quantity) {
        double totalPrice = quantity > 0 ? quantity * produk.getPrice() : produk.getPrice();
        holder.tvProductPrice.setText(formatCurrency(totalPrice));
    }

    // Method to format price as currency without decimals
    private String formatCurrency(double value) {
        return String.format("Rp %,.0f", value);
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    // Method to get the selected quantities of products
    public Map<Integer, Integer> getSelectedQuantities() {
        return selectedQuantities;
    }

    // Method to get product by ID
    public Produk getProductById(int productId) {
        for (Produk produk : produkList) {
            if (produk.getId() == productId) {
                return produk;
            }
        }
        return null;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvQuantity, tvProductDescription, tvAverageRating;
        ImageView ivProductImage, imageAdd, imageMin;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            imageAdd = itemView.findViewById(R.id.imageAdd);
            imageMin = itemView.findViewById(R.id.imageMin);
            tvProductDescription = itemView.findViewById(R.id.tvProductDescription);
            tvAverageRating = itemView.findViewById(R.id.tvAverageRating); // Tambahkan ini
        }
    }

}
