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

import com.example.foodapplication.adapter.RatingAdapter;
import com.example.foodapplication.model.Transaksi;
import com.example.foodapplication.network.ApiService;
import com.example.foodapplication.connection.ApiServer;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingActivity extends AppCompatActivity {
    private RecyclerView rvRating;
    private TextView tvNoFound;
    private RatingAdapter ratingAdapter;
    private List<Transaksi> ratingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        rvRating = findViewById(R.id.rvRating);
        tvNoFound = findViewById(R.id.tvNoFound);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove default title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Back button handling
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(RatingActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Set RecyclerView's LayoutManager
        rvRating.setLayoutManager(new LinearLayoutManager(this));

        // Fetch data from the server
        getRatings();
    }

    private void getRatings() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        int roleId = sharedPreferences.getInt("role_id", -1);

        Log.d("RatingActivity", "userId: " + userId + ", roleId: " + roleId);

        if (userId == -1 || roleId == -1) {
            Toast.makeText(RatingActivity.this, "User ID atau Role ID tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiServer.getApiService();

        apiService.getRatingList("Pesanan Telah Sampai", userId, roleId).enqueue(new Callback<List<Transaksi>>() {
            @Override
            public void onResponse(Call<List<Transaksi>> call, Response<List<Transaksi>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ratingList = response.body();
                    Log.d("RatingActivity", "Full response: " + response.body());

                    if (ratingList.isEmpty()) {
                        tvNoFound.setVisibility(View.VISIBLE);
                        rvRating.setVisibility(View.GONE);
                    } else {
                        tvNoFound.setVisibility(View.GONE);
                        rvRating.setVisibility(View.VISIBLE);

                        ratingAdapter = new RatingAdapter(RatingActivity.this, ratingList);
                        rvRating.setAdapter(ratingAdapter);
                    }
                } else {
                    Log.e("RatingActivity", "Failed response: " + response.errorBody());
                    Toast.makeText(RatingActivity.this, "Failed to load ratings", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Transaksi>> call, Throwable t) {
                Log.e("RatingActivity", "Error fetching ratings: " + t.getMessage());
                Toast.makeText(RatingActivity.this, "Error fetching ratings: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void refreshRatings() {
        getRatings(); // Reuse the existing method to fetch data and refresh the RecyclerView
    }

}
