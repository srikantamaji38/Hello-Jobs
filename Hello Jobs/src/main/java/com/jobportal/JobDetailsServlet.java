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

@WebServlet("/job-details")
public class JobDetailsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // ---------------------------------------------------------
    // doGet: get one job by ID
    // ---------------------------------------------------------
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");

        // Validate ID
        if (idParam == null || idParam.trim().isEmpty()) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            out.println("{\"error\":\"Missing job ID\"}");

            return;
        }

        int id;

        try {

            id = Integer.parseInt(idParam);

        } catch (NumberFormatException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            out.println("{\"error\":\"Invalid job ID\"}");

            return;
        }

        if (id <= 0) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            out.println("{\"error\":\"Job ID must be greater than 0\"}");

            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            String query = "SELECT id, job_title, organization, advertisement_number, "
                    + "total_vacancies, qualification, age_limit, salary, "
                    + "application_start_date, last_date, important_dates, "
                    + "job_description, notification_url, official_apply_url "
                    + "FROM jobs WHERE id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {

                        int jobId = rs.getInt("id");

                        String title = rs.getString("job_title");
                        String organization = rs.getString("organization");
                        String advertisementNumber =
                                rs.getString("advertisement_number");

                        int vacancies =
                                rs.getInt("total_vacancies");

                        String qualification =
                                rs.getString("qualification");

                        String ageLimit =
                                rs.getString("age_limit");

                        String salary =
                                rs.getString("salary");

                        String applicationStartDate =
                                rs.getString("application_start_date");

                        String lastDate =
                                rs.getString("last_date");

                        String importantDates =
                                rs.getString("important_dates");

                        String jobDescription =
                                rs.getString("job_description");

                        String notificationUrl =
                                rs.getString("notification_url");

                        String officialApplyUrl =
                                rs.getString("official_apply_url");

                        StringBuilder json = new StringBuilder();

                        json.append("{");

                        json.append("\"id\":")
                                .append(jobId)
                                .append(",");

                        json.append("\"title\":\"")
                                .append(escapeJson(title))
                                .append("\",");

                        json.append("\"organization\":\"")
                                .append(escapeJson(organization))
                                .append("\",");

                        json.append("\"advertisementNumber\":\"")
                                .append(escapeJson(advertisementNumber))
                                .append("\",");

                        json.append("\"vacancies\":")
                                .append(vacancies)
                                .append(",");

                        json.append("\"qualification\":\"")
                                .append(escapeJson(qualification))
                                .append("\",");

                        json.append("\"ageLimit\":\"")
                                .append(escapeJson(ageLimit))
                                .append("\",");

                        json.append("\"salary\":\"")
                                .append(escapeJson(salary))
                                .append("\",");

                        json.append("\"applicationStartDate\":\"")
                                .append(escapeJson(applicationStartDate))
                                .append("\",");

                        json.append("\"lastDate\":\"")
                                .append(escapeJson(lastDate))
                                .append("\",");

                        json.append("\"importantDates\":\"")
                                .append(escapeJson(importantDates))
                                .append("\",");

                        json.append("\"jobDescription\":\"")
                                .append(escapeJson(jobDescription))
                                .append("\",");

                        json.append("\"notificationUrl\":\"")
                                .append(escapeJson(notificationUrl))
                                .append("\",");

                        json.append("\"officialApplyUrl\":\"")
                                .append(escapeJson(officialApplyUrl))
                                .append("\"");

                        json.append("}");

                        out.println(json.toString());

                    } else {

                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);

                        out.println("{\"error\":\"Job not found\"}");
                    }
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