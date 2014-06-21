<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="xdi2.core.xri3.CloudNumber" %><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XDI Operator</title>
<script type="text/javascript" src="tabber.js"></script>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body>
	<div id="imgtop"><img id="imgtopleft" src="images/xdi2-topleft.png"><img id="imgtopright" src="images/xdi2-topright.png"></div>
	<div id="main">
	<div class="header">
	<span id="appname">XDI Operator</span>
	&nbsp;&nbsp;&nbsp;&nbsp;
	<a href="index.jsp">&gt;&gt;&gt; Other Apps...</a>
	</div>

	<% if (request.getAttribute("error") != null) { %>
			
		<p style="font-family: monospace; white-space: pre; color: red;"><%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %></p>

	<% } %>

	<% String sessionInput = (String) request.getSession().getAttribute("sessionInput"); %>
	<% String sessionSecretToken = (String) request.getSession().getAttribute("sessionSecretToken"); %>
	<% CloudNumber sessionCloudNumber = (CloudNumber) request.getSession().getAttribute("sessionCloudNumber"); %>
	<% String sessionXdiEndpointUri = (String) request.getSession().getAttribute("sessionXdiEndpointUri"); %>

	<% if (sessionInput == null) { %>

	<form action="XDIOperator" method="post">

		<table cellpadding="3">

		<% String input = (String) request.getAttribute("input"); if (cloudName == null) input = ""; %>
		<% String secretToken = (String) request.getAttribute("secretToken"); if (secretToken == null) secretToken = ""; %>
		<% String endpoint = (String) request.getAttribute("endpoint"); if (endpoint == null) endpoint = ""; %>

		<tr>
		<td>Cloud Name / Cloud Number: </td>
		<td><input type="text" name="input" size="80" value="<%= input %>"></td>
		</tr>

		<tr>
		<td>Secret Token: </td>
		<td><input type="password" name="secretToken" size="80" value="<%= secretToken %>"></td>
		</tr>

		<tr>
		<td>Discover from registry service: </td>
		<td><input type="text" name="endpoint" size="80" value="<%= endpoint %>"></td>
		</tr>

		<tr>
		<td>&nbsp;</td>
		<td><span style="font-size: .8em;">Use <span style="font-size: 1em; font-weight: bold;"><%= xdi2.discovery.XDIDiscoveryClient.NEUSTAR_PROD_DISCOVERY_XDI_CLIENT.getEndpointUri() %></span> for PROD. Use <span style="font-size: 1em; font-weight: bold;"><%= xdi2.discovery.XDIDiscoveryClient.NEUSTAR_OTE_DISCOVERY_XDI_CLIENT.getEndpointUri() %></span> for OTE.</span></td>
		</tr>

		</table>

		<input type="hidden" name="cmd" value="login">
		<input type="submit" value="Login!">
		&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDIDiscovererHelp.jsp">What can I do here?</a>

	</form>

	<% } else if (sessionInput != null) { %>

	<form action="XDIOperator" method="post">

		<table cellpadding="3">

		<tr>
		<td>Cloud Number: </td>
		<td><%= sessionCloudNumber %></td>
		</tr>

		<tr>
		<td>Endpoint: </td>
		<td><%= sessionXdiEndpointUri %></td>
		</tr>

		<tr>
		<td>&nbsp;</td>
		<td><span style="font-size: .8em;">Use <span style="font-size: 1em; font-weight: bold;"><%= xdi2.discovery.XDIDiscoveryClient.NEUSTAR_PROD_DISCOVERY_XDI_CLIENT.getEndpointUri() %></span> for PROD. Use <span style="font-size: 1em; font-weight: bold;"><%= xdi2.discovery.XDIDiscoveryClient.NEUSTAR_OTE_DISCOVERY_XDI_CLIENT.getEndpointUri() %></span> for OTE.</span></td>
		</tr>

		</table>

		<input type="hidden" name="cmd" value="logout">
		<input type="submit" value="Logout!">
		&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDIDiscovererHelp.jsp">What can I do here?</a>

	</form>

	<div class="tabber">

    <div class="tabbertab">

		<h2>Plain Message</h2>
	
		<form action="XDIOperator" method="post">
	
			<input type="hidden" name="cmd" value="buildPlain">
			<input type="submit" value="Build!">
	
		</form>

	</div>

    <div class="tabbertab">

		<h2>Root Link Contract</h2>
	
		<form action="XDIOperator" method="post">
	
			<input type="hidden" name="cmd" value="buildRootLinkContract">
			<input type="submit" value="Build!">
	
		</form>

	</div>

    <div class="tabbertab">

		<h2>Generic Link Contract</h2>
	
		<table cellpadding="3">

		<tr>
		<td>Requesting Authority: </td>
		<td><input type="text" name="requestingAuthority" size="80"></td>
		</tr>

		</table>
	
		<form action="XDIOperator" method="post">
	
			<input type="hidden" name="cmd" value="buildGenericLinkContract">
			<input type="submit" value="Build!">
	
		</form>

	</div>

    <div class="tabbertab">

		<h2>Generic Link Contract</h2>
	
		<form action="XDIOperator" method="post">
	
			<input type="hidden" name="cmd" value="buildGenericLinkContract">
			<input type="submit" value="Build!">
	
		</form>

	</div>
	
	</div>

	<form action="XDIOperator" method="post">

		<textarea name="input" style="width: 100%" rows="12"><%= request.getAttribute("input") != null ? request.getAttribute("input") : "" %></textarea><br>

		<% String resultFormat = (String) request.getAttribute("resultFormat"); if (resultFormat == null) resultFormat = ""; %>
		<% String writeImplied = (String) request.getAttribute("writeImplied"); if (writeImplied == null) writeImplied = ""; %>
		<% String writeOrdered = (String) request.getAttribute("writeOrdered"); if (writeOrdered == null) writeOrdered = ""; %>
		<% String writeInner = (String) request.getAttribute("writeInner"); if (writeInner == null) writeInner = ""; %>
		<% String writePretty = (String) request.getAttribute("writePretty"); if (writePretty == null) writePretty = ""; %>

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

		<input type="hidden" name="cmd" value="message">
		<input type="submit" value="Go!">

	</form>

	<% } %>

	<% if (request.getAttribute("stats") != null) { %>
		<p>
		<%= request.getAttribute("stats") %>

		<% if (request.getAttribute("output") != null) { %>
			Copy&amp;Paste: <textarea style="width: 100px; height: 1.2em; overflow: hidden"><%= request.getAttribute("output") %></textarea>
		<% } %>
		</p>
	<% } %>

	<% if (request.getAttribute("output") != null) { %>
		<div class="result"><pre><%= request.getAttribute("output") %></pre></div><br>
	<% } %>

	</div>
</body>
</html>
