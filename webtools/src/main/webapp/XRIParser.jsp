<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XRI Parser</title>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body style="background-image: url('images/back.png'); background-repeat: repeat-y; margin-left: 60px;">

	<div class="header">
	<img src="images/logo64.png" align="middle">&nbsp;&nbsp;&nbsp;<span id="appname">XRI Parser</span>
	&nbsp;&nbsp;&nbsp;&nbsp;
	<a href="index.jsp">&gt;&gt;&gt; Other Apps...</a>
	</div>

	<% if (request.getAttribute("error") != null) { %>
			
		<p><font color="red"><%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %></font></p>

	<% } %>

	<form action="XRIParser" method="post">

		ABNF Rule: (e.g. xri, xri-reference, xri-segment, subseg, xref, ...): <input type="text" class="input" name="rulename" style="width: 200px" value="<%= request.getAttribute("rulename") != null ? request.getAttribute("rulename") : "" %>"><br>
		String: <input type="text" class="input" name="input" style="width: 500px" value="<%= request.getAttribute("input") != null ? request.getAttribute("input") : "" %>">

		<input type="submit" value="Go!">

	</form>

	<br>

	<% if (request.getAttribute("output") != null) { %>
		<div class="result"><pre><%= request.getAttribute("output") != null ? request.getAttribute("output") : "" %></pre></div><br>
	<% } %>
	
</body>
</html>
