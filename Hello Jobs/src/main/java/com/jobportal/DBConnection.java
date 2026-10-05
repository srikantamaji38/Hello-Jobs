package com.jobportal;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    // =========================================================
    // CHANGE THESE 3 VALUES ACCORDING TO YOUR MYSQL SETUP
    // =========================================================

    private static final String URL =
            "jdbc:mysql://localhost:3306/sarkari_job_portal?useSSL=false&serverTimezone=Asia/Kolkata";

    private static final String USERNAME = "root";

    private static final String PASSWORD = "srikanta@123";

    // =========================================================

    public static Connection getConnection() throws Exception {

        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}