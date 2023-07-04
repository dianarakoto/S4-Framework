<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Accueil</title>
    </head>
    <body>
        <form action="save-emp" method="post" enctype="multipart/form-data">
            <input type="text" name="name" placeholder="Identifiant" value="Diana">
            <input type="file" name="badge">
            <input type="submit" value="Valider">
        </form>
        <a href="find-emp">Find</a>
        <a href="test-fw">Testing</a>
        <form action="test-singleton" method="post" enctype="multipart/form-data">
            <input type="text" name="id">
            <input type="text" name="name" placeholder="Identifiant" value="Diana">
            <input type="submit" value="Valider">
        </form>
    </body>
</html>