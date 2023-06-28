<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.Employee"%>
<%@page import="java.util.Vector"%>
<% 
    Vector<Employee> noms = (Vector<Employee>) request.getAttribute("allEmployees");
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
        <p><a href="get-info?id=<% out.print(noms.get(i).getId()); %>&&name=<% out.print(noms.get(i).getName()); %>"><% out.print(noms.get(i).getId()); %></a><% out.print(noms.get(i).getName()); %></p>
    <% } %>
</body>
</html>