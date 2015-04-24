<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="xdi2.core.properties.XDI2Properties" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XDI Operator - Help</title>
<script type="text/javascript" src="tabber.js"></script>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body>
	<div id="imgtop"><img id="imgtopleft" src="images/xdi2-topleft.png"><a href="http://projectdanube.org/"><img id="imgtopright" src="images/xdi2-topright.png"></a></div>
	<div id="main">
	<div class="header">
	<span id="appname"><img src="images/app20b.png"> XDI Operator Help</span>
	&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDIOperator">&gt;&gt;&gt; Back...</a><br>
	This is version <%= XDI2Properties.properties.getProperty("project.version") %> <%= XDI2Properties.properties.getProperty("project.build.timestamp") %>, Git commit <%= XDI2Properties.properties.getProperty("git.commit.id").substring(0,6) %> <%= XDI2Properties.properties.getProperty("git.commit.time") %>.
	</div>

	<div class="tabber">

    <div class="tabbertab">

	<h2>Overview</h2>

	<p>This tool allows you to authenticate to an XDI endpoint using a cloud name and a secret token, and to perform various common operations on that XDI endpoint.</p>
	
	<p>The XDI Operator serves similar purposes as the XDI Discoverer and XDI Messenger tools, but on a higher level.</p>

	</div>

    <div class="tabbertab">

	<h2>Discoverable information</h2>

	<p>The following information can be discovered from either the registry service or the XDI authority:</p>

	<ul>
	<li>The Cloud Number.</li>
	<li>The XDI endpoint URI of the XDI authority.</li>
	<li>One or more public keys associated with the XDI authority.</li>
	<li>A list of additional services associated with the XDI authority.</li>
	</ul>

	</div>

    <div class="tabbertab">

	<h2>External Call</h2>

	<p>The fields of the XDI Operator tool can be "pre-filled" by passing them as URI parameters as follows:</p>

	<ul>
		<li>"input": The XDI identifier on which to perform discovery.</li>
		<li>"endpoint": The XDI endpoint URI of registry service.</li>
	</ul>

	</div>

	</div>

</body>
</html>
