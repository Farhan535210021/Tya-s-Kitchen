package com.example.foodapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.foodapplication.model.LoginRequest;
import com.example.foodapplication.model.LoginResponse;
import com.example.foodapplication.network.ApiService;
import com.example.foodapplication.connection.ApiServer;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private EditText inputUser, inputPassword;
    private MaterialButton btnLogin, btnRegister;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Cek apakah pengguna sudah login
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1); // Default -1 jika tidak ditemukan

        // Jika user_id ditemukan di SharedPreferences, arahkan ke MainActivity
        if (userId != -1) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish(); // Tutup Login Activity
            return; // Hentikan eksekusi onCreate() lebih lanjut
        }

        // Jika tidak login, tetap tampilkan halaman login
        setContentView(R.layout.activity_login); // Sesuaikan dengan nama layout XML kamu

        // Inisialisasi Views
        inputUser = findViewById(R.id.inputUser);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Inisialisasi Retrofit API
        apiService = ApiServer.getApiService();

        // Tombol Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Tombol Register (Arahkan ke halaman Register)
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent ke halaman Register
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }


    // Fungsi untuk Login User
    private void loginUser() {
        String username = inputUser.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        // Validasi input
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(Login.this, "Field tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        // Membuat LoginRequest Object
        LoginRequest loginRequest = new LoginRequest(username, password);

        // Panggil API login
        apiService.loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int userId = response.body().getUserId();  // Ambil user_id dari response API
                    int roleId = response.body().getRoleid();  // Ambil role_id dari response API


                    // Simpan userId dan roleId ke SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("user_id", userId);  // Simpan user_id
                    editor.putInt("role_id", roleId);  // Simpan role_id
                    editor.putString("username", response.body().getUsername());
                    editor.apply();

                    Toast.makeText(Login.this, "Login Berhasil!", Toast.LENGTH_SHORT).show();

                    // Arahkan ke halaman utama setelah login
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();  // Tutup halaman login
                } else {
                    Toast.makeText(Login.this, "Login Gagal! Periksa Username atau Password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(Login.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
