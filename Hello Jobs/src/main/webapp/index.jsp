<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
        content="width=device-width, initial-scale=1.0">

    <title>Hello Jobs - Sarkari Job Portal</title>

    <link rel="stylesheet"
        href="css/style.css">

</head>

<body>

    <!-- =====================================================
         HEADER
    ====================================================== -->

    <header class="main-header">

        <div class="container header-content">

            <div class="logo">

                <a href="index.jsp">
                    Hello Jobs
                </a>

                <span>
                    Government Job Vacancy Portal
                </span>

            </div>

            <nav>

                <a href="index.jsp">
                    Home
                </a>

                <a href="admin/login.jsp">
                    Admin
                </a>

            </nav>

        </div>

    </header>


    <!-- =====================================================
         HERO SECTION
    ====================================================== -->

    <section class="hero">

        <div class="container">

            <h1>
                Find Government Jobs Easily
            </h1>

            <p>
                Search and explore the latest job vacancies,
                qualifications, important dates and official
                application links.
            </p>

            <div class="search-box">

                <input
                    type="text"
                    id="searchInput"
                    placeholder="Search jobs... Example: SSC, Railway, Java">

                <button
                    type="button"
                    onclick="searchJobs()">

                    Search

                </button>

            </div>

        </div>

    </section>


    <!-- =====================================================
         JOB SECTION
    ====================================================== -->

    <main class="container main-content">

        <div class="section-heading">

            <div>

                <h2>
                    Latest Job Vacancies
                </h2>

                <p>
                    Browse available government job opportunities.
                </p>

            </div>

        </div>


        <div id="loadingMessage"
            class="message-box">

            Loading jobs...

        </div>


        <div id="errorMessage"
            class="message-box error-message"
            style="display:none;">

        </div>


        <div id="jobList"
            class="job-grid">

        </div>

    </main>


    <!-- =====================================================
         FOOTER
    ====================================================== -->

    <footer class="footer">

        <div class="container">

            <p>
                &copy; Hello Jobs By Srikanta.
            </p>

        </div>

    </footer>


    <script src="js/app.js"></script>

</body>

</html>