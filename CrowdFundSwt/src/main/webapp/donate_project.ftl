<!DOCTYPE html>
<html lang="en">

<#include "head.ftl" />
<@myheader title="Donate to a Project"></@myheader>

<body class="vsc-initialized">
<#include "navbar.ftl" />

<main role="main" class="container">
    <div class="jumbotron shadow">
        <h1 class="display-4 text-center">Fund the project</h1>
        <form method="POST">
            <div class="form-group">
                <label for="amount">Donation amount : €</label>
                <input id="amount" name="amount" maxlength="25" type="number"
                       step="0.01" class="form-control" placeholder="Amount">
            </div>
            <div class="form-group">
                <label for="email">Your email :</label>
                <input id="email" name="semail" maxlength="300" type="text"
                       class="form-control" placeholder="john@example.com">
            </div>
            <div class="form-group">
                <label for="name">Your Name :</label>
                <input id="name" name="sname" maxlength="300" type="text"
                       class="form-control" placeholder="John Doe">
            </div>
            <div class="form-group">
                <label for="payinfo">Your bank account number (IBAN):</label>
                <input id="payinfo" name="spayinfo" maxlength="60" type="text"
                       class="form-control" placeholder="DE123456789">
            </div>
            <button id="submit-btn" type="submit" class="btn btn-primary">Fund Project</button>
        </form>
    </div>
</main>
</body>

</html>