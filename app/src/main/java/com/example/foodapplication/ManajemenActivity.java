package com.example.foodapplication;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapplication.adapter.ManajemenAdapter;
import com.example.foodapplication.model.ProdukManajemen;
import com.example.foodapplication.model.UploadResponse;
import com.example.foodapplication.network.ApiService;
import com.example.foodapplication.connection.ApiServer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManajemenActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private RecyclerView rvManajemen;
    private ManajemenAdapter manajemenAdapter;
    private List<ProdukManajemen> produkList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manajemen);

        // Ambil role_id dari SharedPreferences dengan nama yang benar
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        int roleId = preferences.getInt("role_id", -1);  // Ambil role_id dari UserSession

        // Tambahkan log untuk melihat role_id
        Log.d("ManajemenActivity", "Role ID: " + roleId);  // Log role_id untuk melihat di Logcat

        // Cek apakah role_id == 1
        if (roleId != 1) {
            // Jika bukan role_id = 1, tampilkan pesan error dan kembalikan ke aktivitas lain atau tutup activity
            Log.d("ManajemenActivity", "User tidak memiliki akses, role_id: " + roleId);  // Log pesan bahwa user tidak memiliki akses
            Toast.makeText(this, "Anda tidak memiliki izin untuk mengakses halaman ini.", Toast.LENGTH_SHORT).show();
            finish();  // Menutup aktivitas atau kembalikan ke halaman sebelumnya
            return;
        }

        Log.d("ManajemenActivity", "User memiliki akses, role_id: " + roleId);  // Log pesan jika user memiliki akses

        rvManajemen = findViewById(R.id.rv_manajemen);
        rvManajemen.setLayoutManager(new LinearLayoutManager(this));

        // Fetch products from the API
        getProdukManajemen();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove default title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Back button handling
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(ManajemenActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        FloatingActionButton fabAddProduct = findViewById(R.id.fab_add_product);
        fabAddProduct.setOnClickListener(v -> manajemenAdapter.showEditDialog(null, false));  // false berarti mode tambah produk
    }

    public interface OnImageSelectedListener {
        void onImageSelected(Uri imageUri);
    }

    private OnImageSelectedListener onImageSelectedListener;

    public void setOnImageSelectedListener(OnImageSelectedListener listener) {
        this.onImageSelectedListener = listener;
    }
// Fungsi ini bisa dipanggil setelah login berhasil atau saat mendapatkan `roleid` dari API


    private void getProdukManajemen() {
        ApiService apiService = ApiServer.getApiService();
        apiService.getProdukManajemen().enqueue(new Callback<List<ProdukManajemen>>() {
            @Override
            public void onResponse(Call<List<ProdukManajemen>> call, Response<List<ProdukManajemen>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    produkList = response.body();
                    manajemenAdapter = new ManajemenAdapter(ManajemenActivity.this, produkList);
                    rvManajemen.setAdapter(manajemenAdapter);
                } else {
                    Toast.makeText(ManajemenActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProdukManajemen>> call, Throwable t) {
                Toast.makeText(ManajemenActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fungsi untuk membuka galeri dan memilih gambar
    public void openImageSelector() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            // Notify listener if set
            if (onImageSelectedListener != null) {
                onImageSelectedListener.onImageSelected(selectedImageUri);
            } else {
                manajemenAdapter.setSelectedImageUri(selectedImageUri); // Backup if listener is not set
            }
        }
    }

    // Function to refresh the RecyclerView after update
    public void refreshProductList() {
        getProdukManajemen();
    }
}
