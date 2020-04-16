<!DOCTYPE html>
<html lang="en">

<#include "head.ftl" />
<@myheader title="Project"></@myheader>

<body class="vsc-initialized">
  <#include "navbar.ftl" />

  <main role="main" class="container">
    <div class="jumbotron shadow">
      <div class="container text-center">
        <img src="/img/icon.png" alt=".." style="width: 96px;">
        <h2>${pjrw.project.title}</h2>
      </div>
      <div class="container" style="font-size: 1.2em;">
        <div class="progress border border-dark">
          <div id="pgb" style="width: 1%" class="progress-bar
          progress-bar-striped progress-bar-animated bg-success" 
          role="progressbar"><a id="pgr">0</a></div>
        </div>
        <div class="row">
          <b>Project Starter : </b>${pjrw.project.psname}
        </div>
        <div class="row">
          <b>Status :</b> <h6 class="${pjrw.project.status}">${pjrw.project.status}</h6>
        </div>
        <div class="row">
          <b>End Date :</b> ${pjrw.project.endDate}
        </div>
        <div class="row">
          <b>Funding Limit :</b> <a class="" id="flimit">${pjrw.project.fundingLimit}</a>€
        </div>
        <div class="row">
          <b>Currently Funded :</b> <a class="" id="curr">${pjrw.project.currentlyFunded}</a>€
        </div>
        <div class="row">
          <b>Number of donations :</b> ${pjrw.project.donationsNum}
        </div>
        <div class="row">
          <b>Description : </b> <p>${pjrw.project.description}</p>
        </div>
        <div class="row">
          <table class="table">
            <thead>
            <tr>
              <th scope="col">Reward</th>
              <th scope="col">Amount (€)</th>
            </tr>
            </thead>
            <tbody>
            <#list pjrw.rewards as rw>
              <tr>
                <td>${rw.reward}</td>
                <td>${rw.amount}</td>
              </tr>
            </#list>
            </tbody>
          </table>
        </div>
      </div>
    </div>

	<#if !pjrw.project.temp && pjrw.project.status=="Open">
    <div class="jumbotron shadow text-center" style="height: 5em; padding-top: 1.5em;">
      <a href="/donate_project?id=${pjrw.project.id}" class="button btn btn-primary">Donate</a>
    </div>
    </#if>
  </main>

  <script>
    const flimit = parseFloat(document.getElementById("flimit").innerText);
    const curr = parseFloat(document.getElementById("curr").innerText);
    const percent = Math.round(curr*100/flimit).toString() + "%";
    document.getElementById("pgr").innerText = percent;
    document.getElementById("pgb").style.width = percent;
</script>

</body>

</html>
