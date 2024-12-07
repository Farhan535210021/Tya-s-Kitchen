package com.example.foodapplication.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapplication.R;
import com.example.foodapplication.model.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItemList;

    public CartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_pembelian, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.llProductList.removeAllViews();

        double totalPrice = 0.0;

        for (CartItem item : cartItemList) {
            LinearLayout productLayout = new LinearLayout(context);
            productLayout.setOrientation(LinearLayout.HORIZONTAL);
            productLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView productName = new TextView(context);
            productName.setText(item.getProductName() + " (" + item.getQuantity() + "x)");
            productName.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            TextView productPrice = new TextView(context);
            // Using formatCurrency method to format the price
            productPrice.setText(formatCurrency(item.getTotalProductPrice()));
            productPrice.setGravity(Gravity.END);
            productPrice.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            productLayout.addView(productName);
            productLayout.addView(productPrice);
            holder.llProductList.addView(productLayout);

            totalPrice += item.getTotalProductPrice();
        }

        // Using formatCurrency method to format the total price
        holder.tvTotalPrice.setText("Total: " + formatCurrency(totalPrice));
    }

    // Method to format price as currency without decimals
    private String formatCurrency(double value) {
        return String.format("Rp %,.0f", value);
    }

    // Method to get the list of cart items
    public List<CartItem> getCartItems() {
        return cartItemList;
    }

    // New method to clear cart items
    public void clearCartItems() {
        cartItemList.clear();  // Clear all items in the cart
        notifyDataSetChanged();  // Notify the adapter about the data change
    }

    @Override
    public int getItemCount() {
        return 1;  // Only one card for displaying all items
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llProductList;
        TextView tvTotalPrice;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            llProductList = itemView.findViewById(R.id.llProductList);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }
    }
}
