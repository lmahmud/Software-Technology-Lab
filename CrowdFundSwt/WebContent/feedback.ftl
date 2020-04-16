<!DOCTYPE html>
<html lang="en">

<#include "head.ftl" />
<@myheader title=mainmsg!"Feedback"></@myheader>

<body class="vsc-initialized">
  <#include "navbar.ftl" />

  <main role="main" class="container">
    <div class="jumbotron shadow text-center"
      style="width: 50%;margin: 0 auto;">
      <h1 class="text-center">${mainmsg!"Error"}</h1>
      <h4 class="text-center"><p>${msg!"Error occured."}</p></h4>
      <a href="/" class="button btn btn-primary">Go Home</a>
    </div>
  </main>

</body>

</html>
