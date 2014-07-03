<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XDI Discoverer</title>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body>
	<div id="imgtop"><img id="imgtopleft" src="images/xdi2-topleft.png"><img id="imgtopright" src="images/xdi2-topright.png"></div>
	<div id="main">
	<div class="header">
	<span id="appname">XDI Discoverer</span>
	&nbsp;&nbsp;&nbsp;&nbsp;
	<% for (int i=0; i<((Integer) request.getAttribute("sampleInputs")).intValue(); i++) { %>
		<a href="XDIDiscoverer?sample=<%= i+1 %>">Sample <%= i+1 %></a>&nbsp;&nbsp;
	<% } %>
	<a href="index.jsp">&gt;&gt;&gt; Other Apps...</a>
	</div>

	<% if (request.getAttribute("error") != null) { %>
			
		<p style="font-family: monospace; white-space: pre; color: red;"><%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %></p>

	<% } %>

	<form action="XDIDiscoverer" method="post" accept-charset="UTF-8">

		<table cellpadding="3">
		<tr>
		<td>Cloud Name / Cloud Number: </td>
		<td><input type="text" name="input" size="80" value="<%= request.getAttribute("input") != null ? request.getAttribute("input") : "" %>"></td>
		</tr>

		<% String resultFormat = (String) request.getAttribute("resultFormat"); if (resultFormat == null) resultFormat = ""; %>
		<% String writeOrdered = (String) request.getAttribute("writeOrdered"); if (writeOrdered == null) writeOrdered = ""; %>
		<% String writeImplied = (String) request.getAttribute("writeImplied"); if (writeImplied == null) writeImplied = ""; %>
		<% String writeInner = (String) request.getAttribute("writeInner"); if (writeInner == null) writeInner = ""; %>
		<% String writePretty = (String) request.getAttribute("writePretty"); if (writePretty == null) writePretty = ""; %>
		<% String endpoint = (String) request.getAttribute("endpoint"); if (endpoint == null) endpoint = ""; %>
		<% String authority = (String) request.getAttribute("authority"); if (authority == null) authority = ""; %>
		<% String services = (String) request.getAttribute("services"); if (services == null) services = ""; %>

		<tr>
		<td>Discover from registry service: </td>
		<td><input type="text" name="endpoint" size="80" value="<%= endpoint %>"></td>
		</tr>

		<tr>
		<td>&nbsp;</td>
		<td><span style="font-size: .8em;">Use <span style="font-size: 1em; font-weight: bold;"><%= xdi2.discovery.XDIDiscoveryClient.NEUSTAR_PROD_DISCOVERY_XDI_CLIENT.getEndpointUri() %></span> for PROD. Use <span style="font-size: 1em; font-weight: bold;"><%= xdi2.discovery.XDIDiscoveryClient.NEUSTAR_OTE_DISCOVERY_XDI_CLIENT.getEndpointUri() %></span> for OTE.</span></td>
		</tr>

		<tr>
		<td>Discover from XDI authority:</td>
		<td><input name="authority" type="checkbox" <%= authority.equals("on") ? "checked" : "" %>></td>
		</tr>

		<tr>
		<td>Additional services:</td>
		<td><input type="text" name="services" size="80" value="<%= services %>"></td>
		</tr>
		</table>

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

		<input name="writePretty" type="checkbox" <%= writePretty.equals("on") ? "checked" : "" %>>pretty=1

		<input type="submit" value="Go!">
		&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDIDiscovererHelp.jsp">What can I do here?</a>

	</form>

	<% if (request.getAttribute("stats") != null) { %>
		<p>
		<%= request.getAttribute("stats") %>

		<% if (request.getAttribute("output") != null) { %>
			Copy&amp;Paste: <textarea style="width: 100px; height: 1.2em; overflow: hidden"><%= request.getAttribute("output") %></textarea>
		<% } %>

		<% if (request.getAttribute("discoveryResultRegistry") != null) { %>
<!--			<form action="XDIMessenger">
				<input type="hidden" name="endpoint" value="bla">
				<input type="submit" value="Send XDI Message">
			</form> -->
		<% } %>
		</p>
	<% } %>

	<% if (request.getAttribute("output") != null) { %>
		<% if (request.getAttribute("outputId") != null) { %>
			<form class="graphit" target="_blank" action="http://neustar.github.io/xdi-grapheditor/xdi-grapheditor/public_html/index.html"><input type="submit" value="Graph it!"><input type="hidden" name="input" value="<%= request.getRequestURL().toString().replaceFirst("/[^/]+$", "/XDIOutput?outputId=" + request.getAttribute("outputId")) %>"></form>
		<% } %>
		<div class="result"><pre><%= request.getAttribute("output") %></pre></div><br>
	<% } %>

	<% if (request.getAttribute("output2") != null) { %>
		<% if (request.getAttribute("outputId") != null) { %>
			<form class="graphit" target="_blank" action="http://neustar.github.io/xdi-grapheditor/xdi-grapheditor/public_html/index.html"><input type="submit" value="Graph it!"><input type="hidden" name="input" value="<%= request.getRequestURL().toString().replaceFirst("/[^/]+$", "/XDIOutput?outputId=" + request.getAttribute("outputId2")) %>"></form>
		<% } %>
		<div class="result"><pre><%= request.getAttribute("output2") %></pre></div><br>
	<% } %>

	</div>
</body>
</html>
