<%@page import="model.Person" %>
<% Person p = (Person) request.getAttribute("test"); %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <p><% out.println(p.getName()); %></p>
</body>
</html>