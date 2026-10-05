<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
        content="width=device-width, initial-scale=1.0">

    <title>Admin Login - Hello Jobs</title>

    <link rel="stylesheet"
        href="../css/style.css">

</head>

<body class="login-page">

    <div class="login-container">

        <div class="login-card">

            <div class="login-logo">

                <h1>
                    Hello Jobs
                </h1>

                <p>
                    Admin Panel
                </p>

            </div>


            <form id="loginForm">

                <div class="form-group">

                    <label for="username">
                        Username
                    </label>

                    <input
                        type="text"
                        id="username"
                        name="username"
                        placeholder="Enter username"
                        required>

                </div>


                <div class="form-group">

                    <label for="password">
                        Password
                    </label>

                    <input
                        type="password"
                        id="password"
                        name="password"
                        placeholder="Enter password"
                        required>

                </div>


                <button
                    type="submit"
                    class="btn btn-primary full-width">

                    Login

                </button>

            </form>


            <div id="loginMessage"
                class="form-message">

            </div>


            <a href="../index.jsp"
                class="back-link">

                ← Back to Website

            </a>

        </div>

    </div>


    <script src="../js/app.js"></script>

</body>

</html>