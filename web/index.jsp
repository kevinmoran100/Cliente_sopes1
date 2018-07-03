<%-- 
    Document   : index
    Created on : 02-jul-2018, 17:46:51
    Author     : kevin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <%
            Object se = session.getAttribute("user");
            if (se == null || se.toString().equals("")) {
                response.sendRedirect("/login.jsp");
            }
            else{
                response.sendRedirect("/home");
            }
        %>
        
    </body>
</html>
