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
      <link rel="stylesheet" type="text/css" href="css/icontrol.css">
      <title>Big-IP Pools</title>
      <jsp:include page="iControlServlet"/>
    </head>
    <body>
      <div id="main">
        <div id="header">
          <div id="widgetBar">
            
            <div class="headerWidget">
              [ show pools toggle ]
            </div>
          
            <div class="headerWidget">
              [ manage pools widget ]
            </div>
          </div>
          
          <a href="#">
            <img src="images/F5-logo.jpg" id="logo" alt="F5 logo">
          </a>
          
          <img src="#" id="logoText" alt="Pool Manager">
        </div>

        <div id="indexLeftColumn">
          <div id="applet">
            <center>
              <applet code=com/wizards/operations/icontrol/applet/PoolStats.class
                      width=350 height=400>
              </applet>
            </center>
          </div>
        </div>

        <div id="indexRightColumn">
          <table>
            <c:forEach var="pool" items="${pools}">
              <tr>
                <td>${pool.name}</td>
                <td>${pool.description}</td>
              </tr>
            </c:forEach>
          </table>
        </div>
      <div id="footer">
        <hr>
        <p id="footerText">[ footer text ]</p>
      </div>
    </div>
  </body>
</html>
