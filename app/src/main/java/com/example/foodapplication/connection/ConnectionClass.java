package com.example.foodapplication.connection;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;

public class ConnectionClass {

    protected static String db = "foodapp";
    protected static String ip = "10.5.23";
    protected static String port = "3306";
    protected static String username = "root";
    protected static String password = "";

    public Connection conn(){
        Connection conn = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            String connectionString = "jdbc:mysql://127.0.0.1:" + port + "/" + db;
            conn = DriverManager.getConnection(connectionString,username,password);
        }
        catch (Exception e){
            Log.e("Error", Objects.requireNonNull(e.getMessage()));
        }
        return conn;
    }
}

