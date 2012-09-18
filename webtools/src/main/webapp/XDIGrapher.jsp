<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XDI Grapher</title>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body style="background-image: url('images/back.png'); background-repeat: repeat-y; margin-left: 60px;">

	<div class="header">
	<img src="images/logo64.png" align="middle">&nbsp;&nbsp;&nbsp;<span id="appname">XDI Grapher</span>
	&nbsp;&nbsp;&nbsp;&nbsp;
	<% for (int i=0; i<((Integer) request.getAttribute("sampleInputs")).intValue(); i++) { %>
		<a href="XDIGrapher?sample=<%= i+1 %>">Sample <%= i+1 %></a>&nbsp;&nbsp;
	<% } %>
	<a href="index.jsp">&gt;&gt;&gt; Other Apps...</a>
	</div>

	<% if (request.getAttribute("error") != null) { %>
			
		<p><font color="red"><%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %></font></p>

	<% } %>

	<form action="XDIGrapher" method="post">

		<textarea class="input" name="input" style="width: 100%" rows="12"><%= request.getAttribute("input") != null ? request.getAttribute("input") : "" %></textarea><br>

		<% String type = (String) request.getAttribute("type"); if (type == null) type = ""; %>

		<select name="type">
		<option value="stdl" <%= type.equals("stdl") ? "selected" : "" %>>XDI Standard Graph (with legend)</option>
		<option value="std" <%= type.equals("std") ? "selected" : "" %>>XDI Standard Graph (without legend)</option>
		</select>
		<input type="submit" value="Draw!">
		&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDIGrapherHelp.jsp">What can I do here?</a>

	</form>

	<% if (request.getAttribute("stats") != null) { %>
		<p>
		<%= request.getAttribute("stats") %>
		</p>
	<% } %>

	<% if (request.getAttribute("imageId") != null) { %>
		<div>
			<img src="/XDIGrapherImage?imageId=<%= request.getAttribute("imageId") %>">
		</div><br>
	<% } %>

</body>
</html>
