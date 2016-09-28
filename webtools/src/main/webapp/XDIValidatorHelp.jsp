<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="xdi2.core.properties.XDI2Properties" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XDI Validator - Help</title>
<script type="text/javascript" src="tabber.js"></script>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body>
	<div id="imgtop"><img id="imgtopleft" src="images/xdi2-topleft.png"><img id="imgtopright" src="images/xdi2-topright.png"></div>
	<div id="main">
	<div class="header">
	<span id="appname"><img src="images/app20b.png"> XDI Validator Help</span>
	&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDIValidator">&gt;&gt;&gt; Back...</a><br>
	This is version <%= XDI2Properties.properties.getProperty("project.version") %> <%= XDI2Properties.properties.getProperty("project.build.timestamp") %>, Git commit <%= XDI2Properties.properties.getProperty("git.commit.id").substring(0,6) %> <%= XDI2Properties.properties.getProperty("git.commit.time") %>.
	</div>

	<div class="tabber">

    <div class="tabbertab">

	<h2>Information</h2>

	<p>This tool can validate XDI documents in different serialization formats.</p>

	<p>If the document is valid, some basic statistics about it will be displayed.</p>

	<p>If the document is not valid, the intended format should be selected manually (instead of using auto-detect), since this will produce a more detailed error message.</p> 

	<p>Several example documents are available that can be loaded and validated.</p>

	</div>

    <div class="tabbertab">

	<h2>Formats</h2>

	<p><b>JXD</b><br>
	JSON serialization of an XDI graph (default format).</p>

	<p><b>XDI DISPLAY</b><br>
	An enumeration of XDI statements in the graph.</p>

	</div>
	
	</div>

</body>
</html>
