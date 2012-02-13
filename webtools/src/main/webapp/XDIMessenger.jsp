<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>XDI Messenger</title>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body>

	<div class="header">
	<img src="images/logo64.png" align="middle">&nbsp;&nbsp;&nbsp;<span id="appname">XDI Messenger</span>
	&nbsp;&nbsp;&nbsp;&nbsp;
	<% for (int i=0; i<((Integer) request.getAttribute("sampleInputs")).intValue(); i++) { %>
		<a href="XDIMessenger?sample=<%= i+1 %>">Sample <%= i+1 %></a>&nbsp;&nbsp;
	<% } %>
	<a href="index.jsp">&gt;&gt;&gt;Other Apps...</a>
	</div>

	<% if (request.getAttribute("error") != null) { %>
			
		<p><font color="red"><%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %></font></p>

	<% } %>

	<form action="XDIMessenger" method="post">

		<textarea name="input" style="width: 100%" rows="12"><%= request.getAttribute("input") != null ? request.getAttribute("input") : "" %></textarea><br>

		<% String endpoint = (String) request.getAttribute("endpoint"); if (endpoint == null) endpoint = ""; %>

		Send to endpoint: 
		<input type="text" name="endpoint" size="80" value="<%= endpoint %>">
		<input type="submit" value="Go!">
		&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDIMessengerHelp.jsp">What can I do here?</a>

		<% if (request.getAttribute("stats") != null) { %>
			<p>
			<%= request.getAttribute("stats") %>

			<% if (request.getAttribute("output") != null) { %>
				Copy&amp;Paste: <textarea style="width: 100px; height: 1.2em; overflow: hidden"><%= request.getAttribute("output") != null ? request.getAttribute("output") : "" %></textarea>
			<% } %>
			</p>
		<% } %>

		<% if (request.getAttribute("output") != null) { %>
			<div class="result"><pre><%= request.getAttribute("output") != null ? request.getAttribute("output") : "" %></pre></div><br>
		<% } %>
	</form>

</body>
</html>
