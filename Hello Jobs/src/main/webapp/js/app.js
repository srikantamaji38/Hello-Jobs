// ============================================================
// HELLO JOBS - MAIN JAVASCRIPT
// ============================================================


// ============================================================
// PAGE LOAD
// ============================================================

document.addEventListener("DOMContentLoaded", function () {

    // Homepage
    if (document.getElementById("jobList")) {

        loadJobs();

        var searchInput =
            document.getElementById("searchInput");

        if (searchInput) {

            searchInput.addEventListener("keypress", function (event) {

                if (event.key === "Enter") {
                    searchJobs();
                }
            });
        }
    }


    // Job Details page
    if (document.getElementById("jobDetails")) {

        loadJobDetails();
    }


    // Admin Login
    if (document.getElementById("loginForm")) {

        document
            .getElementById("loginForm")
            .addEventListener("submit", function (event) {

                event.preventDefault();

                loginAdmin();
            });
    }


    // Admin Dashboard
    if (document.getElementById("adminJobTable")) {

        loadAdminJobs();

        var jobForm =
            document.getElementById("jobForm");

        if (jobForm) {

            jobForm.addEventListener("submit", function (event) {

                event.preventDefault();

                saveJob();
            });
        }
    }

});


// ============================================================
// LOAD ALL PUBLIC JOBS
// Backend -> Frontend JSON
// ============================================================

function loadJobs() {

    var xhr = new XMLHttpRequest();

    xhr.open("GET", "jobs", true);

    xhr.onreadystatechange = function () {

        if (xhr.readyState !== 4) {
            return;
        }

        var loading =
            document.getElementById("loadingMessage");

        if (loading) {
            loading.style.display = "none";
        }

        if (xhr.status === 200) {

            try {

                var jobs =
                    JSON.parse(xhr.responseText);

                displayJobs(jobs);

            } catch (error) {

                showError(
                    "Invalid JSON response from server."
                );
            }

        } else {

            showError(
                "Unable to load jobs."
            );
        }
    };

    xhr.send();
}


// ============================================================
// SEARCH JOBS
// ============================================================

function searchJobs() {

    var searchInput =
        document.getElementById("searchInput");

    if (!searchInput) {
        return;
    }

    var search =
        searchInput.value.trim();

    var loading =
        document.getElementById("loadingMessage");

    if (loading) {

        loading.style.display = "block";

        loading.innerText =
            "Searching jobs...";
    }

    var xhr =
        new XMLHttpRequest();

    xhr.open(
        "GET",
        "jobs?search=" + encodeURIComponent(search),
        true
    );

    xhr.onreadystatechange = function () {

        if (xhr.readyState !== 4) {
            return;
        }

        if (loading) {
            loading.style.display = "none";
        }

        if (xhr.status === 200) {

            try {

                var jobs =
                    JSON.parse(xhr.responseText);

                displayJobs(jobs);

            } catch (error) {

                showError(
                    "Invalid JSON response from server."
                );
            }

        } else {

            showError(
                "Search failed."
            );
        }
    };

    xhr.send();
}


// ============================================================
// DISPLAY JOB CARDS
// ============================================================

function displayJobs(jobs) {

    var jobList =
        document.getElementById("jobList");

    var errorMessage =
        document.getElementById("errorMessage");

    if (errorMessage) {
        errorMessage.style.display = "none";
    }

    if (!jobList) {
        return;
    }

    jobList.innerHTML = "";


    if (!jobs || jobs.length === 0) {

        jobList.innerHTML =
            '<div class="empty-state">'
            + '<h3>No jobs found</h3>'
            + '<p>Try another search keyword.</p>'
            + '</div>';

        return;
    }


    for (var i = 0; i < jobs.length; i++) {

        var job = jobs[i];

        var card =
            document.createElement("div");

        card.className =
            "job-card";


        var title =
            document.createElement("h3");

        title.innerText =
            job.title;


        var organization =
            document.createElement("p");

        organization.className =
            "job-organization";

        organization.innerText =
            job.organization;


        var details =
            document.createElement("div");

        details.className =
            "job-summary";


        var vacancies =
            document.createElement("p");

        vacancies.innerHTML =
            "<strong>Vacancies:</strong> "
            + escapeHtml(job.vacancies);


        var qualification =
            document.createElement("p");

        qualification.innerHTML =
            "<strong>Qualification:</strong> "
            + escapeHtml(job.qualification);


        var lastDate =
            document.createElement("p");

        lastDate.innerHTML =
            "<strong>Last Date:</strong> "
            + escapeHtml(job.lastDate);


        var description =
            document.createElement("p");

        description.className =
            "job-short-description";

        description.innerText =
            job.jobDescription;


        var button =
            document.createElement("a");

        button.className =
            "btn btn-primary";

        button.href =
            "job.jsp?id="
            + encodeURIComponent(job.id);

        button.innerText =
            "View Details";


        details.appendChild(vacancies);
        details.appendChild(qualification);
        details.appendChild(lastDate);

        card.appendChild(title);
        card.appendChild(organization);
        card.appendChild(details);
        card.appendChild(description);
        card.appendChild(button);

        jobList.appendChild(card);
    }
}


// ============================================================
// LOAD JOB DETAILS
// ============================================================

function loadJobDetails() {

    var params =
        new URLSearchParams(
            window.location.search
        );

    var id =
        params.get("id");

    var container =
        document.getElementById("jobDetails");


    if (!container) {
        return;
    }


    if (!id) {

        container.innerHTML =
            '<div class="message-box error-message">'
            + 'Job ID is missing.'
            + '</div>';

        return;
    }


    var xhr =
        new XMLHttpRequest();

    xhr.open(
        "GET",
        "job-details?id="
        + encodeURIComponent(id),
        true
    );


    xhr.onreadystatechange = function () {

        if (xhr.readyState !== 4) {
            return;
        }

        if (xhr.status === 200) {

            try {

                var job =
                    JSON.parse(xhr.responseText);

                displayJobDetails(job);

            } catch (error) {

                container.innerHTML =
                    '<div class="message-box error-message">'
                    + 'Invalid server response.'
                    + '</div>';
            }

        } else {

            container.innerHTML =
                '<div class="message-box error-message">'
                + 'Job not found.'
                + '</div>';
        }
    };


    xhr.send();
}


// ============================================================
// DISPLAY COMPLETE JOB DETAILS
// ============================================================

function displayJobDetails(job) {

    var container =
        document.getElementById("jobDetails");

    if (!container) {
        return;
    }

    container.innerHTML = "";


    var card =
        document.createElement("div");

    card.className =
        "details-card";


    var heading =
        document.createElement("div");

    heading.className =
        "details-heading";


    var title =
        document.createElement("h1");

    title.innerText =
        job.title;


    var organization =
        document.createElement("p");

    organization.className =
        "details-organization";

    organization.innerText =
        job.organization;


    heading.appendChild(title);
    heading.appendChild(organization);

    card.appendChild(heading);


    var infoGrid =
        document.createElement("div");

    infoGrid.className =
        "details-grid";


    addDetail(
        infoGrid,
        "Advertisement Number",
        job.advertisementNumber
    );

    addDetail(
        infoGrid,
        "Total Vacancies",
        job.vacancies
    );

    addDetail(
        infoGrid,
        "Qualification",
        job.qualification
    );

    addDetail(
        infoGrid,
        "Age Limit",
        job.ageLimit
    );

    addDetail(
        infoGrid,
        "Salary",
        job.salary
    );

    addDetail(
        infoGrid,
        "Application Start Date",
        job.applicationStartDate
    );

    addDetail(
        infoGrid,
        "Last Date",
        job.lastDate
    );

    addDetail(
        infoGrid,
        "Important Dates",
        job.importantDates
    );


    card.appendChild(infoGrid);


    // Job Description

    var descriptionSection =
        document.createElement("section");

    descriptionSection.className =
        "description-section";


    var descriptionTitle =
        document.createElement("h2");

    descriptionTitle.innerText =
        "Job Description";


    var description =
        document.createElement("p");

    description.innerText =
        job.jobDescription;


    descriptionSection.appendChild(
        descriptionTitle
    );

    descriptionSection.appendChild(
        description
    );

    card.appendChild(
        descriptionSection
    );


    // Links

    var linksSection =
        document.createElement("section");

    linksSection.className =
        "job-links";


    var notificationLink =
        document.createElement("a");

    notificationLink.href =
        job.notificationUrl;

    notificationLink.target =
        "_blank";

    notificationLink.rel =
        "noopener noreferrer";

    notificationLink.className =
        "btn btn-secondary";

    notificationLink.innerText =
        "View Notification";


    var applyLink =
        document.createElement("a");

    applyLink.href =
        job.officialApplyUrl;

    applyLink.target =
        "_blank";

    applyLink.rel =
        "noopener noreferrer";

    applyLink.className =
        "btn btn-primary";

    applyLink.innerText =
        "Apply Online";


    linksSection.appendChild(
        notificationLink
    );

    linksSection.appendChild(
        applyLink
    );

    card.appendChild(
        linksSection
    );

    container.appendChild(card);
}


// ============================================================
// ADD DETAIL
// ============================================================

function addDetail(container, label, value) {

    var item =
        document.createElement("div");

    item.className =
        "detail-item";


    var labelElement =
        document.createElement("span");

    labelElement.className =
        "detail-label";

    labelElement.innerText =
        label;


    var valueElement =
        document.createElement("span");

    valueElement.className =
        "detail-value";

    valueElement.innerText =
        value == null ? "" : value;


    item.appendChild(labelElement);
    item.appendChild(valueElement);

    container.appendChild(item);
}


// ============================================================
// ADMIN LOGIN
// ============================================================

function loginAdmin() {

    var username =
        document.getElementById("username").value.trim();

    var password =
        document.getElementById("password").value;


    var message =
        document.getElementById("loginMessage");


    if (username === "") {

        message.innerText =
            "Username is required.";

        message.className =
            "form-message error-message";

        return;
    }


    if (password === "") {

        message.innerText =
            "Password is required.";

        message.className =
            "form-message error-message";

        return;
    }


    var xhr =
        new XMLHttpRequest();

    xhr.open(
        "POST",
        "../admin-login",
        true
    );


    xhr.setRequestHeader(
        "Content-Type",
        "application/x-www-form-urlencoded;charset=UTF-8"
    );


    xhr.onreadystatechange = function () {

        if (xhr.readyState !== 4) {
            return;
        }

        try {

            var result =
                JSON.parse(xhr.responseText);


            if (xhr.status === 200
                && result.success) {

                message.innerText =
                    "Login successful. Redirecting...";

                message.className =
                    "form-message success-message";


                setTimeout(function () {

                    window.location.href =
                        "dashboard.jsp";

                }, 500);


            } else {

                message.innerText =
                    result.error
                    || "Login failed.";

                message.className =
                    "form-message error-message";
            }


        } catch (error) {

            message.innerText =
                "Invalid server response.";

            message.className =
                "form-message error-message";
        }
    };


    var data =
        "username="
        + encodeURIComponent(username)
        + "&password="
        + encodeURIComponent(password);


    xhr.send(data);
}


// ============================================================
// LOAD ADMIN JOBS
// Backend -> Frontend JSON
// ============================================================

function loadAdminJobs() {

    var table =
        document.getElementById("adminJobTable");

    if (!table) {
        return;
    }


    table.innerHTML =
        '<tr><td colspan="6">Loading...</td></tr>';


    var xhr =
        new XMLHttpRequest();

    xhr.open(
        "GET",
        "../admin-jobs",
        true
    );


    xhr.onreadystatechange = function () {

        if (xhr.readyState !== 4) {
            return;
        }


        if (xhr.status === 200) {

            try {

                var jobs =
                    JSON.parse(xhr.responseText);

                displayAdminJobs(jobs);

            } catch (error) {

                table.innerHTML =
                    '<tr><td colspan="6">'
                    + 'Invalid server response.'
                    + '</td></tr>';
            }

        } else if (xhr.status === 401) {

            window.location.href =
                "login.jsp";

        } else {

            table.innerHTML =
                '<tr><td colspan="6">'
                + 'Unable to load jobs.'
                + '</td></tr>';
        }
    };


    xhr.send();
}


// ============================================================
// DISPLAY ADMIN JOB TABLE
// ============================================================

function displayAdminJobs(jobs) {

    var table =
        document.getElementById("adminJobTable");

    if (!table) {
        return;
    }


    table.innerHTML = "";


    if (!jobs || jobs.length === 0) {

        table.innerHTML =
            '<tr><td colspan="6">'
            + 'No jobs available.'
            + '</td></tr>';

        return;
    }


    for (var i = 0; i < jobs.length; i++) {

        var job =
            jobs[i];


        var row =
            document.createElement("tr");


        var idCell =
            document.createElement("td");

        idCell.innerText =
            job.id;


        var titleCell =
            document.createElement("td");

        titleCell.innerText =
            job.title;


        var organizationCell =
            document.createElement("td");

        organizationCell.innerText =
            job.organization;


        var vacancyCell =
            document.createElement("td");

        vacancyCell.innerText =
            job.vacancies;


        var dateCell =
            document.createElement("td");

        dateCell.innerText =
            job.lastDate;


        var actionCell =
            document.createElement("td");


        var editButton =
            document.createElement("button");

        editButton.type =
            "button";

        editButton.className =
            "btn btn-small btn-primary";

        editButton.innerText =
            "Edit";


        editButton.onclick =
            function (selectedJob) {

                return function () {

                    editJob(selectedJob);
                };

            }(job);


        var deleteButton =
            document.createElement("button");

        deleteButton.type =
            "button";

        deleteButton.className =
            "btn btn-small btn-danger";

        deleteButton.innerText =
            "Delete";


        deleteButton.onclick =
            function (selectedJobId) {

                return function () {

                    deleteJob(selectedJobId);
                };

            }(job.id);


        actionCell.appendChild(
            editButton
        );

        actionCell.appendChild(
            deleteButton
        );


        row.appendChild(idCell);
        row.appendChild(titleCell);
        row.appendChild(organizationCell);
        row.appendChild(vacancyCell);
        row.appendChild(dateCell);
        row.appendChild(actionCell);


        table.appendChild(row);
    }
}


// ============================================================
// EDIT JOB
// ============================================================

function editJob(job) {

    var jobIdElement =
        document.getElementById("jobId");

    var jobTitleElement =
        document.getElementById("jobTitle");

    var organizationElement =
        document.getElementById("organization");


    if (jobIdElement) {
        jobIdElement.value =
            job.id;
    }

    if (jobTitleElement) {
        jobTitleElement.value =
            job.title;
    }

    if (organizationElement) {
        organizationElement.value =
            job.organization;
    }


    // Get complete job information

    var xhr =
        new XMLHttpRequest();

    xhr.open(
        "GET",
        "../job-details?id="
        + encodeURIComponent(job.id),
        true
    );


    xhr.onreadystatechange = function () {

        if (xhr.readyState !== 4) {
            return;
        }


        if (xhr.status === 200) {

            try {

                var completeJob =
                    JSON.parse(xhr.responseText);


                setValue(
                    "jobTitle",
                    completeJob.title
                );

                setValue(
                    "organization",
                    completeJob.organization
                );

                setValue(
                    "advertisementNumber",
                    completeJob.advertisementNumber
                );

                setValue(
                    "totalVacancies",
                    completeJob.vacancies
                );

                setValue(
                    "qualification",
                    completeJob.qualification
                );

                setValue(
                    "ageLimit",
                    completeJob.ageLimit
                );

                setValue(
                    "salary",
                    completeJob.salary
                );

                setValue(
                    "applicationStartDate",
                    completeJob.applicationStartDate
                );

                setValue(
                    "lastDate",
                    completeJob.lastDate
                );

                setValue(
                    "importantDates",
                    completeJob.importantDates
                );

                setValue(
                    "jobDescription",
                    completeJob.jobDescription
                );

                setValue(
                    "notificationUrl",
                    completeJob.notificationUrl
                );

                setValue(
                    "officialApplyUrl",
                    completeJob.officialApplyUrl
                );


                var formTitle =
                    document.getElementById("formTitle");

                if (formTitle) {
                    formTitle.innerText =
                        "Edit Job";
                }


                var saveButton =
                    document.getElementById("saveJobButton");

                if (saveButton) {
                    saveButton.innerText =
                        "Update Job";
                }


                window.scrollTo({
                    top: 0,
                    behavior: "smooth"
                });


            } catch (error) {

                showAdminMessage(
                    "Unable to load complete job information.",
                    true
                );
            }

        } else {

            showAdminMessage(
                "Unable to load job information.",
                true
            );
        }
    };


    xhr.send();
}


// ============================================================
// SAVE JOB
//
// IMPORTANT:
//
// Frontend -> Backend:
// application/x-www-form-urlencoded
//
// Backend:
// request.getParameter("action")
//
// Backend -> Frontend:
// application/json
// ============================================================

function saveJob() {

    var form =
        document.getElementById("jobForm");

    if (!form) {
        return;
    }


    // Browser validation

    if (!form.checkValidity()) {

        form.reportValidity();

        return;
    }


    var jobId =
        getValue("jobId").trim();


    // Decide ADD or UPDATE

    var action =
        jobId !== ""
            ? "update"
            : "add";


    // --------------------------------------------------------
    // Create form-urlencoded data
    // --------------------------------------------------------

    var data =
        new URLSearchParams();


    data.append(
        "action",
        action
    );

    data.append(
        "id",
        jobId
    );

    data.append(
        "jobTitle",
        getValue("jobTitle").trim()
    );

    data.append(
        "organization",
        getValue("organization").trim()
    );

    data.append(
        "advertisementNumber",
        getValue("advertisementNumber").trim()
    );

    data.append(
        "totalVacancies",
        getValue("totalVacancies").trim()
    );

    data.append(
        "qualification",
        getValue("qualification").trim()
    );

    data.append(
        "ageLimit",
        getValue("ageLimit").trim()
    );

    data.append(
        "salary",
        getValue("salary").trim()
    );

    data.append(
        "applicationStartDate",
        getValue("applicationStartDate")
    );

    data.append(
        "lastDate",
        getValue("lastDate")
    );

    data.append(
        "importantDates",
        getValue("importantDates").trim()
    );

    data.append(
        "jobDescription",
        getValue("jobDescription").trim()
    );

    data.append(
        "notificationUrl",
        getValue("notificationUrl").trim()
    );

    data.append(
        "officialApplyUrl",
        getValue("officialApplyUrl").trim()
    );


    // --------------------------------------------------------
    // DEBUG
    // --------------------------------------------------------

    console.log(
        "ADMIN JOB ACTION:",
        action
    );

    console.log(
        "ADMIN JOB REQUEST:",
        data.toString()
    );


    // --------------------------------------------------------
    // Send form-urlencoded request
    // --------------------------------------------------------

    var xhr =
        new XMLHttpRequest();


    xhr.open(
        "POST",
        "../admin-jobs",
        true
    );


    xhr.setRequestHeader(
        "Content-Type",
        "application/x-www-form-urlencoded;charset=UTF-8"
    );


    xhr.onreadystatechange = function () {

        if (xhr.readyState !== 4) {
            return;
        }


        console.log(
            "ADMIN JOB STATUS:",
            xhr.status
        );

        console.log(
            "ADMIN JOB RESPONSE:",
            xhr.responseText
        );


        var result;


        try {

            result =
                JSON.parse(
                    xhr.responseText
                );

        } catch (error) {

            showAdminMessage(
                "Invalid JSON response from server.",
                true
            );

            return;
        }


        // ----------------------------------------------------
        // Success
        // ----------------------------------------------------

        if (
            xhr.status >= 200
            && xhr.status < 300
            && result.success
        ) {

            showAdminMessage(
                result.message
                || "Job saved successfully.",
                false
            );


            clearJobForm();


            loadAdminJobs();


            return;
        }


        // ----------------------------------------------------
        // Error
        // ----------------------------------------------------

        if (xhr.status === 401) {

            window.location.href =
                "login.jsp";

            return;
        }


        showAdminMessage(
            result.error
            || "Unable to save job.",
            true
        );
    };


    xhr.send(
        data.toString()
    );
}


// ============================================================
// DELETE JOB
//
// Frontend -> Backend:
//
// action=delete&id=123
//
// Backend -> Frontend:
//
// JSON
// ============================================================

function deleteJob(id) {

    var confirmed =
        confirm(
            "Are you sure you want to delete this job?"
        );


    if (!confirmed) {
        return;
    }


    var data =
        new URLSearchParams();


    data.append(
        "action",
        "delete"
    );

    data.append(
        "id",
        id
    );


    console.log(
        "DELETE REQUEST:",
        data.toString()
    );


    var xhr =
        new XMLHttpRequest();


    xhr.open(
        "POST",
        "../admin-jobs",
        true
    );


    xhr.setRequestHeader(
        "Content-Type",
        "application/x-www-form-urlencoded;charset=UTF-8"
    );


    xhr.onreadystatechange = function () {

        if (xhr.readyState !== 4) {
            return;
        }


        console.log(
            "DELETE STATUS:",
            xhr.status
        );

        console.log(
            "DELETE RESPONSE:",
            xhr.responseText
        );


        var result;


        try {

            result =
                JSON.parse(
                    xhr.responseText
                );

        } catch (error) {

            showAdminMessage(
                "Invalid JSON response from server.",
                true
            );

            return;
        }


        if (
            xhr.status >= 200
            && xhr.status < 300
            && result.success
        ) {

            showAdminMessage(
                result.message
                || "Job deleted successfully.",
                false
            );


            loadAdminJobs();

            return;
        }


        if (xhr.status === 401) {

            window.location.href =
                "login.jsp";

            return;
        }


        showAdminMessage(
            result.error
            || "Delete failed.",
            true
        );
    };


    xhr.send(
        data.toString()
    );
}


// ============================================================
// CLEAR JOB FORM
// ============================================================

function clearJobForm() {

    var form =
        document.getElementById("jobForm");

    if (!form) {
        return;
    }


    form.reset();


    setValue(
        "jobId",
        ""
    );


    var formTitle =
        document.getElementById("formTitle");

    if (formTitle) {

        formTitle.innerText =
            "Add New Job";
    }


    var saveButton =
        document.getElementById("saveJobButton");

    if (saveButton) {

        saveButton.innerText =
            "Add Job";
    }
}


// ============================================================
// ADMIN LOGOUT
// ============================================================

function logoutAdmin() {

    var xhr =
        new XMLHttpRequest();

    xhr.open(
        "GET",
        "../admin-logout",
        true
    );


    xhr.onreadystatechange = function () {

        if (xhr.readyState === 4) {

            window.location.href =
                "login.jsp";
        }
    };


    xhr.send();
}


// ============================================================
// ADMIN MESSAGE
// ============================================================

function showAdminMessage(
        message,
        isError) {

    var element =
        document.getElementById(
            "adminMessage"
        );


    if (!element) {
        return;
    }


    element.innerText =
        message;


    if (isError) {

        element.className =
            "form-message error-message";

    } else {

        element.className =
            "form-message success-message";
    }
}


// ============================================================
// GENERAL ERROR
// ============================================================

function showError(message) {

    var element =
        document.getElementById(
            "errorMessage"
        );


    if (!element) {
        return;
    }


    element.innerText =
        message;


    element.style.display =
        "block";
}


// ============================================================
// GET ELEMENT VALUE
// ============================================================

function getValue(id) {

    var element =
        document.getElementById(id);


    if (!element) {
        return "";
    }


    return element.value || "";
}


// ============================================================
// SET ELEMENT VALUE
// ============================================================

function setValue(id, value) {

    var element =
        document.getElementById(id);


    if (!element) {
        return;
    }


    element.value =
        value == null ? "" : value;
}


// ============================================================
// SIMPLE HTML ESCAPE
// ============================================================

function escapeHtml(value) {

    if (
        value === null
        || value === undefined
    ) {

        return "";
    }


    var div =
        document.createElement("div");

    div.innerText =
        value;


    return div.innerHTML;
}
