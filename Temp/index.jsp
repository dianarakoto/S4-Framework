<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Accueil</title>
    </head>
    <body>
        <form action="save-emp" method="get">
            <input type="text" name="id" placeholder="Identifiant" value="1">
            <input type="text" name="name" placeholder="Nom" value="Diana">
            <input type="submit" value="Valider">
        </form>
        <a href="find-emp">Find</a>
        <a href="test-fw">Testing</a>
    </body>
</html>