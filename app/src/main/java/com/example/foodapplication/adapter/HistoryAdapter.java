package com.example.foodapplication.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapplication.PembayaranActivity;
import com.example.foodapplication.R;
import com.example.foodapplication.connection.ApiServer;
import com.example.foodapplication.model.GeneralResponse;
import com.example.foodapplication.model.OrderHistory;
import com.example.foodapplication.model.ProdukHistory;
import com.example.foodapplication.network.ApiService;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<OrderHistory> orderHistoryList;

    public HistoryAdapter(Context context, List<OrderHistory> orderHistoryList) {
        this.context = context;
        this.orderHistoryList = orderHistoryList;
    }

    public void updateData(List<OrderHistory> newOrderHistoryList) {
        this.orderHistoryList = newOrderHistoryList; // Update the list with new data
        notifyDataSetChanged(); // Refresh the RecyclerView
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_riwayat, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        OrderHistory order = orderHistoryList.get(position);

        // Set data pada item tampilan
        holder.tvName.setText(order.getUserName());
        holder.tvJml.setText(order.getTotalQty() + " produk");
        holder.tvPrice.setText(formatCurrency(order.getTotalHarga()));
        Log.d("HistoryAdapter", "Tanggal Transaksi (Before Conversion): " + order.getTanggalTransaksi());

        String wibDate = convertToWIB(order.getTanggalTransaksi());
        holder.tvDate.setText(wibDate);  // Menampilkan waktu dalam zona WIB
        Log.d("HistoryAdapter", "Tanggal Transaksi (After Conversion): " + wibDate);

        holder.tvStatus.setText(order.getStatusTransaksi());

        // Ambil roleId dari SharedPreferences untuk cek hak akses
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int roleId = sharedPreferences.getInt("role_id", -1);

        // Sembunyikan btnStatus jika roleId adalah 2
        if (roleId == 2) {
            holder.btnStatus.setVisibility(View.GONE);
        } else {
            holder.btnStatus.setVisibility(View.VISIBLE); // Pastikan tombol terlihat untuk roleId lainnya
        }

        // Handle Konfirmasi Button
        holder.btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "6281519920344"; // Nomor telepon tanpa tanda "+" atau simbol lain
                String message = "Halo, lampirkan screenshot bukti pembayaran yaa :)";
                String whatsappUrl = "https://wa.me/" + phoneNumber + "?text=" + Uri.encode(message);

                PackageManager pm = context.getPackageManager();
                try {
                    // Cek apakah WhatsApp biasa terinstal
                    PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);

                    Intent waIntent = new Intent(Intent.ACTION_VIEW);
                    waIntent.setData(Uri.parse(whatsappUrl));
                    waIntent.setPackage("com.whatsapp");

                    // Mulai Intent untuk membuka WhatsApp
                    context.startActivity(waIntent);
                } catch (PackageManager.NameNotFoundException e) {
                    // Jika WhatsApp biasa tidak terinstal, cek WhatsApp Business
                    try {
                        PackageInfo info = pm.getPackageInfo("com.whatsapp.w4b", PackageManager.GET_META_DATA);

                        Intent waIntent = new Intent(Intent.ACTION_VIEW);
                        waIntent.setData(Uri.parse(whatsappUrl));
                        waIntent.setPackage("com.whatsapp.w4b");

                        // Mulai Intent untuk membuka WhatsApp Business
                        context.startActivity(waIntent);
                    } catch (PackageManager.NameNotFoundException ex) {
                        // Jika tidak ada aplikasi WhatsApp di perangkat, buka di browser
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl));
                        context.startActivity(browserIntent);
                    }
                }
            }
        });

        // Handle Bayar Button
        holder.btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PembayaranActivity.class);
                intent.putExtra("transaction_id", order.getTransactionId());
                context.startActivity(intent);
            }
        });

        // Handle Status Button untuk Admin
        holder.btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roleId == 1) {
                    showStatusDialog(order);
                } else {
                    Toast.makeText(context, "Anda tidak memiliki akses untuk melihat status ini.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle item click untuk detail pesanan
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderDetailDialog(order);
            }
        });
    }


    @Override
    public int getItemCount() {
        return orderHistoryList.size();
    }

    private void showStatusDialog(OrderHistory order) {
        // Buat dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_status_invoice, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); // Membuat latar dialog transparan
        }

        // Referensi ke elemen UI di dialog
        TextView tvStatusInvoice = dialogView.findViewById(R.id.tvStatusInvoice);
        RadioGroup rgStatusPembayaran = dialogView.findViewById(R.id.rgStatusPembayaran);
        RadioGroup rgStatusPemesanan = dialogView.findViewById(R.id.rgStatusPemesanan);
        MaterialButton btnUpdateStatus = dialogView.findViewById(R.id.btnUpdateStatus);



        // Set status pembayaran dan pemesanan dari order
        if (order.getStatusPembayaran().equals("Sudah dibayar")) {
            rgStatusPembayaran.check(R.id.rbSudahDibayar);
        } else {
            rgStatusPembayaran.check(R.id.rbBelumDibayar);
        }

        if (order.getStatusTransaksi().equals("Pending")) {
            rgStatusPemesanan.check(R.id.rbPending);
        } else if (order.getStatusTransaksi().equals("Proses Pembuatan")) {
            rgStatusPemesanan.check(R.id.rbProsesPembuatan);
        } else if (order.getStatusTransaksi().equals("Proses Pengiriman")) {
            rgStatusPemesanan.check(R.id.rbProsesPengiriman);
        } else if (order.getStatusTransaksi().equals("Pesanan Telah Sampai")) {
            rgStatusPemesanan.check(R.id.rbPesananSampai);
        }

        // Listener untuk mengubah status pemesanan otomatis jika status pembayaran sudah dibayar
        rgStatusPembayaran.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbSudahDibayar) {
                    rgStatusPemesanan.check(R.id.rbProsesPembuatan); // Set otomatis ke 'Proses Pembuatan'
                }
            }
        });

        // Tombol untuk menyimpan perubahan
        btnUpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newStatusPembayaran = rgStatusPembayaran.getCheckedRadioButtonId() == R.id.rbSudahDibayar ? "Sudah dibayar" : "Belum dibayar";
                String newStatusPemesanan = "Pending"; // Default
                if (rgStatusPemesanan.getCheckedRadioButtonId() == R.id.rbProsesPembuatan) {
                    newStatusPemesanan = "Proses Pembuatan";
                } else if (rgStatusPemesanan.getCheckedRadioButtonId() == R.id.rbProsesPengiriman) {
                    newStatusPemesanan = "Proses Pengiriman";
                }
                else if (rgStatusPemesanan.getCheckedRadioButtonId() == R.id.rbPesananSampai) {
                    newStatusPemesanan = "Pesanan Telah Sampai";
                }

                // Membuat JSON untuk mengirim status update ke API
                JsonObject statusUpdateRequest = new JsonObject();
                statusUpdateRequest.addProperty("transaction_id", order.getTransactionId());
                statusUpdateRequest.addProperty("status_pembayaran", newStatusPembayaran);
                statusUpdateRequest.addProperty("status_pemesanan", newStatusPemesanan);

                // Mengirim update status ke server
                ApiService apiService = ApiServer.getApiService();  // Pastikan kamu menginisialisasi ApiService dengan benar
                apiService.updateOrderStatus(statusUpdateRequest).enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(Call<GeneralResponse> call, retrofit2.Response<GeneralResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Berhasil diperbarui
                            Toast.makeText(context, "Status berhasil diperbarui", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();  // Tutup dialog setelah update

                            refreshOrderHistory();
                        } else {
                            // Gagal memperbarui status
                            Toast.makeText(context, "Gagal memperbarui status", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GeneralResponse> call, Throwable t) {
                        // Terjadi kesalahan jaringan
                        Toast.makeText(context, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Tampilkan dialog
        dialog.show();
    }

    private boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void refreshOrderHistory() {
        // Ambil userId dan roleId dari SharedPreferences atau sumber lain
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);  // Pastikan key sesuai dengan apa yang kamu gunakan di sesi login
        int roleId = sharedPreferences.getInt("role_id", -1);

        // Cek apakah userId dan roleId valid
        if (userId != -1 && roleId != -1) {
            ApiService apiService = ApiServer.getApiService();
            apiService.getOrderHistory(userId, roleId).enqueue(new Callback<List<OrderHistory>>() {
                @Override
                public void onResponse(Call<List<OrderHistory>> call, Response<List<OrderHistory>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Update RecyclerView dengan data terbaru
                        updateData(response.body());
                    } else {
                        Toast.makeText(context, "Gagal memuat data terbaru", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<OrderHistory>> call, Throwable t) {
                    Toast.makeText(context, "Gagal memuat data terbaru", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(context, "User ID atau Role ID tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }



    private void showOrderDetailDialog(OrderHistory order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_invoice, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Referensi elemen UI
        LinearLayout llProductList = dialogView.findViewById(R.id.llProductList);
        TextView tvStatusPembayaran = dialogView.findViewById(R.id.tvStatusPembayaran);
        TextView tvStatusPesanan = dialogView.findViewById(R.id.tvStatusPesanan);
        TextView tvAlamatPengiriman = dialogView.findViewById(R.id.tvAlamatPengiriman);
        TextView tvNomorHp = dialogView.findViewById(R.id.tvNomorHp);  // Nomor HP

        // Ambil role ID dari SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int roleId = sharedPreferences.getInt("role_id", -1);

        // Jika role ID adalah 1 (Admin), tampilkan nomor HP
        if (roleId == 1) {
            tvNomorHp.setVisibility(View.VISIBLE);
            tvNomorHp.setText("Nomor HP: " + (order.getNomorHp() != null ? order.getNomorHp() : "Tidak tersedia"));
        }

        // Set alamat pengiriman dengan pengecekan null
        tvAlamatPengiriman.setText("Alamat Pengiriman: " +
                (order.getAlamatPengiriman() != null ? order.getAlamatPengiriman() : "Tidak tersedia"));

        // Set status pembayaran dan pesanan dengan pengecekan null
        tvStatusPembayaran.setText("Status Pembayaran: " +
                (order.getStatusPembayaran() != null ? order.getStatusPembayaran() : "Tidak tersedia"));
        tvStatusPesanan.setText("Status Pesanan: " +
                (order.getStatusTransaksi() != null ? order.getStatusTransaksi() : "Tidak tersedia"));

        // Bersihkan daftar produk sebelum menambahkan produk baru
        llProductList.removeAllViews();

        // Tambahkan produk ke dalam layout
        if (order.getProductList() != null && !order.getProductList().isEmpty()) {
            for (ProdukHistory product : order.getProductList()) {
                View productView = inflater.inflate(R.layout.item_produkinvoice, null);
                TextView tvProductName = productView.findViewById(R.id.tvProductName);
                TextView tvProductQty = productView.findViewById(R.id.tvProductQty);
                TextView tvProductPrice = productView.findViewById(R.id.tvProductPrice);
                TextView tvProductTotalPrice = productView.findViewById(R.id.tvProductTotalPrice);

                // Set data produk
                tvProductName.setText(product.getName());
                tvProductQty.setText("Qty: " + product.getQuantity());
                tvProductPrice.setText(formatCurrency(product.getPrice()));
                double totalPrice = product.getQuantity() * product.getPrice();
                tvProductTotalPrice.setText(formatCurrency(totalPrice));

                llProductList.addView(productView);
            }
        } else {
            TextView tvEmpty = new TextView(context);
            tvEmpty.setText("Tidak ada produk dalam pesanan ini");
            llProductList.addView(tvEmpty);
        }

        dialog.show();
    }



    // Fungsi untuk format harga tanpa desimal
    private String formatCurrency(double value) {
        return String.format("Rp %,.0f", value);
    }

    // Fungsi untuk konversi waktu dari GMT ke WIB
    private String convertToWIB(String gmtTime) {
        // Format waktu yang diterima dari server
        SimpleDateFormat gmtFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));  // Zona waktu dari server (GMT)

        // Format untuk menampilkan dalam zona WIB
        SimpleDateFormat wibFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        wibFormat.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));  // Zona waktu WIB (Asia/Jakarta)

        try {
            // Parsing waktu GMT ke Date
            Date date = gmtFormat.parse(gmtTime);
            // Mengonversi ke waktu WIB
            return wibFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Jika parsing gagal, kembalikan waktu asli
        return gmtTime;
    }





    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvJml, tvStatus, tvDate;
        MaterialButton btnKonfirmasi, btnBayar, btnStatus;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvJml = itemView.findViewById(R.id.tvJml);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnKonfirmasi = itemView.findViewById(R.id.btnKonfirmasi);
            btnBayar = itemView.findViewById(R.id.btnBayar);
            btnStatus = itemView.findViewById(R.id.btnStatus);
        }
    }
}

