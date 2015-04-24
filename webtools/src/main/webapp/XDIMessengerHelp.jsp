<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="xdi2.core.properties.XDI2Properties" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XDI Messenger - Help</title>
<script type="text/javascript" src="tabber.js"></script>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body>
	<div id="imgtop"><img id="imgtopleft" src="images/xdi2-topleft.png"><a href="http://projectdanube.org/"><img id="imgtopright" src="images/xdi2-topright.png"></a></div>
	<div id="main">
	<div class="header">
	<span id="appname"><img src="images/app20b.png"> XDI Messenger Help</span>
	&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDIMessenger">&gt;&gt;&gt; Back...</a><br>
	This is version <%= XDI2Properties.properties.getProperty("project.version") %> <%= XDI2Properties.properties.getProperty("project.build.timestamp") %>, Git commit <%= XDI2Properties.properties.getProperty("git.commit.id").substring(0,6) %> <%= XDI2Properties.properties.getProperty("git.commit.time") %>.
	</div>

	<div class="tabber">

    <div class="tabbertab">

	<h2>Information</h2>

	Here you can send XDI messages to XDI endpoints and view results.

	<ul>
	<li>Messages can be entered in any format.</li>
	<li>Messages are sent to the endpoint in JSON format by default.</li>
	<li>Message results are requested from the endpoint in JSON format.</li>
	<li>Only $get operations will result in a direct response.</li>
	<li>If you get the error "Unknown serialization format", use the XDI Validator to debug your message.</li>
	</ul>

	Graphs can also be accessed with HTTP GET requests containing an XDI address. If you enter the
	endpoint URI in your browser, you will get the whole graph. If you enter a subject XRI after the
	endpoint URI, you will get only the statements rooted in that subject, etc.

	</div>

    <div class="tabbertab">

	<h2>Example XDI endpoints</h2>

	<p>The following XDI endpoints are deployed together with the XDI Messenger for testing purposes:</p>

	<%	String base = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/")); %>
	
	<p style="font-weight: bold"><a target="_blank" href="<%= base %>/xdi/mem-graph/"><%= base %>/xdi/mem-graph/</a></p>
	<p>This is an XDI endpoint backed by an in-memory store.<br>
	<b>Versioning:</b> disabled. <b>Link contracts:</b> disabled.</p>
	
	<p style="font-weight: bold"><a target="_blank" href="<%= base %>/xdi/bdb-graph/"><%= base %>/xdi/bdb-graph/</a></p>
	<p>This is an XDI endpoint backed by a Berkely DB store.<br>
	<b>Versioning:</b> disabled. <b>Link contracts:</b> disabled.</p>
	
	<p style="font-weight: bold"><a target="_blank" href="<%= base %>/xdi/file-graph/"><%= base %>/xdi/file-graph/</a></p>
	<p>This is an XDI endpoint backed by an XDI/JSON text file.<br>
	<b>Versioning:</b> disabled. <b>Link contracts:</b> disabled.</p>

	</div>

    <div class="tabbertab">

	<h2>External Call</h2>

	<p>The fields of the XDI Messenger tool can be "pre-filled" by passing them as URI parameters as follows:</p>

	<ul>
		<li>"recipient": The XDI identifier that is the recipient of the message. If this is a string that starts with "ote:" or "prod:", then the XDI identifier will be discovered from a suitable XDI discovery service.</li>
		<li>"sender": The XDI identifier that is the sender of the message. If this is a string that starts with "ote:" or "prod:", then the XDI identifier will be discovered from a suitable XDI discovery service. Default: "$anon".</li>
		<li>"linkContract": The address of the link contract in the target graph. Default: "$do".</li>
		<li>"operation": The operation in the message. Default: "$get".</li>
		<li>"target": The target of the operation. Default: "".</li>
		<li>"secretToken": An optional secret token for the message.</li>
		<li>"signature": An optional signature for the message.</li>
		<li>"endpoint": The XDI endpoint URI for the message. If this is a string that starts with "ote:" or "prod:", then the XDI endpoint URI will be discovered from a suitable XDI discovery service.</li>
	</ul>

	</div>

	</div>

</body>
</html>
