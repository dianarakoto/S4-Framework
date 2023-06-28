<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.Employee"%>
<%@page import="java.util.Vector"%>
<% 
    Vector<String> noms = (Vector<String>) request.getAttribute("allEmployees");
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
        <p><% out.print(noms.get(i)); %></p>
    <% } %>
</body>
</html>