<%@page contentType="text/html"  pageEncoding="UTF-8"
              isErrorPage="true"%>
<%@page import="java.io.PrintWriter"%>
<html>
<head><title>Error</title></head>
<body>
  <h1>Error：</h1><%= exception %>
  <h2>PrintStackTrace：</h2>
<%
    exception.printStackTrace(new PrintWriter(out));
%>
</body>
</html>