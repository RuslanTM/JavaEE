<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<body>


    <h3>Hello anonymous!</h3>
    <div>your name is '<%=request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Public person" %>'</div>
    <div>is admin '<%=request.isUserInRole("administrator")%>'</div>
    <div>is manager '<%=request.isUserInRole("manager")%>'</div>
    <div>is tomcat  '<%=request.isUserInRole("tomcat")%>'</div>

</body>
</html>