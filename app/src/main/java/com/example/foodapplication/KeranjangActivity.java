package com.example.foodapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapplication.adapter.CartAdapter;
import com.example.foodapplication.adapter.RecommendationAdapter;
import com.example.foodapplication.model.CartItem;
import com.example.foodapplication.model.CheckoutItem;
import com.example.foodapplication.model.RecommendedProduct;
import com.example.foodapplication.network.ApiService;
import com.example.foodapplication.connection.ApiServer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KeranjangActivity extends AppCompatActivity {

    private RecyclerView rvListPembelian, rvListRekomendasi;
    private CartAdapter cartAdapter;
    private RecommendationAdapter recommendationAdapter;
    private ApiService apiService;
    private int userId;
    private TextView tvProductCount, tvProductPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keranjang);

        // Initialize views
        tvProductCount = findViewById(R.id.tvProductCount);
        tvProductPrice = findViewById(R.id.tvProductPrice);

        // Get user_id from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);  // Retrieve user_id

        if (userId == -1) {
            Toast.makeText(this, "User tidak ditemukan, silahkan login", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize checkout button
        Button btnCheckout = findViewById(R.id.btnBayar);
        btnCheckout.setOnClickListener(view -> {
            checkout(); // Handle checkout, combining both cart and recommendations
        });

        // Initialize RecyclerView for cart items
        rvListPembelian = findViewById(R.id.rvlistpembelian);
        rvListPembelian.setLayoutManager(new LinearLayoutManager(this));

        // Initialize RecyclerView for recommendations
        rvListRekomendasi = findViewById(R.id.rvlistrekomendasi);
        rvListRekomendasi.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Initialize API service
        apiService = ApiServer.getApiService();

        // Get cart items
        getKeranjangItems();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove default title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Back button handling
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(KeranjangActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getKeranjangItems();  // Refresh cart items when activity resumes
    }

    private void getKeranjangItems() {
        apiService.getCartItems(userId).enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CartItem> cartItems = response.body();
                    // Set adapter with cart items
                    cartAdapter = new CartAdapter(KeranjangActivity.this, cartItems);
                    rvListPembelian.setAdapter(cartAdapter);

                    // Get product names from cart items
                    List<String> productNames = new ArrayList<>();
                    for (CartItem item : cartItems) {
                        productNames.add(item.getProductName());
                    }

                    // Fetch recommendations based on cart items
                    getRecommendations(productNames);

                    // Update total product count and price
                    updateTotalProductAndPrice();
                } else {
                    Toast.makeText(KeranjangActivity.this, "Gagal memuat keranjang", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Log.e("KeranjangActivity", "Gagal memuat keranjang: " + t.getMessage());
            }
        });
    }

    private void getRecommendations(List<String> cartItems) {
        // Create JSON from cartItems
        JsonObject jsonBody = new JsonObject();
        JsonArray cartArray = new JsonArray();
        for (String item : cartItems) {
            cartArray.add(item);
        }
        jsonBody.add("cart_items", cartArray);

        // Call API to fetch recommendations
        apiService.getRecommendations(jsonBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Parse response as JsonArray
                    JsonArray recommendationsArray = response.body().getAsJsonArray("recommendations");

                    List<RecommendedProduct> recommendations = new ArrayList<>();
                    for (int i = 0; i < recommendationsArray.size(); i++) {
                        JsonObject productObj = recommendationsArray.get(i).getAsJsonObject();

                        // Parse fields with null-safety checks
                        int productId = productObj.has("id") ? productObj.get("id").getAsInt() : -1;
                        String name = productObj.has("name") ? productObj.get("name").getAsString() : "Unknown";
                        double price = productObj.has("price") ? productObj.get("price").getAsDouble() : 0.0;
                        String imageUrl = productObj.has("imageUrl") ? productObj.get("imageUrl").getAsString() : "";
                        double averageRating = productObj.has("average_rating") ? productObj.get("average_rating").getAsDouble() : 0.0;

                        // Create product object and add to the list
                        RecommendedProduct product = new RecommendedProduct(productId, name, price, imageUrl, averageRating);
                        recommendations.add(product);
                    }

                    Log.d("KeranjangActivity", "Rekomendasi: " + recommendations);

                    // Set adapter with recommendations
                    recommendationAdapter = new RecommendationAdapter(KeranjangActivity.this, recommendations, KeranjangActivity.this);
                    rvListRekomendasi.setAdapter(recommendationAdapter);

                    // Update total product count and price
                    updateTotalProductAndPrice();

                } else {
                    Toast.makeText(KeranjangActivity.this, "Gagal memuat rekomendasi, masukkan produk ke keranjang terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("KeranjangActivity", "Gagal memuat rekomendasi: " + t.getMessage());
            }
        });
    }


    public void updateTotalProductAndPrice() {
        int totalProductCount = 0;
        double totalPrice = 0.0;

        // Calculate total products and price from cart items
        for (CartItem cartItem : cartAdapter.getCartItems()) {
            totalProductCount += cartItem.getQuantity();
            totalPrice += cartItem.getQuantity() * cartItem.getPrice();
        }

        // Check if recommendationAdapter is initialized before using
        if (recommendationAdapter != null) {
            Map<Integer, Integer> recommendedQuantities = recommendationAdapter.getSelectedQuantities();
            for (Map.Entry<Integer, Integer> entry : recommendedQuantities.entrySet()) {
                int productId = entry.getKey();
                int quantity = entry.getValue();

                if (quantity > 0) {
                    RecommendedProduct recommendedProduct = recommendationAdapter.getProductById(productId);
                    if (recommendedProduct != null) {
                        totalProductCount += quantity;
                        totalPrice += quantity * recommendedProduct.getPrice();
                    }
                }
            }
        }

        // Update TextViews for total product count and price
        tvProductCount.setText(totalProductCount + " produk");
        tvProductPrice.setText(formatCurrency(totalPrice));
    }

    private String formatCurrency(double value) {
        return String.format("Rp %,.0f", value);
    }

    private void checkout() {
        List<CheckoutItem> checkoutItems = new ArrayList<>();

        // Get items from cart
        for (CartItem cartItem : cartAdapter.getCartItems()) {
            CheckoutItem item = new CheckoutItem(
                    cartItem.getProduk_id(),
                    cartItem.getQuantity(),
                    cartItem.getPrice()
            );
            checkoutItems.add(item);
        }

        // Get items from recommendations
        Map<Integer, Integer> recommendedQuantities = recommendationAdapter.getSelectedQuantities();
        for (Map.Entry<Integer, Integer> entry : recommendedQuantities.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();

            // If product is added from recommendations, add to checkout list
            if (quantity > 0) {
                RecommendedProduct recommendedProduct = recommendationAdapter.getProductById(productId);
                if (recommendedProduct != null) {
                    CheckoutItem item = new CheckoutItem(
                            recommendedProduct.getId(),
                            quantity, // Use quantity from Map
                            recommendedProduct.getPrice()
                    );
                    checkoutItems.add(item);
                }
            }
        }

        // Send all items for checkout
        sendCheckoutToApi(checkoutItems);
    }

    private void sendCheckoutToApi(List<CheckoutItem> checkoutItems) {
        // Create a JSON request body as a JsonObject
        JsonObject jsonBody = new JsonObject();
        JsonArray jsonArray = new JsonArray();

        // Get user_id from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        jsonBody.addProperty("user_id", userId);

        // Add checkout items to JSON body
        for (CheckoutItem item : checkoutItems) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("product_id", item.getProductId());
            jsonObject.addProperty("quantity", item.getQuantity());
            jsonObject.addProperty("price", item.getPrice());
            jsonArray.add(jsonObject);
        }

        jsonBody.add("checkout_items", jsonArray);

        // Send the JsonObject to your API for checkout
        apiService.checkout(jsonBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    // Show success message
                    Toast.makeText(KeranjangActivity.this, "Checkout berhasil!", Toast.LENGTH_SHORT).show();

                    // Reset the cart after successful checkout
                    resetCart();

                    // Redirect to HistoryActivity
                    Intent intent = new Intent(KeranjangActivity.this, HistoryActivity.class);
                    startActivity(intent);
                    finish(); // Close KeranjangActivity
                } else {
                    // Show error message
                    Toast.makeText(KeranjangActivity.this, "Checkout gagal!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Checkout", "Error during checkout: " + t.getMessage());
                Toast.makeText(KeranjangActivity.this, "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void resetCart() {
        // Reset cart after successful checkout
        cartAdapter.clearCartItems();
        recommendationAdapter.clearSelectedQuantities();

        // Clear the display
        tvProductCount.setText("0 produk");
        tvProductPrice.setText(formatCurrency(0));

        // Optionally, reload the cart to reflect the changes
        getKeranjangItems();
    }
}

