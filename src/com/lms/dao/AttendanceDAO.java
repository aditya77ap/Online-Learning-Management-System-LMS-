package com.lms.dao;

import com.lms.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceDAO {

    public boolean markAttendance(int studentId, int courseId, String status, java.sql.Date date) {
        String query = "INSERT INTO attendance_records (student_id, course_id, status, date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setString(3, status);
            pstmt.setDate(4, date);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkAttendanceExists(int studentId, int courseId, java.sql.Date date) {
        String query = "SELECT COUNT(*) FROM attendance_records WHERE student_id = ? AND course_id = ? AND date = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setDate(3, date);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAttendance(int studentId, int courseId, String status, java.sql.Date date) {
        String query = "UPDATE attendance_records SET status = ? WHERE student_id = ? AND course_id = ? AND date = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, studentId);
            pstmt.setInt(3, courseId);
            pstmt.setDate(4, date);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void createSession(int courseId, String token) {
        // First delete old sessions for this course to keep it clean
        String deleteQuery = "DELETE FROM active_sessions WHERE course_id = ?";
        String insertQuery = "INSERT INTO active_sessions (course_id, token) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement delStmt = conn.prepareStatement(deleteQuery)) {
                delStmt.setInt(1, courseId);
                delStmt.executeUpdate();
            }

            try (PreparedStatement insStmt = conn.prepareStatement(insertQuery)) {
                insStmt.setInt(1, courseId);
                insStmt.setString(2, token);
                insStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean validateSession(int courseId, String token) {
        String query = "SELECT generated_at FROM active_sessions WHERE course_id = ? AND token = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, courseId);
            pstmt.setString(2, token);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Timestamp generatedAt = rs.getTimestamp("generated_at");
                long valTime = generatedAt.getTime();
                long curTime = System.currentTimeMillis();

                // Token valid for 15 seconds (giving some buffer over 10s refresh)
                if (curTime - valTime < 15000) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, Object> getStudentStats(int studentId) {
        Map<String, Object> stats = new HashMap<>();
        String query = "SELECT " +
                "COUNT(*) as total_classes, " +
                "SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) as present_classes, " +
                "SUM(CASE WHEN status = 'Absent' THEN 1 ELSE 0 END) as absent_classes " +
                "FROM attendance_records WHERE student_id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int total = rs.getInt("total_classes");
                int present = rs.getInt("present_classes");
                int absent = rs.getInt("absent_classes");

                stats.put("total", total);
                stats.put("present", present);
                stats.put("absent", absent);
                stats.put("percentage", total == 0 ? 0.0 : (double) present / total * 100);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public List<Map<String, Object>> getSubjectWiseStats(int studentId) {
        List<Map<String, Object>> subjectStats = new ArrayList<>();
        String query = "SELECT c.course_name, " +
                "COUNT(*) as total, " +
                "SUM(CASE WHEN ar.status = 'Present' THEN 1 ELSE 0 END) as present " +
                "FROM attendance_records ar " +
                "JOIN courses c ON ar.course_id = c.id " +
                "WHERE ar.student_id = ? " +
                "GROUP BY c.course_name";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("course", rs.getString("course_name"));
                map.put("total", rs.getInt("total"));
                map.put("present", rs.getInt("present"));
                int total = rs.getInt("total");
                int present = rs.getInt("present");
                map.put("percentage", total == 0 ? 0.0 : (double) present / total * 100);
                subjectStats.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjectStats;
    }

    public int validateToken(String token) {
        String query = "SELECT course_id, generated_at FROM active_sessions WHERE token = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, token);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Timestamp generatedAt = rs.getTimestamp("generated_at");
                long valTime = generatedAt.getTime();
                long curTime = System.currentTimeMillis();

                // Token valid for 15 seconds
                if (curTime - valTime < 15000) {
                    return rs.getInt("course_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
