package com.example.foodapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapplication.adapter.HistoryAdapter;
import com.example.foodapplication.model.OrderHistory;
import com.example.foodapplication.network.ApiService;
import com.example.foodapplication.connection.ApiServer;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private TextView tvNoFound;
    private HistoryAdapter historyAdapter;
    private ApiService apiService;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rvHistory = findViewById(R.id.rvHistory);
        tvNoFound = findViewById(R.id.tvNoFound);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hapus title aplikasi default
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Inisialisasi API Service
        apiService = ApiServer.getApiService();

        // Ambil user_id dan role_id dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);  // Ambil user_id yang tersimpan
        int roleId = sharedPreferences.getInt("role_id", -1);  // Ambil role_id yang tersimpan

        // Logging untuk memastikan nilai userId dan roleId
        Log.d("HistoryActivity", "userId: " + userId + ", roleId: " + roleId);

        // Cek apakah userId dan roleId valid
        if (userId == -1 || roleId == -1) {
            Toast.makeText(this, "User tidak ditemukan, silahkan login", Toast.LENGTH_SHORT).show();
            return;
        }

        // Panggil method untuk mengambil riwayat pesanan, gunakan roleId juga
        getOrderHistory(userId, roleId);
    }



    private void getOrderHistory(int userId, int roleId) {
        apiService.getOrderHistory(userId, roleId).enqueue(new Callback<List<OrderHistory>>() {
            @Override
            public void onResponse(Call<List<OrderHistory>> call, Response<List<OrderHistory>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderHistory> orderHistoryList = response.body();

                    if (orderHistoryList.isEmpty()) {
                        tvNoFound.setVisibility(View.VISIBLE);
                        rvHistory.setVisibility(View.GONE);
                    } else {
                        tvNoFound.setVisibility(View.GONE);
                        rvHistory.setVisibility(View.VISIBLE);

                        // Set adapter untuk RecyclerView
                        historyAdapter = new HistoryAdapter(HistoryActivity.this, orderHistoryList);
                        rvHistory.setAdapter(historyAdapter);
                    }
                } else {
                    Toast.makeText(HistoryActivity.this, "Gagal memuat data riwayat pesanan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderHistory>> call, Throwable t) {
                Log.e("HistoryActivity", "Error: " + t.getMessage());
                Toast.makeText(HistoryActivity.this, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
