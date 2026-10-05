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

@WebServlet("/jobs")
public class JobServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // ---------------------------------------------------------
    // doGet: get all jobs or search jobs
    // ---------------------------------------------------------
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        String search = request.getParameter("search");

        try (Connection conn = DBConnection.getConnection()) {

            String query;

            if (search == null || search.trim().isEmpty()) {

                query = "SELECT id, job_title, organization, total_vacancies, "
                        + "qualification, last_date, job_description "
                        + "FROM jobs ORDER BY id DESC";

            } else {

                query = "SELECT id, job_title, organization, total_vacancies, "
                        + "qualification, last_date, job_description "
                        + "FROM jobs "
                        + "WHERE job_title LIKE ? "
                        + "OR organization LIKE ? "
                        + "OR qualification LIKE ? "
                        + "OR job_description LIKE ? "
                        + "ORDER BY id DESC";
            }

            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                if (search != null && !search.trim().isEmpty()) {

                    String keyword = "%" + search.trim() + "%";

                    stmt.setString(1, keyword);
                    stmt.setString(2, keyword);
                    stmt.setString(3, keyword);
                    stmt.setString(4, keyword);
                }

                try (ResultSet rs = stmt.executeQuery()) {

                    StringBuilder json = new StringBuilder();

                    json.append("[");

                    boolean first = true;

                    while (rs.next()) {

                        if (!first) {
                            json.append(",");
                        }

                        first = false;

                        int id = rs.getInt("id");
                        String title = rs.getString("job_title");
                        String organization = rs.getString("organization");
                        int vacancies = rs.getInt("total_vacancies");
                        String qualification = rs.getString("qualification");
                        String lastDate = rs.getString("last_date");
                        String jobDescription = rs.getString("job_description");

                        json.append("{");

                        json.append("\"id\":").append(id).append(",");
                        json.append("\"title\":\"")
                                .append(escapeJson(title))
                                .append("\",");

                        json.append("\"organization\":\"")
                                .append(escapeJson(organization))
                                .append("\",");

                        json.append("\"vacancies\":")
                                .append(vacancies)
                                .append(",");

                        json.append("\"qualification\":\"")
                                .append(escapeJson(qualification))
                                .append("\",");

                        json.append("\"lastDate\":\"")
                                .append(escapeJson(lastDate))
                                .append("\",");

                        json.append("\"jobDescription\":\"")
                                .append(escapeJson(jobDescription))
                                .append("\"");

                        json.append("}");
                    }

                    json.append("]");

                    out.println(json.toString());
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            out.println("{\"error\":\"Database error\"}");
        }
    }

    // ---------------------------------------------------------
    // JSON escape method
    // ---------------------------------------------------------
    private String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}