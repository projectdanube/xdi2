<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>XDI Grapher</title>
</head>
<body>

	<img src="images/logo_xdi4j.gif" align="middle">&nbsp;&nbsp;&nbsp;<span style="font-weight: bold; border-bottom: 3px solid #707070">XDI Grapher</span> by Azigo
	&nbsp;&nbsp;&nbsp;&nbsp;<a href="Other.jsp" style="color: #707070">Other Apps...</a>

	<% if (request.getAttribute("error") != null) { %>
			
		<p><font color="red"><%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %></font></p>

	<% } %>

	<form action="XDIGrapher" method="post">

		<textarea name="input" style="width: 100%" rows="12"><%= request.getAttribute("input") != null ? request.getAttribute("input") : "" %></textarea><br>

		<% String type = (String) request.getAttribute("type"); if (type == null) type = ""; %>

		<select name="type">
		<option value="box" <%= type.equals("box") ? "selected" : "" %>>XDI RDF Box Graph</option>
		<option value="spol" <%= type.equals("spol") ? "selected" : "" %>>XDI S/P/O Graph (with legend)</option>
		<option value="spo" <%= type.equals("spo") ? "selected" : "" %>>XDI S/P/O Graph (without legend)</option>
		</select>
		<input type="submit" value="Draw!">
		&nbsp;&nbsp;&nbsp;&nbsp;<a href="Help.jsp" style="color: #707070">What can I do here?</a>

		<% if (request.getAttribute("stats") != null) { %>
			<p>
			<%= request.getAttribute("stats") %>
			</p>
		<% } %>

		<% if (request.getAttribute("imageId") != null) { %>
			<div>
				<img src="image?imageId=<%= request.getAttribute("imageId") %>">
			</div><br>
		<% } %>
	</form>

</body>
</html>
