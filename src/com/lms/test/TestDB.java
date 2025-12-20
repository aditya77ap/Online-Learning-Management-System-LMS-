package com.lms.test;

import com.lms.util.DBConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("Testing Database Connection...");
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("Connection successful!");
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
                System.out.println("Users found:");
                while (rs.next()) {
                    System.out.println(" - " + rs.getString("email") + " (" + rs.getString("role") + ")");
                }
            }
        } catch (Exception e) {
            System.err.println("Connection failed!");
            e.printStackTrace();
        }
    }
}
