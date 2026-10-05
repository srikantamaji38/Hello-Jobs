<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
    if (session.getAttribute("adminId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
        content="width=device-width, initial-scale=1.0">

    <title>Admin Dashboard - Hello Jobs</title>

    <link rel="stylesheet"
        href="../css/style.css">

</head>

<body>

    <!-- =====================================================
         ADMIN HEADER
    ====================================================== -->

    <header class="admin-header">

        <div class="container admin-header-content">

            <div>

                <h2>
                    Hello Jobs
                </h2>

                <span>
                    Admin Dashboard
                </span>

            </div>

            <div class="admin-nav">

                <a href="../index.jsp"
                    target="_blank">

                    View Website

                </a>

                <button
                    onclick="logoutAdmin()"
                    class="btn btn-danger">

                    Logout

                </button>

            </div>

        </div>

    </header>


    <!-- =====================================================
         DASHBOARD
    ====================================================== -->

    <main class="container dashboard">

        <div class="dashboard-title">

            <h1>
                Job Management
            </h1>

            <p>
                Add, edit and delete government job vacancies.
            </p>

        </div>


        <!-- =================================================
             ADD / EDIT FORM
        ================================================== -->

        <section class="admin-card">

            <div class="card-header">

                <h2 id="formTitle">
                    Add New Job
                </h2>

                <button
                    type="button"
                    onclick="clearJobForm()"
                    class="btn btn-secondary">

                    Clear Form

                </button>

            </div>


            <form id="jobForm">

                <input
                    type="hidden"
                    id="jobId"
                    name="id">


                <div class="form-grid">

                    <div class="form-group">

                        <label>
                            Job Title *
                        </label>

                        <input
                            type="text"
                            id="jobTitle"
                            name="jobTitle"
                            required>

                    </div>


                    <div class="form-group">

                        <label>
                            Organization *
                        </label>

                        <input
                            type="text"
                            id="organization"
                            name="organization"
                            required>

                    </div>


                    <div class="form-group">

                        <label>
                            Advertisement Number *
                        </label>

                        <input
                            type="text"
                            id="advertisementNumber"
                            name="advertisementNumber"
                            required>

                    </div>


                    <div class="form-group">

                        <label>
                            Total Vacancies *
                        </label>

                        <input
                            type="number"
                            id="totalVacancies"
                            name="totalVacancies"
                            min="1"
                            required>

                    </div>


                    <div class="form-group">

                        <label>
                            Qualification *
                        </label>

                        <input
                            type="text"
                            id="qualification"
                            name="qualification"
                            required>

                    </div>


                    <div class="form-group">

                        <label>
                            Age Limit *
                        </label>

                        <input
                            type="text"
                            id="ageLimit"
                            name="ageLimit"
                            placeholder="18-32 years"
                            required>

                    </div>


                    <div class="form-group">

                        <label>
                            Salary *
                        </label>

                        <input
                            type="text"
                            id="salary"
                            name="salary"
                            required>

                    </div>


                    <div class="form-group">

                        <label>
                            Application Start Date *
                        </label>

                        <input
                            type="date"
                            id="applicationStartDate"
                            name="applicationStartDate"
                            required>

                    </div>


                    <div class="form-group">

                        <label>
                            Last Date *
                        </label>

                        <input
                            type="date"
                            id="lastDate"
                            name="lastDate"
                            required>

                    </div>


                    <div class="form-group full-column">

                        <label>
                            Important Dates *
                        </label>

                        <input
                            type="text"
                            id="importantDates"
                            name="importantDates"
                            placeholder="Application starts: 01-10-2026, Last date: 30-10-2026"
                            required>

                    </div>


                    <div class="form-group full-column">

                        <label>
                            Job Description *
                        </label>

                        <textarea
                            id="jobDescription"
                            name="jobDescription"
                            rows="7"
                            placeholder="Enter complete job description..."
                            required></textarea>

                    </div>


                    <div class="form-group">

                        <label>
                            Notification URL *
                        </label>

                        <input
                            type="url"
                            id="notificationUrl"
                            name="notificationUrl"
                            placeholder="https://example.com/notification"
                            required>

                    </div>


                    <div class="form-group">

                        <label>
                            Official Apply URL *
                        </label>

                        <input
                            type="url"
                            id="officialApplyUrl"
                            name="officialApplyUrl"
                            placeholder="https://example.com/apply"
                            required>

                    </div>

                </div>


                <div class="form-actions">

        <button
            type="submit"
            id="saveJobButton"
            class="btn btn-primary">
            Add Job
        </button>

        <button
            type="button"
            onclick="clearJobForm()"
            class="btn btn-secondary">
            Cancel
        </button>

    </div>
                

            </form>


            <div id="adminMessage"
                class="form-message">

            </div>

        </section>


        <!-- =================================================
             JOB LIST
        ================================================== -->

        <section class="admin-card">

            <div class="card-header">

                <div>

                    <h2>
                        Existing Jobs
                    </h2>

                    <p>
                        Manage all job vacancies.
                    </p>

                </div>

                <button
                    onclick="loadAdminJobs()"
                    class="btn btn-secondary">

                    Refresh

                </button>

            </div>


            <div class="table-container">

                <table>

                    <thead>

                        <tr>

                            <th>ID</th>
                            <th>Job</th>
                            <th>Organization</th>
                            <th>Vacancies</th>
                            <th>Last Date</th>
                            <th>Actions</th>

                        </tr>

                    </thead>

                    <tbody id="adminJobTable">

                    </tbody>

                </table>

            </div>

        </section>

    </main>


    <script src="../js/app.js"></script>

</body>

</html>