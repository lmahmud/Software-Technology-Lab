<!DOCTYPE html>
<html lang="en">

<#include "head.ftl" />
<@myheader title="Create Project"></@myheader>

<body class="vsc-initialized">
  <#include "navbar.ftl" />

  <main role="main" class="container">
    <div class="jumbotron shadow">
      <h1 class="display-4 text-center">Fund a project</h1>
      <form method="POST">
        <div class="form-group">
          <label for="title">Title of the project :</label>
          <input id="title" name="title" maxlength="300" type="text"
            class="form-control" placeholder="Title">
        </div>
        <div class="form-group">
          <label for="flimt">Funding Limit : €</label>
          <input id="flimit" name="flimit" maxlength="25" type="number"
            class="form-control" placeholder="Amount">
        </div>
        <div class="form-group">
          <label for="description">Description</label>
          <textarea name="description" class="form-control" id="description" maxlength="999"
            placeholder="Description" style="max-height: 50vh;"></textarea>
        </div>
        <div class="form-group">
          <label for="endd">End date : </label>
          <input id="endd" name="endd" type="date" class="form-control" value="2021-01-01" min="2020-01-01">
        </div>
        <div class="form-group">
          <label for="rewards">Rewards : <b>Reward - amount</b> (one reward per line)</label>
          <textarea name="rewards" class="form-control" id="rewards" maxlength="999"
            placeholder="Reward one - 50&#10;Reward two - 100.50" style="max-height: 50vh;"></textarea>
        </div>
        <div class="form-group">
          <label for="email">Your email :</label>
          <input id="email" name="psemail" maxlength="300" type="text"
            class="form-control" placeholder="john@example.com">
        </div>
        <div class="form-group">
          <label for="name">Your Name :</label>
          <input id="name" name="psname" maxlength="300" type="text"
            class="form-control" placeholder="John Doe">
        </div>
        <div class="form-group">
          <label for="payinfo">Your bank account number (IBAN):</label>
          <input id="payinfo" name="payinfo" maxlength="60" type="text"
            class="form-control" placeholder="DE123456789">
        </div>
        <button type="submit" class="btn btn-primary">Create Project</button>
    </div>
  </main>

  <script>
    var today = new Date().toISOString().split('T')[0];
    document.getElementById("endd").valueAsDate = new Date();
    document.getElementById("endd").setAttribute("min", today);    
  </script>
</body>

</html>