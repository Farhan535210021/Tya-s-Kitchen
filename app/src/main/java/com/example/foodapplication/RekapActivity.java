package com.example.foodapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapplication.adapter.RekapAdapter;
import com.example.foodapplication.model.ProductData;
import com.example.foodapplication.model.SalesReport;
import com.example.foodapplication.model.TransactionDetail;
import com.example.foodapplication.model.TransactionGroup;
import com.example.foodapplication.network.ApiService;
import com.example.foodapplication.connection.ApiServer;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RekapActivity extends AppCompatActivity {

    private TextView tvTotalPendapatan, tvTotalPesanan, tvTotalProdukTerjual;
    private Spinner spinnerYear, spinnerMonth;
    private RecyclerView rvTransactionDetails;
    private RekapAdapter rekapAdapter;
    private String selectedYear;
    private String selectedMonth;
    private LineChart lineChart;
    private PieChart pieChartPaketMakanan, pieChartMakananBerat, pieChartJajananKue, pieChartDesserts, pieChartSpecialDish ; // Tambahkan ini


    private final Map<String, String> monthMap = new HashMap<String, String>() {{
        put("Januari", "01");
        put("Februari", "02");
        put("Maret", "03");
        put("April", "04");
        put("Mei", "05");
        put("Juni", "06");
        put("Juli", "07");
        put("Agustus", "08");
        put("September", "09");
        put("Oktober", "10");
        put("November", "11");
        put("Desember", "12");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap);
        lineChart = findViewById(R.id.lineChart);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pieChartPaketMakanan = findViewById(R.id.pieChartPaketMakanan);
        pieChartMakananBerat = findViewById(R.id.pieChartMakananBerat);
        pieChartJajananKue = findViewById(R.id.pieChartJajananKue);
        pieChartDesserts = findViewById(R.id.pieChartDesserts);
        pieChartSpecialDish = findViewById(R.id.pieChartSpecialDish);



        toolbar.setNavigationOnClickListener(v -> finish());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvTotalPendapatan = findViewById(R.id.tv_total_pendapatan);
        tvTotalPesanan = findViewById(R.id.tv_total_pesanan);
        tvTotalProdukTerjual = findViewById(R.id.tv_total_produk_terjual);
        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        rvTransactionDetails = findViewById(R.id.rv_transaction_details);

        rvTransactionDetails.setLayoutManager(new LinearLayoutManager(this));
        rekapAdapter = new RekapAdapter(this, new ArrayList<>()); // Use an empty list initially

        rvTransactionDetails.setAdapter(rekapAdapter);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);

        setSpinnerSelection(spinnerYear, String.valueOf(currentYear));
        setSpinnerSelection(spinnerMonth, getMonthName(currentMonth));

        selectedYear = String.valueOf(currentYear);
        selectedMonth = monthMap.get(getMonthName(currentMonth));
        loadSalesReportData(selectedYear, selectedMonth);

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = parent.getItemAtPosition(position).toString();
                if (selectedMonth != null) {
                    loadSalesReportData(selectedYear, monthMap.get(selectedMonth));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = parent.getItemAtPosition(position).toString();
                if (selectedYear != null) {
                    loadSalesReportData(selectedYear, monthMap.get(selectedMonth));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


    }
    private void setupPieChart(PieChart pieChart, List<ProductData> data, String label) {
        if (data == null || data.isEmpty()) {
            pieChart.clear();
            return;
        }

        // Langkah 1: Gabungkan produk dengan nama yang sama
        Map<String, Integer> groupedData = new HashMap<>();
        for (ProductData product : data) {
            String namaProduk = product.getNamaProduk();
            int qtyProduk = product.getTotalQty();

            // Tambahkan ke map, jumlahkan jika sudah ada
            groupedData.put(namaProduk, groupedData.getOrDefault(namaProduk, 0) + qtyProduk);
        }

        // Langkah 2: Buat PieEntry dari data yang digabungkan
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : groupedData.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        // Langkah 3: Atur data untuk PieChart
        PieDataSet dataSet = new PieDataSet(entries, label);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPieLabel(float value, PieEntry pieEntry) {
                return String.valueOf((int) value); // Konversi ke integer tanpa desimal
            }
        });

        pieChart.setData(pieData);
        pieChart.setDrawHoleEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelTextSize(10f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }




    private Map<Integer, List<ProductData>> groupProductsByCategory(List<TransactionDetail> products) {
        Map<Integer, List<ProductData>> groupedData = new HashMap<>();

        for (TransactionDetail product : products) {
            int categoryId = product.getCategoryId(); // Pastikan `TransactionDetail` punya getter `getCategoryId()`
            ProductData productData = new ProductData();
            productData.setNamaProduk(product.getNamaProduk());
            productData.setTotalQty(product.getQtyProduk());

            if (!groupedData.containsKey(categoryId)) {
                groupedData.put(categoryId, new ArrayList<>());
            }
            groupedData.get(categoryId).add(productData);
        }

        return groupedData;
    }


    private void loadPieChartData(String year, String month) {
        ApiService apiService = ApiServer.getApiService();
        Call<SalesReport> call = apiService.getSalesReport(year, month);

        call.enqueue(new Callback<SalesReport>() {
            @Override
            public void onResponse(Call<SalesReport> call, Response<SalesReport> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SalesReport salesReport = response.body();

                    if (salesReport.getTransactionDetails() != null) {
                        List<TransactionDetail> products = new ArrayList<>();
                        for (TransactionGroup group : salesReport.getTransactionDetails()) {
                            products.addAll(group.getProducts());
                        }

                        // Grouping berdasarkan category_id
                        Map<Integer, List<ProductData>> groupedData = groupProductsByCategory(products);

                        // Isi masing-masing Pie Chart
                        setupPieChart(pieChartPaketMakanan, groupedData.get(1), "Paket Makanan");
                        setupPieChart(pieChartMakananBerat, groupedData.get(2), "Makanan Berat");
                        setupPieChart(pieChartJajananKue, groupedData.get(3), "Jajanan Kue");
                        setupPieChart(pieChartDesserts, groupedData.get(4), "Desserts");
                        setupPieChart(pieChartSpecialDish, groupedData.get(5), "Special Dish");
                    }
                } else {
                    Toast.makeText(RekapActivity.this, "Gagal memuat data PieChart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SalesReport> call, Throwable t) {
                Toast.makeText(RekapActivity.this, "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private String formatTransactionDate(String transactionDate) {
        try {
            // Asumsikan tanggal berasal dari server dengan format "dd"
            return transactionDate; // Tanggal sudah dalam format "dd"
        } catch (Exception e) {
            e.printStackTrace();
            return transactionDate; // Kembalikan tanggal asli jika terjadi error
        }
    }




    private void setupLineChart(List<TransactionGroup> transactionGroups) {
        List<Entry> entries = new ArrayList<>();
        List<String> dateLabels = new ArrayList<>(); // Untuk menyimpan label tanggal
        int index = 0;

        for (TransactionGroup group : transactionGroups) {
            double totalTransactionAmount = 0;
            for (TransactionDetail product : group.getProducts()) {
                totalTransactionAmount += product.getTotalHarga();
            }
            entries.add(new Entry(index++, (float) totalTransactionAmount));

            // Format tanggal transaksi ke dd/MM
            String formattedDate = formatTransactionDate(group.getTransactionDate());
            Log.d("LineChart", "Tanggal: " + group.getTransactionDate() + ", Formatted: " + formattedDate); // Debug
            dateLabels.add(formattedDate);
        }

        LineDataSet dataSet = new LineDataSet(entries, "Total Penjualan");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(12f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Konfigurasi sumbu X untuk menggunakan tanggal
        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < dateLabels.size()) {
                    return dateLabels.get(index);
                } else {
                    return "";
                }
            }
        });

        lineChart.getXAxis().setGranularity(1f); // Jeda antar label
        lineChart.getXAxis().setLabelCount(dateLabels.size()); // Jumlah label
        lineChart.getXAxis().setTextColor(Color.BLACK); // Warna label agar terlihat jelas
        lineChart.invalidate();
        lineChart.getDescription().setEnabled(false);
        dataSet.setDrawValues(false);
        lineChart.animateY(1000);
    }








    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            if (spinner.getAdapter().getItem(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private String getMonthName(int monthIndex) {
        String[] monthNames = getResources().getStringArray(R.array.month_array);
        return monthNames[monthIndex];
    }

    private void loadSalesReportData(String year, String month) {
        ApiService apiService = ApiServer.getApiService();
        Call<SalesReport> call = apiService.getSalesReport(year, month);

        call.enqueue(new Callback<SalesReport>() {
            @Override
            public void onResponse(Call<SalesReport> call, Response<SalesReport> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SalesReport salesReport = response.body();

                    tvTotalPendapatan.setText(formatToCurrency(salesReport.getTotalPendapatan()));
                    tvTotalPesanan.setText(String.valueOf(salesReport.getTotalPesanan()));
                    tvTotalProdukTerjual.setText(String.valueOf(salesReport.getTotalProdukTerjual()));

                    List<TransactionGroup> transactionGroups = salesReport.getTransactionDetails();
                    if (transactionGroups != null) {
                        rekapAdapter.updateData(transactionGroups); // Pass the list of transaction groups
                        setupLineChart(transactionGroups);
                    } else {
                        rekapAdapter.updateData(new ArrayList<>()); // Pass empty list if no data
                        lineChart.clear();
                    }

                    loadPieChartData(year, month);
                } else {
                    setDefaultValues();
                    Toast.makeText(RekapActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SalesReport> call, Throwable t) {
                setDefaultValues();
                Toast.makeText(RekapActivity.this, "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatToCurrency(double amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        currencyFormat.setMaximumFractionDigits(0);
        return currencyFormat.format(amount);
    }

    private void setDefaultValues() {
        tvTotalPendapatan.setText("-");
        tvTotalPesanan.setText("-");
        tvTotalProdukTerjual.setText("-");
        rekapAdapter.updateData(new ArrayList<>()); // Empty list
        lineChart.clear();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
