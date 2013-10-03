<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XDI Signer - Help</title>
<script type="text/javascript" src="tabber.js"></script>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body style="background-image: url('images/back.png'); background-repeat: repeat-y; margin-left: 60px;">

	<div class="header">
	<img src="images/logo64.png" align="middle">&nbsp;&nbsp;&nbsp;<span id="appname">XDI Signer</span>
	&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDISigner">Back...</a>
	</div>

	<div class="tabber">

    <div class="tabbertab">

	<h2>Information</h2>

	<p>This is an experimental tool for signing and validating XDI (sub-)graphs. This functionality
	is expected to be primarily used in XDI Messaging.</p>

	<p>The following information is required in this tool:</p>

	<ul>
	<li>An input graph.</li>
	<li>A cryptographic key.</li>
	<li>The address of the sub-graph that will be signed / validated.</li>
	</ul>

	<p>Several examples are provided with meaningful combinations of the above.</p>

	<p>If you get the error "Unknown serialization format", use the XDI Validator to debug your graph.</p>

	</div>

    <div class="tabbertab">

	<h2>Keys</h2>

	<p>All signing and validating operations require cryptographic keys:</p>
	
	<ul>
    <li>Create RSA Signature: An RSA private key</li>
    <li>Validate RSA Signature: An RSA public key</li>
    <li>Create AES HMAC: An AES symmetric key</li>
    <li>Validate AES HMAC: An AES symmetric key</li>
	</ul>
	
	</div>

	</div>

</body>
</html>
