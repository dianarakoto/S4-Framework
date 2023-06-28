<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.Employee"%>
<%@page import="java.util.Vector"%>
<% 
    String noms = (String) request.getAttribute("employee");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <p><% out.print(noms); %></p>
</body>
</html>