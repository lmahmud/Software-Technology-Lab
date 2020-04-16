<!DOCTYPE html>
<html lang="en">

<#include "head.ftl" />
<@myheader title="Crowd Fund"></@myheader>

<body class="vsc-initialized">
  <#include "navbar.ftl" />

  <main role="main" class="container">
    <div class="jumbotron shadow text-center"
      style="width: 50%;margin: 0 auto;">
      <h1 class="text-center">Main menu</h1>
      <div class="container ">
        <div class="row">
          <div class="col-md">
            <a href="/browse_projects" class="btn btn-info">Browse
              projects</a>
          </div>
        </div>
        <br>
        <div class="row">
          <div class="col-md">
            <a href="/create_project" class="btn btn-info">Create a
              project</a>
          </div>
        </div>
      </div>
    </div>
  </main>

</body>

</html>
