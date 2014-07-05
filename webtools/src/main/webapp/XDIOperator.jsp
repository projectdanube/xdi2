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
	<% CloudNumber sessionCloudNumber = (CloudNumber) request.getSession().getAttribute("sessionCloudNumber"); %>
	<% String sessionXdiEndpointUri = (String) request.getSession().getAttribute("sessionXdiEndpointUri"); %>

	<% if (sessionInput == null) { %>

	<form action="XDIOperator" method="post" accept-charset="UTF-8">

		<% String input = (String) request.getAttribute("input"); if (input == null) input = ""; %>
		<% String secretToken = (String) request.getAttribute("secretToken"); if (secretToken == null) secretToken = ""; %>
		<% String endpoint = (String) request.getAttribute("endpoint"); if (endpoint == null) endpoint = "PROD"; %>

		<table cellpadding="3">
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
		<td><input type="radio" name="endpoint" value="PROD" <%= endpoint.equals("PROD") ? "checked" : "" %>> PROD &nbsp;
		<input type="radio" name="endpoint" value="OTE" <%= endpoint.equals("OTE") ? "checked" : "" %>> OTE &nbsp;
		<input type="hidden" name="cmd" value="login">
		<input type="submit" value="Authenticate">
		&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDIOperatorHelp.jsp">What can I do here?</a></td>
		</tr>
		</table>


		<hr noshade>

	</form>

	<% } else if (sessionInput != null) { %>

	<form action="XDIOperator" method="post" accept-charset="UTF-8">

		Cloud Number: <span title="<%= sessionXdiEndpointUri %>" style="font-weight: bold;"><%= sessionCloudNumber %></span>&nbsp;&nbsp;

		<input type="hidden" name="cmd" value="logout">
		<input type="submit" value="Reset">
		&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDIOperatorHelp.jsp">What can I do here?</a>

	</form>

	<div class="tabber" style="margin-top: 20px; margin-bottom: 20px;">

	<% String tab = (String) request.getAttribute("tab"); %>

    <div class="tabbertab <%= "0".equals(tab) ? "tabbertabdefault" : "" %>">

		<h2>Plain Message</h2>
	
		<form action="XDIOperator" method="post" accept-charset="UTF-8">
	
			<table cellpadding="3">
	
			<tr>
			<td><input type="hidden" name="cmd" value="buildPlain">
			<input type="submit" name="submit" value="Plain XDI get"></td>
			</tr>
	
			</table>
	
		</form>

		<hr>
	
		<form action="XDIOperator" method="post" accept-charset="UTF-8">
	
			<table cellpadding="3">
	
			<tr>
			<td><input type="hidden" name="cmd" value="buildPlain">
			<input type="submit" name="submit" value="Plain XDI set"></td>
			</tr>
	
			</table>
	
		</form>

		<hr>
	
		<form action="XDIOperator" method="post" accept-charset="UTF-8">
	
			<table cellpadding="3">
	
			<tr>
			<td><input type="hidden" name="cmd" value="buildPlain">
			<input type="submit" name="submit" value="Plain XDI del"></td>
			</tr>
	
			</table>
	
		</form>

	</div>

    <div class="tabbertab <%= "1".equals(tab) ? "tabbertabdefault" : "" %>">

		<h2>Cloud Names</h2>

		<form action="XDIOperator" method="post" accept-charset="UTF-8">
	
			<table cellpadding="3">
	
			<tr>
			<td><input type="hidden" name="cmd" value="buildCloudNames">
			<input type="submit" name="submit" value="Get cloud names"></td>
			</tr>
	
			</table>
	
		</form>

		<hr>
	
		<form action="XDIOperator" method="post" accept-charset="UTF-8">
	
			<table cellpadding="3">
	
			<tr>
			<td><input type="hidden" name="cmd" value="buildCloudNames">
			<input type="submit" name="submit" value="Set cloud name"></td>
			<td>Cloud name: <input type="text" name="cloudName" size="40"></td>
			</tr>
	
			</table>
	
		</form>

		<hr>
	
		<form action="XDIOperator" method="post" accept-charset="UTF-8">
	
			<table cellpadding="3">
	
			<tr>
			<td><input type="hidden" name="cmd" value="buildCloudNames">
			<input type="submit" name="submit" value="Del cloud name"></td>
			<td>Cloud name: <input type="text" name="cloudName" size="40"></td>
			</tr>
	
			</table>
	
		</form>

	</div>

    <div class="tabbertab <%= "2".equals(tab) ? "tabbertabdefault" : "" %>">

		<h2>Root Link Contract</h2>
	
		<form action="XDIOperator" method="post" accept-charset="UTF-8">
	
			<table cellpadding="3">
	
			<tr>
			<td><input type="hidden" name="cmd" value="buildRootLinkContract">
			<input type="submit" name="submit" value="Get root link contract"></td>
			</tr>
	
			</table>
	
		</form>

		<hr>
	
		<form action="XDIOperator" method="post" accept-charset="UTF-8">
	
			<table cellpadding="3">
	
			<tr>
			<td><input type="hidden" name="cmd" value="buildRootLinkContract">
			<input type="submit" name="submit" value="Set root link contract"></td>
			</tr>
	
			</table>
	
		</form>

		<hr>
	
		<form action="XDIOperator" method="post" accept-charset="UTF-8">
	
			<table cellpadding="3">
	
			<tr>
			<td><input type="hidden" name="cmd" value="buildRootLinkContract">
			<input type="submit" name="submit" value="Del root link contract"></td>
			</tr>
	
			</table>
	
		</form>

	</div>

    <div class="tabbertab <%= "3".equals(tab) ? "tabbertabdefault" : "" %>">

		<h2>Public Link Contract</h2>
	
		<form action="XDIOperator" method="post" accept-charset="UTF-8">
	
			<table cellpadding="3">
	
			<tr>
			<td><input type="hidden" name="cmd" value="buildPublicLinkContract">
			<input type="submit" name="submit" value="Get public link contract"></td>
			</tr>
	
			</table>
	
		</form>

		<hr>
	
		<form action="XDIOperator" method="post" accept-charset="UTF-8">
	
			<table cellpadding="3">
	
			<tr>
			<td><input type="hidden" name="cmd" value="buildPublicLinkContract">
			<input type="submit" name="submit" value="Set public link contract"></td>
			</tr>
	
			</table>
	
		</form>

		<hr>
	
		<form action="XDIOperator" method="post" accept-charset="UTF-8">
	
			<table cellpadding="3">
	
			<tr>
			<td><input type="hidden" name="cmd" value="buildPublicLinkContract">
			<input type="submit" name="submit" value="Del public link contract"></td>
			</tr>
	
			</table>
	
		</form>

	</div>

    <div class="tabbertab <%= "4".equals(tab) ? "tabbertabdefault" : "" %>">

		<h2>Generic Link Contract</h2>
	
		<form action="XDIOperator" method="post" accept-charset="UTF-8">
	
			<table cellpadding="3">
	
			<tr>
			<td><input type="hidden" name="cmd" value="buildGenericLinkContract">
			<input type="submit" name="submit" value="Get generic link contract"></td>
			<td>Requesting Authority: <input type="text" name="requestingAuthority" size="40"></td>
			</tr>
	
			</table>
	
		</form>

		<hr>
	
		<form action="XDIOperator" method="post" accept-charset="UTF-8">
	
			<table cellpadding="3">
	
			<tr>
			<td><input type="hidden" name="cmd" value="buildGenericLinkContract">
			<input type="submit" name="submit" value="Set generic link contract"></td>
			<td>Requesting Authority: <input type="text" name="requestingAuthority" size="40"></td>
			</tr>
	
			</table>

		</form>

		<hr>
	
		<form action="XDIOperator" method="post" accept-charset="UTF-8">
	
			<table cellpadding="3">
	
			<tr>
			<td><input type="hidden" name="cmd" value="buildGenericLinkContract">
			<input type="submit" name="submit" value="Del generic link contract"></td>
			<td>Requesting Authority: <input type="text" name="requestingAuthority" size="40"></td>
			</tr>
	
			</table>
	
		</form>

	</div>

    <div class="tabbertab <%= "5".equals(tab) ? "tabbertabdefault" : "" %>">

		<h2>Key Pairs</h2>
	
		<form action="XDIOperator" method="post" accept-charset="UTF-8">
	
			<table cellpadding="3">
	
			<tr>
			<td><input type="hidden" name="cmd" value="buildKeyPairs">
			<input type="submit" name="submit" value="Get key pairs"></td>
			</tr>
	
			</table>
	
		</form>
	
		<hr>
	
		<form action="XDIOperator" method="post" accept-charset="UTF-8">
	
			<table cellpadding="3">
	
			<tr>
			<td><input type="hidden" name="cmd" value="buildKeyPairs">
			<input type="submit" name="submit" value="Generate key pairs"></td>
			</tr>
	
			</table>
	
		</form>
	
		<hr>
	
		<form action="XDIOperator" method="post" accept-charset="UTF-8">
	
			<table cellpadding="3">
	
			<tr>
			<td><input type="hidden" name="cmd" value="buildKeyPairs">
			<input type="submit" name="submit" value="Del key pairs"></td>
			</tr>
	
			</table>
	
		</form>

	</div>
	
	</div>

	<form action="XDIOperator" method="post" accept-charset="UTF-8">

		<textarea name="message" style="width: 100%; white-space: nowrap; overflow: auto;" rows="12"><%= request.getAttribute("message") != null ? request.getAttribute("message") : "" %></textarea><br>

		<% String resultFormat = (String) request.getAttribute("resultFormat"); if (resultFormat == null) resultFormat = ""; %>
		<% String writeImplied = (String) request.getAttribute("writeImplied"); if (writeImplied == null) writeImplied = ""; %>
		<% String writeOrdered = (String) request.getAttribute("writeOrdered"); if (writeOrdered == null) writeOrdered = ""; %>
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
		<% if (request.getAttribute("outputId") != null && ! "".equals(request.getAttribute("output")) && ! "".equals(request.getAttribute("outputId"))) { %>
			<a class="graphit" target="_blank" href="http://neustar.github.io/xdi-grapheditor/xdi-grapheditor/public_html/index.html?input=<%= request.getRequestURL().toString().replaceFirst("/[^/]+$", "/XDIOutput?outputId=" + request.getAttribute("outputId")) %>">Graph It!</a>
		<% } %>
		<div class="result"><pre><%= request.getAttribute("output") %></pre></div><br>
	<% } %>

	</div>
</body>
</html>
