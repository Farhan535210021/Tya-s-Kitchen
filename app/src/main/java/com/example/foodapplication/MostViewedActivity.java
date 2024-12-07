package com.example.foodapplication;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapplication.adapter.MostViewedAdapter;
import com.example.foodapplication.model.MostViewed;
import com.example.foodapplication.network.ApiService;
import com.example.foodapplication.connection.ApiServer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MostViewedActivity extends AppCompatActivity {

    private RecyclerView rvMostViewed;
    private MostViewedAdapter mostViewedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_most_viewed);

        rvMostViewed = findViewById(R.id.rvMostviewed);
        rvMostViewed.setLayoutManager(new LinearLayoutManager(this));

        // Fetch most viewed products from API
        fetchMostViewedProducts();
    }

    private void fetchMostViewedProducts() {
        ApiService apiService = ApiServer.getApiService();
        apiService.getMostViewedProducts().enqueue(new Callback<List<MostViewed>>() {
            @Override
            public void onResponse(Call<List<MostViewed>> call, Response<List<MostViewed>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MostViewed> mostViewedList = response.body();

                    // Set Adapter with parsed products
                    mostViewedAdapter = new MostViewedAdapter(MostViewedActivity.this, mostViewedList);
                    rvMostViewed.setAdapter(mostViewedAdapter);
                } else {
                    Toast.makeText(MostViewedActivity.this, "Failed to load most viewed products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MostViewed>> call, Throwable t) {
                Toast.makeText(MostViewedActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}
