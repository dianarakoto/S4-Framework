<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.Employee"%>
<%@page import="java.util.Vector"%>
<% 
    out.println("oui");
    Vector<String> noms = (Vector<String>) request.getAttribute("allEmployees");
    out.println("ok");
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
    <% for(int i=0; i<noms.size(); i++) { %>
        <p><%= noms.get(i); %></p>
    <% } %>
    <p>yooooooo</p>
</body>
</html>