package com.jobportal;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/admin-login")
public class AdminLoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // ---------------------------------------------------------
    // doPost: validate admin login
    // ---------------------------------------------------------
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Validate username
        if (username == null || username.trim().isEmpty()) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            out.println("{\"error\":\"Username is required\"}");

            return;
        }

        // Validate password
        if (password == null || password.trim().isEmpty()) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            out.println("{\"error\":\"Password is required\"}");

            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            String query = "SELECT id, username FROM admins "
                    + "WHERE username = ? AND password = ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, username.trim());
                stmt.setString(2, password);

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {

                        int adminId = rs.getInt("id");

                        String adminUsername =
                                rs.getString("username");

                        HttpSession session = request.getSession();

                        session.setAttribute("adminId", adminId);
                        session.setAttribute("adminUsername",
                                adminUsername);

                        out.println(
                                "{\"success\":true,\"message\":\"Login successful\"}");

                    } else {

                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                        out.println(
                                "{\"error\":\"Invalid username or password\"}");
                    }
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            out.println("{\"error\":\"Database error\"}");
        }
    }
}