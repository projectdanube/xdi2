<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XRI Parser</title>
<script type="text/javascript" src="tabber.js"></script>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body style="background-image: url('images/back.png'); background-repeat: repeat-y; margin-left: 60px;">

	<div class="header">
	<img src="images/logo64.png" align="middle">&nbsp;&nbsp;&nbsp;<span id="appname">XRI Parser</span>
	&nbsp;&nbsp;&nbsp;&nbsp;
	<a href="index.jsp">&gt;&gt;&gt; Other Apps...</a>
	</div>

	<% if (request.getAttribute("error") != null) { %>
			
		<p style="font-family: monospace; white-space: pre; color: red;"><%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %></p>

	<% } %>

	<form action="XRIParser" method="post">

		Parse ABNF:
		<select name="rulename" style="width: 200px">
		
		<% for (String rule : (String[]) request.getAttribute("rules")) { %>
			<option class="input" name="rulename" value="<%= rule %>" <%= rule.equals(request.getAttribute("rulename")) ? "selected" : "" %>><%= rule %></option>
		<% } %>
		</select>
		&nbsp;
		<input type="text" class="input" name="input" style="width: 500px" value="<%= request.getAttribute("input") != null ? request.getAttribute("input") : "" %>">

		<input type="submit" value="Go!">

	</form>

	<br>

	<div class="tabber">

	<% if (request.getAttribute("output1") != null) { %>
	    <div class="tabbertab">
		<h2>Tree</h2>
		<div class="result"><pre><%= request.getAttribute("output1") != null ? request.getAttribute("output1") : "" %></pre></div><br>
		</div>
	<% } %>

	<% if (request.getAttribute("output2") != null) { %>
	    <div class="tabbertab">
		<h2>Xml</h2>
		<div class="result"><pre><%= request.getAttribute("output2") != null ? request.getAttribute("output2") : "" %></pre></div><br>
		</div>
	<% } %>

	<% if (request.getAttribute("output3") != null) { %>
	    <div class="tabbertab">
		<h2>Stacks</h2>
		<div class="result"><pre><%= request.getAttribute("output3") != null ? request.getAttribute("output3") : "" %></pre></div><br>
		</div>
	<% } %>

	<% if (request.getAttribute("output4") != null) { %>
	    <div class="tabbertab">
		<h2>Counts</h2>
		<div class="result"><pre><%= request.getAttribute("output4") != null ? request.getAttribute("output4") : "" %></pre></div><br>
		</div>
	<% } %>
	
	</div>
	
</body>
</html>
