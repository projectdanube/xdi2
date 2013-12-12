<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XDI Discoverer - Help</title>
<script type="text/javascript" src="tabber.js"></script>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body style="background-image: url('images/back.png'); background-repeat: repeat-y; margin-left: 60px;">

	<div class="header">
	<img src="images/logo64.png" align="middle">&nbsp;&nbsp;&nbsp;<span id="appname">XDI Discoverer</span>
	&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDIDiscoverer">Back...</a>
	</div>

	<div class="tabber">

    <div class="tabbertab">

	<h2>Overview</h2>

	<p>This tool allows you to perform XDI discovery on identifiers such as Cloud Names, Cloud Numbers, and discovery keys.</p>
	
	<p>The first (required) step is to send an XDI discovery request to a registry service.</p>
	
	<p>The second (optional) step is to send an XDI discovery request to the XDI authority associated with the identifier.</p> 

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

	<p>The fields of the XDI Discoverer tool can be "pre-filled" by passing them as URI parameters as follows:</p>

	<ul>
		<li>"input": The XDI identifier on which to perform discovery.</li>
		<li>"endpoint": The XDI endpoint URI of registry service.</li>
	</ul>

	</div>

	</div>

</body>
</html>
