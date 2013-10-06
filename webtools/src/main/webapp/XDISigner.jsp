<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XDI Signer</title>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body>
	<div id="imgtop"><img id="imgtopleft" src="images/xdi2-topleft.png"><img id="imgtopright" src="images/xdi2-topright.png"></div>
	<div id="main">
	<div class="header">
	<span id="appname">XDI Signer</span>
	&nbsp;&nbsp;&nbsp;&nbsp;
	<% for (int i=0; i<((Integer) request.getAttribute("sampleInputs")).intValue(); i++) { %>
		<a href="XDISigner?sample=<%= i+1 %>">Sample <%= i+1 %></a>&nbsp;&nbsp;
	<% } %>
	<a href="index.jsp">&gt;&gt;&gt; Other Apps...</a>
	</div>

	<% if (request.getAttribute("error") != null) { %>
			
		<p style="font-family: monospace; white-space: pre; color: red;"><%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %></p>

	<% } %>

	<form action="XDISigner" method="post">

		<textarea name="input" style="width: 100%" rows="12"><%= request.getAttribute("input") != null ? request.getAttribute("input") : "" %></textarea><br>

		<% String resultFormat = (String) request.getAttribute("resultFormat"); if (resultFormat == null) resultFormat = ""; %>
		<% String writeImplied = (String) request.getAttribute("writeImplied"); if (writeImplied == null) writeImplied = ""; %>
		<% String writeOrdered = (String) request.getAttribute("writeOrdered"); if (writeOrdered == null) writeOrdered = ""; %>
		<% String writeInner = (String) request.getAttribute("writeInner"); if (writeInner == null) writeInner = ""; %>
		<% String writePretty = (String) request.getAttribute("writePretty"); if (writePretty == null) writePretty = ""; %>
		<% String key = (String) request.getAttribute("key"); if (key == null) key = ""; %>
		<% String address = (String) request.getAttribute("address"); if (address == null) address = ""; %>
		<% String digestAlgorithm = (String) request.getAttribute("digestAlgorithm"); if (digestAlgorithm == null) digestAlgorithm = ""; %>
		<% String digestLength = (String) request.getAttribute("digestLength"); if (digestLength == null) digestLength = ""; %>
		<% String keyAlgorithm = (String) request.getAttribute("keyAlgorithm"); if (keyAlgorithm == null) keyAlgorithm = ""; %>
		<% String keyLength = (String) request.getAttribute("keyLength"); if (keyLength == null) keyLength = ""; %>

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
		</select>
		&nbsp;

		<input name="writeImplied" type="checkbox" <%= writeImplied.equals("on") ? "checked" : "" %>>implied=1

		<input name="writeOrdered" type="checkbox" <%= writeOrdered.equals("on") ? "checked" : "" %>>ordered=1

		<input name="writeInner" type="checkbox" <%= writeInner.equals("on") ? "checked" : "" %>>inner=1

		<input name="writePretty" type="checkbox" <%= writePretty.equals("on") ? "checked" : "" %>>pretty=1<br>

		Digest Algorithm:&nbsp;<input type="text" name="digestAlgorithm" size="5" value="<%= digestAlgorithm.trim() %>">
		Digest Length:&nbsp;<input type="text" name="digestLength" size="5" value="<%= digestLength.trim() %>">
		Key Algorithm:&nbsp;<input type="text" name="keyAlgorithm" size="5" value="<%= keyAlgorithm.trim() %>">
		Key Length:&nbsp;<input type="text" name="keyLength" size="5" value="<%= keyLength.trim() %>">
		&nbsp;--&gt;&nbsp;
		<input type="submit" name="submit" value="Sign!">
		<input type="submit" name="submit" value="Validate!">
		&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDISignerHelp.jsp">What can I do here?</a>

	</form>

	<% if (request.getAttribute("stats") != null) { %>
		<p>
		<%= request.getAttribute("stats") %>

		<% if (request.getAttribute("output") != null) { %>
			Copy&amp;Paste: <textarea style="width: 100px; height: 1.2em; overflow: hidden"><%= request.getAttribute("output") %></textarea>
		<% } %>
		</p>
	<% } %>

	<% if (request.getAttribute("output2") != null) { %>
		<div class="result"><pre><%= request.getAttribute("output2") %></pre></div><br>
	<% } %>

	<% if (request.getAttribute("output") != null) { %>
		<div class="result"><pre><%= request.getAttribute("output") %></pre></div><br>
	<% } %>

	</div>
</body>
</html>
