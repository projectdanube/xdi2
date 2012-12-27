<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XDI Local Messenger</title>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
<script language="javascript" type="text/javascript">
function updateSamples() {
	var categorySelect = document.getElementById('categorySelect');
	var category = categorySelect.options[categorySelect.selectedIndex].value;
	var html = '';
	<% for (int c=0; c<((List<String>) request.getAttribute("sampleCategories")).size(); c++) { %>
		if (category == <%= c %>) {
			<% for (int i=0; i<((List<List<String>>) request.getAttribute("sampleInputs")).get(c).size(); i++) { %>
				html += '<a href="XDILocalMessenger?category=<%= c+1 %>&sample=<%= i+1 %>" title="<%= ((List<List<String>>) request.getAttribute("sampleTooltips")).get(c).get(i) %>">Sample <%= i+1 %></a>&nbsp;&nbsp;';
			<% } %>
		}
	<% } %>
	document.getElementById('samples').innerHTML = html;
}
</script>
</head>
<body style="background-image: url('images/back.png'); background-repeat: repeat-y; margin-left: 60px;" onload="updateSamples();">

	<div class="header">
	<img src="images/logo64.png" align="middle">&nbsp;&nbsp;&nbsp;<span id="appname">XDI Local Messenger</span>
	&nbsp;&nbsp;&nbsp;&nbsp;
	<select id="categorySelect" onchange="updateSamples();">
	<% for (int i=0; i<((List<String>) request.getAttribute("sampleCategories")).size(); i++) { %>
		<option value="<%= i %>" <%= (i + 1) == Integer.parseInt((String) request.getAttribute("category")) ? "selected" : "" %>><%= ((List<String>) request.getAttribute("sampleCategories")).get(i) %></option>
	<% } %>
	</select>
	&nbsp;&nbsp;
	<span id="samples"></span>
	<a href="index.jsp">&gt;&gt;&gt; Other Apps...</a>
	</div>

	<% if (request.getAttribute("error") != null) { %>

		<p style="font-family: monospace; white-space: pre; color: red;"><%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %></p>

	<% } %>

	<form action="XDILocalMessenger" method="post">

		<table width="100%" cellspacing="0" cellpadding="0" border="0">
		<tr>
		<td width="50%" style="padding-right: 10px">
			<textarea class="input" name="input" style="width: 100%" rows="12"><%= request.getAttribute("input") != null ? request.getAttribute("input") : "" %></textarea><br>
		</td>
		<td width="50%" style="padding-left: 10px">
			<textarea class="input" name="message" style="width: 100%" rows="12"><%= request.getAttribute("message") != null ? request.getAttribute("message") : "" %></textarea><br>
		</td>
		</tr>
		</table>

		<% String resultFormat = (String) request.getAttribute("resultFormat"); if (resultFormat == null) resultFormat = ""; %>
		<% String writeContexts = (String) request.getAttribute("writeContexts"); if (writeContexts == null) writeContexts = ""; %>
		<% String writeOrdered = (String) request.getAttribute("writeOrdered"); if (writeOrdered == null) writeOrdered = ""; %>
		<% String writePretty = (String) request.getAttribute("writePretty"); if (writePretty == null) writePretty = ""; %>
		<% String variablesSupport = (String) request.getAttribute("variablesSupport"); if (variablesSupport == null) variablesSupport = ""; %>
		<% String dollarRefSupport = (String) request.getAttribute("dollarRefSupport"); if (dollarRefSupport == null) dollarRefSupport = ""; %>
		<% String linkContractsSupport = (String) request.getAttribute("linkContractsSupport"); if (linkContractsSupport == null) linkContractsSupport = ""; %>

		Result Format:
		<select name="resultFormat">
		<option value="XDI/JSON" <%= resultFormat.equals("XDI/JSON") ? "selected" : "" %>>XDI/JSON</option>
		<option value="XDI DISPLAY" <%= resultFormat.equals("XDI DISPLAY") ? "selected" : "" %>>XDI DISPLAY</option>
		</select>
		&nbsp;

		<input name="writeContexts" type="checkbox" <%= writeContexts.equals("on") ? "checked" : "" %>>contexts=1

		<input name="writeOrdered" type="checkbox" <%= writeOrdered.equals("on") ? "checked" : "" %>>ordered=1

		<input name="writePretty" type="checkbox" <%= writePretty.equals("on") ? "checked" : "" %>>pretty=1

		<input name="variablesSupport" type="checkbox" <%= variablesSupport.equals("on") ? "checked" : "" %>>Variables

		<input name="dollarRefSupport" type="checkbox" <%= dollarRefSupport.equals("on") ? "checked" : "" %>>$ref Support

		<input name="linkContractsSupport" type="checkbox" <%= linkContractsSupport.equals("on") ? "checked" : "" %>>Link Contracts&nbsp;

		<input type="hidden" name="category" value="<%= (String) request.getAttribute("category") %>">
		<input type="hidden" name="sample" value="<%= (String) request.getAttribute("sample") %>">
		<input type="submit" value="Go!">
		&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDILocalMessengerHelp.jsp">What can I do here?</a>

	</form>

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
	
</body>
</html>
