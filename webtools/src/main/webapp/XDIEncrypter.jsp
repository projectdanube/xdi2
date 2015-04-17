<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="xdi2.core.properties.XDI2Properties" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XDI Encrypter</title>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body>
	<div id="imgtop"><img id="imgtopleft" src="images/xdi2-topleft.png"><img id="imgtopright" src="images/xdi2-topright.png"></div>
	<div id="main">
	<div class="header">
	<span id="appname"><img src="images/app20b.png"> XDI Encrypter</span>
	&nbsp;&nbsp;&nbsp;&nbsp;Examples: 
	<% for (int i=0; i<((Integer) request.getAttribute("sampleInputs")).intValue(); i++) { %>
		<a href="XDIEncrypter?sample=<%= i+1 %>"><%= i+1 %></a>&nbsp;&nbsp;
	<% } %>
	<a href="index.jsp">&gt;&gt;&gt; Other Apps...</a><br>
	This is version <%= XDI2Properties.properties.getProperty("project.version") %> <%= XDI2Properties.properties.getProperty("project.build.timestamp") %>, Git commit <%= XDI2Properties.properties.getProperty("git.commit.id").substring(0,6) %> <%= XDI2Properties.properties.getProperty("git.commit.time") %>.
	</div>

	<% if (request.getAttribute("error") != null) { %>
			
		<p style="font-family: monospace; white-space: pre; color: red;"><%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %></p>

	<% } %>

	<form action="XDIEncrypter" method="post" accept-charset="UTF-8">

		<textarea class="input" name="input" style="width: 100%" rows="12"><%= request.getAttribute("input") != null ? request.getAttribute("input") : "" %></textarea><br>

		<% String resultFormat = (String) request.getAttribute("resultFormat"); if (resultFormat == null) resultFormat = ""; %>
		<% String writeImplied = (String) request.getAttribute("writeImplied"); if (writeImplied == null) writeImplied = ""; %>
		<% String writeOrdered = (String) request.getAttribute("writeOrdered"); if (writeOrdered == null) writeOrdered = ""; %>
		<% String writePretty = (String) request.getAttribute("writePretty"); if (writePretty == null) writePretty = ""; %>
		<% String key = (String) request.getAttribute("key"); if (key == null) key = ""; %>
		<% String address = (String) request.getAttribute("address"); if (address == null) address = ""; %>
		<% String keyAlgorithm = (String) request.getAttribute("keyAlgorithm"); if (keyAlgorithm == null) keyAlgorithm = ""; %>
		<% String keyLength = (String) request.getAttribute("keyLength"); if (keyLength == null) keyLength = ""; %>
		<% String singleton = (String) request.getAttribute("singleton"); if (singleton == null) singleton = ""; %>

		<p>
		Key: 
		<input type="text" name="key" size="30" value="<%= key %>">
		Address: 
		<input type="text" name="address" size="10" value="<%= address %>">

		Result Format:
		<select name="resultFormat">
		<option value="XDI/JSON" <%= resultFormat.equals("XDI/JSON") ? "selected" : "" %>>XDI/JSON</option>
		<option value="XDI DISPLAY" <%= resultFormat.equals("XDI DISPLAY") ? "selected" : "" %>>XDI DISPLAY</option>
		<option value="XDI/JSON/TREE" <%= resultFormat.equals("XDI/JSON/TREE") ? "selected" : "" %>>XDI/JSON/TREE</option>
		<option value="XDI/JSON/PARSE" <%= resultFormat.equals("XDI/JSON/PARSE") ? "selected" : "" %>>XDI/JSON/PARSE</option>
		<option value="XDI/JSON/TRIPLE" <%= resultFormat.equals("XDI/JSON/TRIPLE") ? "selected" : "" %>>XDI/JSON/TRIPLE</option>
		<option value="XDI/JSON/QUAD" <%= resultFormat.equals("XDI/JSON/QUAD") ? "selected" : "" %>>XDI/JSON/QUAD</option>
		</select>
		&nbsp;

		<input name="writeImplied" type="checkbox" <%= writeImplied.equals("on") ? "checked" : "" %>>implied=1

		<input name="writeOrdered" type="checkbox" <%= writeOrdered.equals("on") ? "checked" : "" %>>ordered=1

		<input name="writePretty" type="checkbox" <%= writePretty.equals("on") ? "checked" : "" %>>pretty=1<br>
		</p>

		Key Alg:&nbsp;<input type="text" name="keyAlgorithm" size="5" value="<%= keyAlgorithm.trim() %>">
		Key Len:&nbsp;<input type="text" name="keyLength" size="5" value="<%= keyLength.trim() %>">
		<input name="singleton" type="checkbox" <%= singleton.equals("on") ? "checked" : "" %>>Singleton
		&nbsp;--&gt;&nbsp;
		<input type="submit" name="submit" value="Encrypt!">
		<input type="submit" name="submit" value="Decrypt!">
		&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDIEncrypterHelp.jsp">What can I do here?</a>

	</form>

	<br><div class="line"></div>

	<% if (request.getAttribute("stats") != null) { %>
		<p>
		<%= request.getAttribute("stats") %>

		<% if (request.getAttribute("output2") != null) { %>
			Copy&amp;Paste: <textarea style="width: 100px; height: 1.2em; overflow: hidden"><%= request.getAttribute("output") %></textarea>
		<% } %>
		</p>
	<% } %>

	<% if (request.getAttribute("output2") != null) { %>
		<div class="result"><pre><%= request.getAttribute("output2") %></pre></div><br>
	<% } %>

	<% if (request.getAttribute("output") != null) { %>
		<% if (request.getAttribute("outputId") != null && ! "".equals(request.getAttribute("output")) && ! "".equals(request.getAttribute("outputId"))) { %>
			<a class="graphit" target="_blank" href="http://neustar.github.io/xdi-grapheditor/xdi-grapheditor/public_html/index.html?input=<%= request.getRequestURL().toString().replaceFirst("/[^/]+$", "/XDIOutput?outputId=" + request.getAttribute("outputId")) %>">Graph It!</a>
		<% } %>
		<div class="result"><pre><%= request.getAttribute("output") %></pre></div><br>
	<% } %>

	</div>
</body>
</html>
