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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapplication.adapter.ProductAdapter;
import com.example.foodapplication.model.CartRequest;
import com.example.foodapplication.model.GeneralResponse;
import com.example.foodapplication.model.Produk;
import com.example.foodapplication.network.ApiService;
import com.example.foodapplication.connection.ApiServer;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView rvProduk;
    private ProductAdapter productAdapter;
    private ApiService apiService;
    private TextView tvCategoryName;
    private Button btnMasukkanKeranjang;

    private int userId = 1; // Contoh user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Ambil ID kategori dan nama kategori dari Intent
        int categoryId = getIntent().getIntExtra("CATEGORY_ID", 0);
        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");


        int productId = getIntent().getIntExtra("PRODUCT_ID", -1);


        if (categoryId != 0) {
            loadProductsByCategory(categoryId); // Fungsi untuk memuat produk berdasarkan kategori
        }

        // Jika ada PRODUCT_ID yang dikirim, kamu bisa menyorot atau men-scroll ke produk tersebut
        if (productId != -1) {
            highlightProduct(productId); // Fungsi untuk menyorot produk
        }
        // Setup AppBar dan tampilkan nama kategori
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hapus title aplikasi default
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        tvCategoryName = findViewById(R.id.tvCategoryName);
        if (categoryName != null) {
            tvCategoryName.setText(categoryName);
        }

        // Setup tombol back
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(OrderActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Setup RecyclerView
        rvProduk = findViewById(R.id.rvProduk);
        rvProduk.setLayoutManager(new GridLayoutManager(this, 2));

        // Inisialisasi API Service
        apiService = ApiServer.getApiService();

        // Ambil user_id dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);  // Ambil user_id

        if (userId == -1) {
            Toast.makeText(this, "User tidak ditemukan, silahkan login", Toast.LENGTH_SHORT).show();
            return;  // Berhenti jika user belum login
        }

        // Ambil produk berdasarkan kategori dan user_id
        getProdukByCategory(categoryId, userId);

        // Setup "Masukkan Keranjang" button
        btnMasukkanKeranjang = findViewById(R.id.btnCheckOut);
        btnMasukkanKeranjang.setOnClickListener(v -> addToCart());
    }

    private void loadProductsByCategory(int categoryId) {
        // Logika untuk memuat produk berdasarkan kategori
    }

    private void highlightProduct(int productId) {
        // Logika untuk menyorot produk tertentu (misal scroll ke produk tersebut)
    }
    private void getProdukByCategory(int categoryId, int userId) {
        apiService.getProdukByCategory(categoryId, userId).enqueue(new Callback<List<Produk>>() {
            @Override
            public void onResponse(Call<List<Produk>> call, Response<List<Produk>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Produk> produkList = response.body();
                    if (!produkList.isEmpty()) {
                        productAdapter = new ProductAdapter(OrderActivity.this, produkList);
                        rvProduk.setAdapter(productAdapter);

                        // Tambahkan views untuk setiap produk hanya saat produk pertama kali dimuat
                        for (Produk produk : produkList) {
                            incrementProductViews(produk.getId());
                        }
                    } else {
                        Toast.makeText(OrderActivity.this, "Tidak ada produk", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OrderActivity.this, "Tidak ada produk", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Produk>> call, Throwable t) {
                Log.e("OrderActivity", "Gagal mendapatkan produk: " + t.getMessage());
            }
        });
    }




    private void addToCart() {
        // Mengambil user_id dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1); // Ambil user_id yang tersimpan

        if (userId == -1) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
            return; // User belum login, hentikan proses
        }

        Map<Integer, Integer> selectedQuantities = productAdapter.getSelectedQuantities(); // Dapatkan produk yang dipilih beserta kuantitasnya

        for (Map.Entry<Integer, Integer> entry : selectedQuantities.entrySet()) {
            int productId = entry.getKey();  // ID Produk
            int quantity = entry.getValue();  // Kuantitas yang dipilih

            // Kirim kuantitas meskipun 0
            Produk produk = productAdapter.getProductById(productId);
            if (produk != null) {
                double productPrice = produk.getPrice();
                CartRequest cartRequest = new CartRequest(userId, productId, productPrice, quantity);

                apiService.addToCart(cartRequest).enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                        if (response.isSuccessful()) {
                            if (quantity == 0) {

                            } else {
                                Toast.makeText(OrderActivity.this, "Produk berhasil dimasukkan ke keranjang", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("OrderActivity", "Error Response: " + response.code() + " - " + response.message());
                            Log.e("OrderActivity", "Error Body: " + response.errorBody().toString());
                            Toast.makeText(OrderActivity.this, "Gagal memasukkan ke keranjang", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GeneralResponse> call, Throwable t) {
                        Log.e("OrderActivity", "Gagal memasukkan ke keranjang: " + t.getMessage());
                    }
                });
            }
        }
    }


    // Tambahkan ini di OrderActivity untuk memperbarui views produk
    public void incrementProductViews(int productId) {
        apiService.incrementProductViews(productId).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("OrderActivity", "Views produk berhasil diupdate");
                } else {
                    Log.e("OrderActivity", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Log.e("OrderActivity", "Gagal memperbarui views produk: " + t.getMessage());
            }
        });
    }





}
