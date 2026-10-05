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

@WebServlet("/admin-jobs")
public class AdminJobServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // =========================================================
    // GET
    // Load all jobs for admin dashboard
    // Backend -> Frontend : JSON
    // =========================================================

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        if (!isAdminLoggedIn(request)) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            out.print("{\"error\":\"Admin login required\"}");

            return;
        }

        String query =
                "SELECT id, job_title, organization, "
                + "total_vacancies, qualification, last_date, "
                + "job_description "
                + "FROM jobs ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            StringBuilder json = new StringBuilder();

            json.append("[");

            boolean first = true;

            while (rs.next()) {

                if (!first) {
                    json.append(",");
                }

                first = false;

                json.append("{");

                json.append("\"id\":")
                        .append(rs.getInt("id"))
                        .append(",");

                json.append("\"title\":\"")
                        .append(escapeJson(rs.getString("job_title")))
                        .append("\",");

                json.append("\"organization\":\"")
                        .append(escapeJson(rs.getString("organization")))
                        .append("\",");

                json.append("\"vacancies\":")
                        .append(rs.getInt("total_vacancies"))
                        .append(",");

                json.append("\"qualification\":\"")
                        .append(escapeJson(rs.getString("qualification")))
                        .append("\",");

                json.append("\"lastDate\":\"")
                        .append(escapeJson(rs.getString("last_date")))
                        .append("\",");

                json.append("\"jobDescription\":\"")
                        .append(escapeJson(rs.getString("job_description")))
                        .append("\"");

                json.append("}");
            }

            json.append("]");

            out.print(json.toString());

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            out.print("{\"error\":\"Database error\"}");
        }
    }


    // =========================================================
    // POST
    //
    // Frontend -> Backend:
    //
    // action=add
    // action=update
    // action=delete
    //
    // Content-Type:
    // application/x-www-form-urlencoded
    //
    // Backend -> Frontend:
    // JSON
    // =========================================================

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        // -----------------------------------------------------
        // Admin authentication
        // -----------------------------------------------------

        if (!isAdminLoggedIn(request)) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            out.print(
                    "{\"error\":\"Admin login required\"}");

            return;
        }

        // -----------------------------------------------------
        // Get action
        // -----------------------------------------------------

        String action = request.getParameter("action");

        System.out.println(
                "AdminJobServlet action = " + action);

        if (action == null || action.trim().isEmpty()) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            out.print(
                    "{\"error\":\"Missing action\"}");

            return;
        }

        action = action.trim();

        // -----------------------------------------------------
        // CRUD action
        // -----------------------------------------------------

        switch (action) {

            case "add":

                addJob(request, response, out);

                return;

            case "update":

                updateJob(request, response, out);

                return;

            case "delete":

                deleteJob(request, response, out);

                return;

            default:

                response.setStatus(
                        HttpServletResponse.SC_BAD_REQUEST);

                out.print(
                        "{\"error\":\"Invalid action: "
                        + escapeJson(action)
                        + "\"}");

                return;
        }
    }


    // =========================================================
    // ADD JOB
    // =========================================================

    private void addJob(HttpServletRequest request,
            HttpServletResponse response,
            PrintWriter out) {

        String jobTitle =
                request.getParameter("jobTitle");

        String organization =
                request.getParameter("organization");

        String advertisementNumber =
                request.getParameter("advertisementNumber");

        String vacanciesParam =
                request.getParameter("totalVacancies");

        String qualification =
                request.getParameter("qualification");

        String ageLimit =
                request.getParameter("ageLimit");

        String salary =
                request.getParameter("salary");

        String applicationStartDate =
                request.getParameter("applicationStartDate");

        String lastDate =
                request.getParameter("lastDate");

        String importantDates =
                request.getParameter("importantDates");

        String jobDescription =
                request.getParameter("jobDescription");

        String notificationUrl =
                request.getParameter("notificationUrl");

        String officialApplyUrl =
                request.getParameter("officialApplyUrl");


        // -----------------------------------------------------
        // Validation
        // -----------------------------------------------------

        if (isBlank(jobTitle)
                || isBlank(organization)
                || isBlank(advertisementNumber)
                || isBlank(vacanciesParam)
                || isBlank(qualification)
                || isBlank(ageLimit)
                || isBlank(salary)
                || isBlank(applicationStartDate)
                || isBlank(lastDate)
                || isBlank(importantDates)
                || isBlank(jobDescription)
                || isBlank(notificationUrl)
                || isBlank(officialApplyUrl)) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            out.print(
                    "{\"error\":\"All job fields are required\"}");

            return;
        }


        int totalVacancies;

        try {

            totalVacancies =
                    Integer.parseInt(vacanciesParam.trim());

        } catch (NumberFormatException e) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            out.print(
                    "{\"error\":\"Total vacancies must be a valid number\"}");

            return;
        }


        if (totalVacancies <= 0) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            out.print(
                    "{\"error\":\"Total vacancies must be greater than 0\"}");

            return;
        }


        if (!isValidUrl(notificationUrl)
                || !isValidUrl(officialApplyUrl)) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            out.print(
                    "{\"error\":\"URL must start with http:// or https://\"}");

            return;
        }


        // -----------------------------------------------------
        // INSERT
        // -----------------------------------------------------

        String query =
                "INSERT INTO jobs "
                + "(job_title, organization, advertisement_number, "
                + "total_vacancies, qualification, age_limit, salary, "
                + "application_start_date, last_date, important_dates, "
                + "job_description, notification_url, official_apply_url) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


        try (Connection conn =
                DBConnection.getConnection();
                PreparedStatement stmt =
                        conn.prepareStatement(query)) {

            stmt.setString(1, jobTitle.trim());
            stmt.setString(2, organization.trim());
            stmt.setString(3, advertisementNumber.trim());
            stmt.setInt(4, totalVacancies);
            stmt.setString(5, qualification.trim());
            stmt.setString(6, ageLimit.trim());
            stmt.setString(7, salary.trim());
            stmt.setString(8, applicationStartDate.trim());
            stmt.setString(9, lastDate.trim());
            stmt.setString(10, importantDates.trim());
            stmt.setString(11, jobDescription.trim());
            stmt.setString(12, notificationUrl.trim());
            stmt.setString(13, officialApplyUrl.trim());


            int rows =
                    stmt.executeUpdate();


            if (rows > 0) {

                out.print(
                        "{\"success\":true,"
                        + "\"message\":\"Job added successfully\"}");

            } else {

                response.setStatus(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                out.print(
                        "{\"error\":\"Job could not be added\"}");
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            out.print(
                    "{\"error\":\"Database error\"}");
        }
    }


    // =========================================================
    // UPDATE JOB
    // =========================================================

    private void updateJob(HttpServletRequest request,
            HttpServletResponse response,
            PrintWriter out) {

        // -----------------------------------------------------
        // Get ID
        // -----------------------------------------------------

        String idParam =
                request.getParameter("id");

        System.out.println(
                "Update ID = " + idParam);


        if (isBlank(idParam)) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            out.print(
                    "{\"error\":\"Job ID is required for update\"}");

            return;
        }


        int id;

        try {

            id = Integer.parseInt(idParam.trim());

        } catch (NumberFormatException e) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            out.print(
                    "{\"error\":\"Invalid job ID\"}");

            return;
        }


        // -----------------------------------------------------
        // Get all fields
        // -----------------------------------------------------

        String jobTitle =
                request.getParameter("jobTitle");

        String organization =
                request.getParameter("organization");

        String advertisementNumber =
                request.getParameter("advertisementNumber");

        String vacanciesParam =
                request.getParameter("totalVacancies");

        String qualification =
                request.getParameter("qualification");

        String ageLimit =
                request.getParameter("ageLimit");

        String salary =
                request.getParameter("salary");

        String applicationStartDate =
                request.getParameter("applicationStartDate");

        String lastDate =
                request.getParameter("lastDate");

        String importantDates =
                request.getParameter("importantDates");

        String jobDescription =
                request.getParameter("jobDescription");

        String notificationUrl =
                request.getParameter("notificationUrl");

        String officialApplyUrl =
                request.getParameter("officialApplyUrl");


        // -----------------------------------------------------
        // Validation
        // -----------------------------------------------------

        if (isBlank(jobTitle)
                || isBlank(organization)
                || isBlank(advertisementNumber)
                || isBlank(vacanciesParam)
                || isBlank(qualification)
                || isBlank(ageLimit)
                || isBlank(salary)
                || isBlank(applicationStartDate)
                || isBlank(lastDate)
                || isBlank(importantDates)
                || isBlank(jobDescription)
                || isBlank(notificationUrl)
                || isBlank(officialApplyUrl)) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            out.print(
                    "{\"error\":\"All job fields are required\"}");

            return;
        }


        int totalVacancies;

        try {

            totalVacancies =
                    Integer.parseInt(vacanciesParam.trim());

        } catch (NumberFormatException e) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            out.print(
                    "{\"error\":\"Total vacancies must be a valid number\"}");

            return;
        }


        if (totalVacancies <= 0) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            out.print(
                    "{\"error\":\"Total vacancies must be greater than 0\"}");

            return;
        }


        if (!isValidUrl(notificationUrl)
                || !isValidUrl(officialApplyUrl)) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            out.print(
                    "{\"error\":\"URL must start with http:// or https://\"}");

            return;
        }


        // -----------------------------------------------------
        // UPDATE SQL
        // -----------------------------------------------------

        String query =
                "UPDATE jobs SET "
                + "job_title = ?, "
                + "organization = ?, "
                + "advertisement_number = ?, "
                + "total_vacancies = ?, "
                + "qualification = ?, "
                + "age_limit = ?, "
                + "salary = ?, "
                + "application_start_date = ?, "
                + "last_date = ?, "
                + "important_dates = ?, "
                + "job_description = ?, "
                + "notification_url = ?, "
                + "official_apply_url = ? "
                + "WHERE id = ?";


        try (Connection conn =
                DBConnection.getConnection();
                PreparedStatement stmt =
                        conn.prepareStatement(query)) {

            stmt.setString(1, jobTitle.trim());
            stmt.setString(2, organization.trim());
            stmt.setString(3, advertisementNumber.trim());
            stmt.setInt(4, totalVacancies);
            stmt.setString(5, qualification.trim());
            stmt.setString(6, ageLimit.trim());
            stmt.setString(7, salary.trim());
            stmt.setString(8, applicationStartDate.trim());
            stmt.setString(9, lastDate.trim());
            stmt.setString(10, importantDates.trim());
            stmt.setString(11, jobDescription.trim());
            stmt.setString(12, notificationUrl.trim());
            stmt.setString(13, officialApplyUrl.trim());

            // IMPORTANT:
            // ID is the 14th parameter
            stmt.setInt(14, id);


            int rows =
                    stmt.executeUpdate();


            System.out.println(
                    "Update affected rows = " + rows);


            if (rows > 0) {

                out.print(
                        "{\"success\":true,"
                        + "\"message\":\"Job updated successfully\"}");

            } else {

                response.setStatus(
                        HttpServletResponse.SC_NOT_FOUND);

                out.print(
                        "{\"error\":\"Job not found or nothing was updated\"}");
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            out.print(
                    "{\"error\":\"Database error\"}");
        }
    }


    // =========================================================
    // DELETE JOB
    // =========================================================

    private void deleteJob(HttpServletRequest request,
            HttpServletResponse response,
            PrintWriter out) {

        // -----------------------------------------------------
        // Get ID
        // -----------------------------------------------------

        String idParam =
                request.getParameter("id");


        System.out.println(
                "Delete ID = " + idParam);


        if (isBlank(idParam)) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            out.print(
                    "{\"error\":\"Job ID is required for delete\"}");

            return;
        }


        int id;

        try {

            id =
                    Integer.parseInt(idParam.trim());

        } catch (NumberFormatException e) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            out.print(
                    "{\"error\":\"Invalid job ID\"}");

            return;
        }


        // -----------------------------------------------------
        // DELETE SQL
        // -----------------------------------------------------

        String query =
                "DELETE FROM jobs WHERE id = ?";


        try (Connection conn =
                DBConnection.getConnection();
                PreparedStatement stmt =
                        conn.prepareStatement(query)) {

            stmt.setInt(1, id);


            int rows =
                    stmt.executeUpdate();


            System.out.println(
                    "Delete affected rows = " + rows);


            if (rows > 0) {

                out.print(
                        "{\"success\":true,"
                        + "\"message\":\"Job deleted successfully\"}");

            } else {

                response.setStatus(
                        HttpServletResponse.SC_NOT_FOUND);

                out.print(
                        "{\"error\":\"Job not found\"}");
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            out.print(
                    "{\"error\":\"Database error\"}");
        }
    }


    // =========================================================
    // CHECK ADMIN LOGIN
    // =========================================================

    private boolean isAdminLoggedIn(
            HttpServletRequest request) {

        HttpSession session =
                request.getSession(false);

        return session != null
                && session.getAttribute("adminId") != null;
    }


    // =========================================================
    // CHECK BLANK
    // =========================================================

    private boolean isBlank(String value) {

        return value == null
                || value.trim().isEmpty();
    }


    // =========================================================
    // URL VALIDATION
    // =========================================================

    private boolean isValidUrl(String url) {

        if (url == null) {
            return false;
        }

        url = url.trim();

        return url.startsWith("http://")
                || url.startsWith("https://");
    }


    // =========================================================
    // JSON ESCAPE
    // =========================================================

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
