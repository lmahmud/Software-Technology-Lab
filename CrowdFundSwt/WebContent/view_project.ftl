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
        <h2>${pj.title}</h2>
      </div>
      <div class="container" style="font-size: 1.2em;">
        <div class="progress border border-dark">
          <div id="pgb" style="width: 1%" class="progress-bar
          progress-bar-striped progress-bar-animated bg-success" 
          role="progressbar"><a id="pgr">0</a></div>
        </div>
        <div class="row">
          <b>Project Starter : </b>${pj.psname}
        </div>
        <div class="row">
          <b>Status :</b> ${pj.status}
        </div>
        <div class="row">
          <b>End Date :</b> ${pj.endDate}
        </div>
        <div class="row">
          <b>Funding Limit :</b> <a class="" id="flimit">${pj.fundingLimit}</a>€
        </div>
        <div class="row">
          <b>Currently Funded :</b> <a class="" id="curr">${pj.currentlyFunded}</a>€
        </div>
        <div class="row">
          <b>Number of donations :</b> ${pj.donationsNum}
        </div>
        <div class="row">
          <b>Description : </b> <p>${pj.description}</p>
        </div>
      </div>
    </div>

	<#if !pj.temp && pj.status=="Open">
    <div class="jumbotron shadow text-center" style="height: 5em; padding-top: 1.5em;">
      <a href="/donate_project?id=${pj.id}" class="button btn btn-primary">Donate</a>
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
