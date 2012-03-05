<%-- 
    Document   : index
    Created on : Mar 4, 2012, 6:36:14 PM
    Author     : hildebj
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Pools</title>
    </head>
    <body>
      <table>
        <c:forEach var="pool" items="${pools}">
          <tr>
            <td>${pool.name}</td>
            <td>${pool.connections}</td>
            <td>${pool.description}</td>
          </tr>
        </c:forEach>
      </table>
    </body>
</html>
