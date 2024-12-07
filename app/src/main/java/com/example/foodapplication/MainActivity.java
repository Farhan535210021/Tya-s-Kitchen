package com.example.foodapplication;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapplication.adapter.CategoriesAdapter;
import com.example.foodapplication.adapter.TrendingAdapter;
import com.example.foodapplication.connection.ApiServer;
import com.example.foodapplication.model.Category;
import com.example.foodapplication.model.MostViewed;
import com.example.foodapplication.model.Trending;
import com.example.foodapplication.network.ApiService;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvCategories, rvTrending;
    private CategoriesAdapter categoriesAdapter;
    private TrendingAdapter trendingAdapter;
    private List<Category> categoryList;
    private List<Trending> trendingList;
    private TextView tvLocation, tvHistory;
    private ImageView ivSalesReport;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Inisialisasi RecyclerView
        rvCategories = findViewById(R.id.rvCategories);
        rvTrending = findViewById(R.id.rvTrending);





        // Setup LocationRequest
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // Interval untuk pembaruan lokasi
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Setup layout manager
        rvCategories.setLayoutManager(new GridLayoutManager(this, 3)); // 3 kolom untuk kategori
        rvTrending.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Isi data kategori
        categoryList = new ArrayList<>();
        categoryList.add(new Category(1, "Paket Makanan", R.drawable.logo_categories1));
        categoryList.add(new Category(2, "Makanan Berat", R.drawable.logo_categories2));
        categoryList.add(new Category(3, "Jajanan Kue", R.drawable.logo_categories3));
        categoryList.add(new Category(4, "Desserts", R.drawable.logo_categories4));
        categoryList.add(new Category(5, "Special Dish", R.drawable.logo_categories5));

        // Setup adapter kategori
        categoriesAdapter = new CategoriesAdapter(this, categoryList, category -> {
            // Navigasi ke OrderActivity dengan mengirimkan categoryId dan categoryName
            Intent intent = new Intent(MainActivity.this, OrderActivity.class);
            intent.putExtra("CATEGORY_ID", category.getId());  // Mengirim ID kategori sebagai integer
            intent.putExtra("CATEGORY_NAME", category.getName());  // Mengirim nama kategori untuk AppBar
            startActivity(intent);
        });

        rvCategories.setAdapter(categoriesAdapter);

        // Ambil TextView untuk Account Name
        TextView tvAccountName = findViewById(R.id.tvAccountname);

        // Ambil username dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "User");  // Default ke "User" jika tidak ada
        int roleId = sharedPreferences.getInt("role_id", -1);
        // Set TextView dengan format "Good Morning, [Username]"
        tvAccountName.setText("Hello there, " + username);

        rvTrending.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        fetchTrendingProducts();



        // Inisialisasi FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Inisialisasi TextView dan CardView
        tvLocation = findViewById(R.id.tvLocation);
        tvHistory = findViewById(R.id.tvHistory);

        findViewById(R.id.cvHistory).setOnClickListener(v -> {
            // Navigasi ke HistoryActivity saat CardView History ditekan
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });


        // Inisialisasi ImageView untuk ikon profil
        ImageView ivProfile = findViewById(R.id.ivProfile);

        // Handle klik pada ikon profil
        ivProfile.setOnClickListener(v -> {
            // Intent untuk berpindah ke halaman ProfileActivity
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Inisialisasi callback untuk pembaruan lokasi
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Toast.makeText(MainActivity.this, "Lokasi tidak tersedia", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Ambil lokasi terbaru
                for (android.location.Location location : locationResult.getLocations()) {
                    if (location != null) {
                        // Gunakan Geocoder untuk mendapatkan nama kota dan provinsi
                        getLocationName(location.getLatitude(), location.getLongitude());
                    }
                }
            }
        };

        tvLocation.setOnClickListener(v -> {
            // Cek izin lokasi dan ambil lokasi saat ini
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                startLocationUpdates();
            }
        });
        // Inisialisasi ivSalesReport
        ivSalesReport = findViewById(R.id.ivSalesReport);
        if (roleId == 1) {
            ivSalesReport.setVisibility(View.VISIBLE);
            ivSalesReport.setOnClickListener(v -> {
                // Navigasi ke RekapActivity
                Intent intent = new Intent(MainActivity.this, RekapActivity.class);
                startActivity(intent);
            });
        } else {
            ivSalesReport.setVisibility(View.GONE);
        }


        // Bottom Navigation View
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (roleId == 2) {
            bottomNavigationView.getMenu().findItem(R.id.page_3).setVisible(false);
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.page_1) {
                // Aksi untuk item Keranjang
                Intent intentKeranjang = new Intent(MainActivity.this, KeranjangActivity.class);
                startActivity(intentKeranjang);
                return true;
            } else if (itemId == R.id.page_2) {
                // Aksi untuk item History
                Intent intentHistory = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intentHistory);
                return true;
            } else if (itemId == R.id.page_3) {
                // Aksi untuk item Manajemen
                Intent intentManajemen = new Intent(MainActivity.this, ManajemenActivity.class);
                startActivity(intentManajemen);
                return true;
            } else if (itemId == R.id.page_4) {
                // Aksi untuk item Rating
                Intent intentRating = new Intent(MainActivity.this, RatingActivity.class);
                startActivity(intentRating);
                return true;
            }
            return false;
        });

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void getLocationName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String city = address.getLocality(); // Nama kota
                String state = address.getAdminArea(); // Nama provinsi

                String locationString = city + ", " + state;

                if (locationString.length() > 20) {  // Contoh batas panjang
                    locationString = locationString.substring(0, 17) + "...";
                }

                tvLocation.setText(locationString);
            } else {
                Toast.makeText(MainActivity.this, "Tidak dapat menemukan lokasi", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Gagal mendapatkan lokasi", Toast.LENGTH_SHORT).show();
        }
    }


    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        getLocationName(location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(MainActivity.this, "Lokasi tidak tersedia", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Panggil super
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Izin lokasi tidak diberikan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchTrendingProducts() {
        ApiService apiService = ApiServer.getApiService();
        apiService.getMostViewedProducts().enqueue(new Callback<List<MostViewed>>() {
            @Override
            public void onResponse(Call<List<MostViewed>> call, Response<List<MostViewed>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MostViewed> trendingList = response.body();

                    // Set adapter for rvTrending with most viewed products
                    trendingAdapter = new TrendingAdapter(MainActivity.this, trendingList, product -> {
                        // Handle click event here (open product details or similar action)
                    });
                    rvTrending.setAdapter(trendingAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load trending products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MostViewed>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
