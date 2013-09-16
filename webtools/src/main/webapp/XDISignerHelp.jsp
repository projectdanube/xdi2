<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XDI Messenger - Help</title>
<script type="text/javascript" src="tabber.js"></script>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body style="background-image: url('images/back.png'); background-repeat: repeat-y; margin-left: 60px;">

	<div class="header">
	<img src="images/logo64.png" align="middle">&nbsp;&nbsp;&nbsp;<span id="appname">XDI Messenger</span>
	&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDILocalMessenger">Back...</a>
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

	<p>The following XDI endpoints are deployed together with the XDI Messenger for testing purposes:

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

	</div>

</body>
</html>
