package com.example.foodapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.foodapplication.model.User;
import com.example.foodapplication.network.ApiService;
import com.example.foodapplication.connection.ApiServer;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private EditText etName, etNomor, etAlamat, etPassword;
    private MaterialButton btnUpdate;
    private ApiService apiService;
    private String userId;  // Mengambil user_id dari SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Inisialisasi API service
        apiService = ApiServer.getApiService();

        // Ambil user_id dari SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = String.valueOf(preferences.getInt("user_id", -1));

        // Inisialisasi views
        etName = findViewById(R.id.etName);
        etNomor = findViewById(R.id.etPhone);
        etAlamat = findViewById(R.id.etAlamat);
        etPassword = findViewById(R.id.etPassword);
        btnUpdate = findViewById(R.id.btnUpdate);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);  // Tambahkan ini

        // Fetch user data
        getUserProfile();

        // Action untuk tombol update
        btnUpdate.setOnClickListener(v -> updateUserProfile());

        // Tambahkan action untuk tombol logout
        btnLogout.setOnClickListener(v -> logoutUser());
    }

    // Fungsi logout
    private void logoutUser() {
        // Hapus data session dari SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();  // Hapus semua data
        editor.apply();

        // Arahkan ke halaman login setelah logout
        Intent intent = new Intent(ProfileActivity.this, Login.class);
        startActivity(intent);
        finish();  // Tutup activity Profile agar tidak bisa kembali ke sini setelah logout
    }


    // Fungsi untuk mendapatkan profil pengguna
    private void getUserProfile() {
        apiService.getUserProfile(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    // Set data ke EditText
                    etName.setText(user.getUsername());
                    etNomor.setText(user.getNomorHp());
                    etAlamat.setText(user.getAlamat());  // Ambil alamat dari kolom 'alamat'
                    etPassword.setText("");  // Password tidak diambil dari server, biarkan kosong
                } else {
                    Toast.makeText(ProfileActivity.this, "Gagal mengambil data profil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fungsi untuk mengupdate profil pengguna
    private void updateUserProfile() {
        String newName = etName.getText().toString();
        String newNomor = etNomor.getText().toString();
        String newAlamat= etAlamat.getText().toString();  // Ini sebenarnya adalah alamat
        String newPassword = etPassword.getText().toString();  // Bisa kosong jika tidak diubah

        // Validasi input
        if (newName.isEmpty() || newNomor.isEmpty() || newAlamat.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Membuat object User dengan data baru
        User updatedUser = new User(newName, newPassword.isEmpty() ? null : newPassword, newNomor, newAlamat, 1);  // roleID diset ke 1 sebagai contoh

        // Panggil API untuk update profil
        apiService.updateUserProfile(userId, updatedUser).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Profil berhasil diupdate", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Gagal mengupdate profil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
