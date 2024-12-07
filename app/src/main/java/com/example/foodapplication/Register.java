package com.example.foodapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.foodapplication.model.User;
import com.example.foodapplication.network.ApiService;
import com.example.foodapplication.connection.ApiServer;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    private EditText etUsername, etPassword, etNomorHP, etAlamat;
    private Button btnRegister;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Inisialisasi View
        etUsername = findViewById(R.id.InUser);
        etPassword = findViewById(R.id.inPassword);
        etNomorHP = findViewById(R.id.inPhone); // ganti dari etEmail menjadi etNomorHP
        etAlamat = findViewById(R.id.inAddress);
        btnRegister = findViewById(R.id.btnRegister);

        // Inisialisasi Retrofit API
        apiService = ApiServer.getApiService();

        // Handle klik tombol register
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String nomorHP = etNomorHP.getText().toString();
        String alamat = etAlamat.getText().toString();
        int roleID = 2;

        // Membuat objek User
        User user = new User(username, password, nomorHP, alamat, roleID); // menggantikan email dengan nomor HP

        // Panggil API register
        apiService.registerUser(user).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Register.this, "Register berhasil!", Toast.LENGTH_SHORT).show();

                    // Setelah berhasil register, arahkan ke halaman login
                    Intent intent = new Intent(Register.this, Login.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        String errorMessage = response.errorBody().string();
                        if (errorMessage.contains("Username sudah terdaftar")) {
                            Toast.makeText(Register.this, "Username sudah ada!", Toast.LENGTH_SHORT).show();
                        } else if (errorMessage.contains("Nomor HP sudah terdaftar")) {
                            Toast.makeText(Register.this, "Nomor HP sudah terdaftar!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Register.this, "Register gagal: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(Register.this, "Terjadi kesalahan!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(Register.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
