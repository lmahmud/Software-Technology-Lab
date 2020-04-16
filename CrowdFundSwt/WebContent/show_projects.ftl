<!DOCTYPE html>
<html lang="en">

<#include "head.ftl" />
<@myheader title="Projects"></@myheader>

<body class="vsc-initialized">
  <#include "navbar.ftl" />

  <main role="main" class="container">
  <div class="jumbotron shadow text-center" style="height: 5em; padding-top: 1.5em;">
      	<a href="/browse_projects" class="button btn btn-primary">Reset</a>
  </div>
  <#list projects as p>
  <#if p.temp><#assign stat="Temp"><#else><#assign stat=p.status></#if>
    <a href="/view_project?id=${p.id}" class="mya">
      <div class="card mb-3 shadow zcard" style="padding: 0 2em;">
        <div class="row no-gutters">
          <div class="col-md-2">
            <img src="/img/icon.png" style="width: 8em; padding: 1em 0;"
              class="card-img" alt="...">
          </div>
          <div class="col-md-10">
            <div class="card-body">
              <div class="row">
                <div class="col-md">
                  <h5 class="card-title">${p.title}</h5>
                </div>
                <div class="col-md-2">
                  <h5 class="card-text status ${p.status}">
                    ${stat}</h5>
                </div>
              </div>
              <p class="card-text text-muted">By ${p.psname}</p>
              <p class="card-text desc">${p.description}</p>
            </div>
          </div>
        </div>
      </div>
    </a>
  </#list>

  </main>

  <script>
    var descs = document.getElementsByClassName("desc");
    Array.from(descs).forEach(function (d) {
      if (d.innerHTML.length > 290) {
        d.innerHTML = d.innerHTML.substring(0, 290) + ' ...';
      }
    });
  </script>

</body>

</html>
