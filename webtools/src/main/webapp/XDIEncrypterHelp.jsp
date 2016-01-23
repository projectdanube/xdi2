<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="xdi2.core.properties.XDI2Properties" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XDI Encrypter - Help</title>
<script type="text/javascript" src="tabber.js"></script>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body>
	<div id="imgtop"><img id="imgtopleft" src="images/xdi2-topleft.png"><a href="http://projectdanube.org/"><img id="imgtopright" src="images/xdi2-topright.png"></a></div>
	<div id="main">
	<div class="header">
	<span id="appname"><img src="images/app20b.png"> XDI Encrypter Help</span>
	&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDIEncrypter">&gt;&gt;&gt; Back...</a><br>
	This is version <%= XDI2Properties.properties.getProperty("project.version") %> <%= XDI2Properties.properties.getProperty("project.build.timestamp") %>, Git commit <%= XDI2Properties.properties.getProperty("git.commit.id").substring(0,6) %> <%= XDI2Properties.properties.getProperty("git.commit.time") %>.
	</div>

	<div class="tabber">

    <div class="tabbertab">

	<h2>Information</h2>

	<p>This is an experimental tool for encrypting and decrypting XDI (sub-)graphs. This functionality
	is expected to be primarily used in XDI Messaging.</p>

	<p>The following information is required in this tool:</p>

	<ul>
	<li>An input graph.</li>
	<li>A cryptographic key.</li>
	<li>The address of the sub-graph that will be encrypted / decrypted.</li>
	</ul>

	<p>Several examples are provided with meaningful combinations of the above.</p>

	<p>If you get the error "Unknown serialization format", use the XDI Validator to debug your graph.</p>

	</div>

    <div class="tabbertab">

	<h2>Keys</h2>

	<p>All encryption and decryption operations require cryptographic keys:</p>
	
	<ul>
    <li>Encrypt RSA: An RSA public key</li>
    <li>Decrypt RSA: An RSA private key</li>
    <li>Encrypt AES: An AES symmetric key</li>
    <li>Decrypt AES: An AES symmetric key</li>
	</ul>
	
	</div>

	</div>

</body>
</html>
