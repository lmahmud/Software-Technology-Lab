<!DOCTYPE html>
<html lang="en">

<#include "head.ftl" />
<@myheader title="Browse"></@myheader>

<body class="vsc-initialized">
  <#include "navbar.ftl" />
  
    <main role="main" class="container">
        <div class="jumbotron shadow">
            <h1 class="display-4 text-center">Browse projects</h1>
            <form method="GET">
                <div class="form-group">
                    <label for="title">Title of the project :</label>
                    <input id="title" name="title" maxlength="300" type="text"
                        class="form-control" placeholder="Title">
                </div>
                <div class="form-group">
                    <label for="status">Status</label>
                    <select class="form-control" id="status" name="status"
                        value="Any">
                        <option>Any</option>
                        <option>Open</option>
                        <option>Successful</option>
                        <option>Failed</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="endd">End date is after: </label>
                    <input id="endd" name="endd" type="date"
                        class="form-control" value="2021-01-01">
                </div>
                <button id="submit-btn" type="submit" class="btn btn-primary">Search
                    Projects</button>
        </div>
        </div>

    </main>

    <script>
        document.getElementById("endd").valueAsDate = new Date();  
    </script>
</body>

</html>