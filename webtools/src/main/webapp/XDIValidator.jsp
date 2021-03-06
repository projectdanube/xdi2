<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="xdi2.core.properties.XDI2Properties" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XDI Validator</title>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body>
	<div id="imgtop"><img id="imgtopleft" src="images/xdi2-topleft.png"><a href="http://projectdanube.org/"><img id="imgtopright" src="images/xdi2-topright.png"></a></div>
	<div id="main">
	<div class="header">
	<span id="appname"><img src="images/app20b.png"> XDI Validator</span>
	&nbsp;&nbsp;&nbsp;&nbsp;Examples: 
	<% for (int i=0; i<((Integer) request.getAttribute("sampleInputs")).intValue(); i++) { %>
		<a href="XDIValidator?sample=<%= i+1 %>"><%= i+1 %></a>&nbsp;&nbsp;
	<% } %>
	<a href="index.jsp">&gt;&gt;&gt; Other Apps...</a><br>
	This is version <%= XDI2Properties.properties.getProperty("project.version") %> <%= XDI2Properties.properties.getProperty("project.build.timestamp") %>, Git commit <%= XDI2Properties.properties.getProperty("git.commit.id").substring(0,6) %> <%= XDI2Properties.properties.getProperty("git.commit.time") %>.
	</div>

	<% if (request.getAttribute("error") != null) { %>
			
		<p style="font-family: monospace; white-space: pre; color: red;"><%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %></p>

	<% } %>

	<form action="XDIValidator" method="post" accept-charset="UTF-8">

		<textarea class="input" name="input" style="width: 100%" rows="12"><%= request.getAttribute("input") != null ? request.getAttribute("input") : "" %></textarea><br>

		<% String from = (String) request.getAttribute("from"); if (from == null) from = ""; %>
		<% String to = (String) request.getAttribute("to"); if (to == null) to = ""; %>

		<p>
		Validate:
		<select name="from">
		<option value="AUTO" <%= from.equals("AUTO") ? "selected" : "" %>>auto-detect</option>
		<option value="XDI DISPLAY" <%= from.equals("XDI DISPLAY") ? "selected" : "" %>>XDI DISPLAY</option>
		<option value="JXD" <%= from.equals("JXD") ? "selected" : "" %>>JXD</option>
		<option value="XDI/JSON/TRIPLE" <%= from.equals("XDI/JSON/TRIPLE") ? "selected" : "" %>>XDI/JSON/TRIPLE</option>
		<option value="XDI/JSON/QUAD" <%= from.equals("XDI/JSON/QUAD") ? "selected" : "" %>>XDI/JSON/QUAD</option>
		</select>
		<input type="submit" value="Go!">
		&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDIValidatorHelp.jsp">What can I do here?</a>
		</p>

	</form>

	<div class="line"></div>

	<% if (request.getAttribute("stats") != null) { %>
		<p>
		<%= request.getAttribute("stats") %>
		</p>
	<% } %>

	<% if (request.getAttribute("output") != null) { %>
		<div class="result"><pre><%= request.getAttribute("output") %></pre></div><br>
	<% } %>

	</div>
</body>
</html>
