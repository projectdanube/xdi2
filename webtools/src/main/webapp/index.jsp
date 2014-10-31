<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="xdi2.core.properties.XDI2Properties" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XDI2 web tools</title>
<script type="text/javascript" src="tabber.js"></script>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
<link rel="shortcut icon" href="favicon.ico" />
<style type="text/css">
#main { background-color: rgba(248,248,248,0.7); margin-top: 0px; margin-left: 110px; margin-right: 110px; padding-left: 40px; padding-right: 40px; }
div.line { margin-left: -150px; }
</style>
<script src="jquery-1.11.1.min.js"></script>
<script>
$(document).ready(function(){$('a[href^="#"]').on('click',function (e) {
e.preventDefault();var target = this.hash; $target = $(target);
$('html, body').stop().animate({'scrollTop': $target.offset().top}, 300, 'swing', function () {
window.location.hash = target;
});});});
</script>
</head>
<body>
	<div id="imgtop"><a href="#slide1"><img id="imgtopleft" src="images/xdi2-topleft.png"></a><img id="imgtopright" src="images/xdi2-topright.png"></div>
	<div id="main">
	<div class="header">
	<span id="appname"><img src="images/app20b.png"> XDI2</span>
	&nbsp;&nbsp;&nbsp;&nbsp;
	<strong>See <a href="http://github.com/projectdanube/xdi2">http://github.com/projectdanube/xdi2</a> for the code and documentation.</strong><br>
	This is version <%= XDI2Properties.properties.getProperty("project.version") %> <%= XDI2Properties.properties.getProperty("project.build.timestamp") %>, Git commit <%= XDI2Properties.properties.getProperty("git.commit.id").substring(0,6) %> <%= XDI2Properties.properties.getProperty("git.commit.time") %>.
	</div>
	
	<div id="nav">
		<center>
		<a href="#slide1"><img src="images/icon1.png"><br>Tools</a><br>
		<a href="#slide2"><img src="images/icon2.png"><br>Code</a><br>
		<a href="#slide3"><img src="images/icon3.png"><br>Sandbox</a><br>
		<a href="#slide4"><img src="images/icon4.png"><br>Connections</a><br>
		<a href="#slide5"><img src="images/icon5.png"><br>Examples</a>
		</center>
	</div>
	
	<div class="slide" id="slide1">

		<h2>XDI Tools</h2>
	
		<table cellpadding="5">
		<tr>
		<td valign="middle"><img src="images/app20.png"></td><td style="width: 180px;" valign="middle"><a href="XDIParser">XDI Parser</a></td>
		<td valign="middle"><img src="images/app20.png"></td><td style="width: 180px;" valign="middle"><a href="XDIValidator">XDI Validator</a></td>
		<td valign="middle"><img src="images/app20.png"></td><td style="width: 180px;" valign="middle"><a href="XDIConverter">XDI Converter</a></td>
		</tr><tr>
		<td valign="middle"><img src="images/app20.png"></td><td style="width: 180px;" valign="middle"><a href="XDIMessenger">XDI Messenger</a></td>
		<td valign="middle"><img src="images/app20.png"></td><td style="width: 180px;" valign="middle"><a href="XDILocalMessenger">XDI Local Messenger</a></td>
		<td valign="middle"><img src="images/app20.png"></td><td style="width: 180px;" valign="middle"><a href="XDISigner">XDI Signer</a></td>
		</tr><tr>
		<td valign="middle"><img src="images/app20.png"></td><td style="width: 180px;" valign="middle"><a href="XDIDiscoverer">XDI Discoverer</a></td>
		<td valign="middle"><img src="images/app20.png"></td><td style="width: 180px;" valign="middle"><a href="XDIOperator">XDI Operator</a></td>
		<td valign="middle"><img src="images/app20.png"></td><td style="width: 180px;" valign="middle"><a href="XDIGrapher">XDI Grapher</a></td>
		</tr>
		</table>

		<p>Local XDI server instance: <a href="<%= request.getRequestURL().substring(0, request.getRequestURL().toString().lastIndexOf('/')) %>/xdi/"><%= request.getRequestURL().substring(0, request.getRequestURL().toString().lastIndexOf('/')) %>/xdi/</a></p>

	</div><div class="slide" id="slide2">

		<h2>XDI Code</h2>

		<p><strong>See <a href="http://github.com/projectdanube/xdi2">http://github.com/projectdanube/xdi2</a> for the code and documentation.</strong></p>

	</div><div class="slide" id="slide3">

		<h2>XDI Sandbox</h2>

		<p>Create XDI clouds for developers:</p>
		
		<ul>
		<li><a href="https://ote.danubeclouds.com/DevAccount.html">https://ote.danubeclouds.com/DevAccount.html</a></li>
		</ul>
		
		<p>XDI Cloud Manager:</p>
		
		<ul>
		<li><a href="https://cloud-manager.projectdanube.org/">https://cloud-manager.projectdanube.org/</a></li>
		</ul>

	</div><div class="slide" id="slide4">

		<h2>XDI Connections</h2>

		<p>Example Business Clouds with XDI Connection Request:</p>
		
		<ul>
		<li><a href="https://acmenews.projectdanube.org/">https://acmenews.projectdanube.org/</a></li>
		<li><a href="https://acmepizza.projectdanube.org/">https://acmepizza.projectdanube.org/</a></li>
		</ul>
		
		<p>Example Personal Cloud Card with XDI Connection Invitation:</p>
		
		<ul>
		<li><a href="https://cloudcards.projectdanube.org/%5B=%5D!:uuid:50f47072-e6a8-4c5c-ac18-499035ab46fe%5B$card%5D!:uuid:710ed911-c532-4cf8-8556-b7c81dd9b757">https://cloudcards.projectdanube.org/%5B=%5D!:uuid:50f47072-e6a8-4c5c-ac18-499035ab46fe%5B$card%5D!:uuid:710ed911-c532-4cf8-8556-b7c81dd9b757</a></li>
		</ul>
		
		<p>Connect Service and Authorization Service (don't access directly):</p>
		
		<ul>
		<li><a href="https://connect-service.projectdanube.org/">https://connect-service.projectdanube.org/</a></li>
		<li><a href="https://auth-service.danubeclouds.com/">https://auth-service.danubeclouds.com/</a></li>
		</ul>

	</div><div class="slide" id="slide5">

		<h2>XDI Examples</h2>
		
		<p>XDI Example Code:</p>

		<ul>
		<li><a href="https://github.com/projectdanube/xdi2-example-core">https://github.com/projectdanube/xdi2-example-core</a></li>
		<li><a href="https://github.com/projectdanube/xdi2-example-client">https://github.com/projectdanube/xdi2-example-client</a></li>
		<li><a href="https://github.com/projectdanube/xdi2-example-messaging">https://github.com/projectdanube/xdi2-example-messaging</a></li>
		<li><a href="https://github.com/projectdanube/xdi2-example-server">https://github.com/projectdanube/xdi2-example-server</a></li>
		</ul>

	</div>

	<div class="footer">
	<p>
	XDI2 (“XDI Two”) is a general-purpose, lightweight and modular Java implementation of XDI specifications.<br>
	<b>Community:</b>
	<a href="http://github.com/projectdanube/xdi2">Github</a>
	<a href="http://groups.google.com/group/xdi2">Google Group</a>
	<a href="https://github.com/projectdanube/xdi2/wiki/XDI2-Weekly-Call">Weekly Call</a>
	<a href="irc://irc.freenode.net:6667/xdi">IRC</a>
	<a href="http://en.wikipedia.org/wiki/XDI">Wikipedia</a>
	<a href="http://www.oasis-open.org/committees/xdi/">OASIS XDI TC</a>
	<a href="http://wiki.oasis-open.org/xdi/">OASIS XDI TC Wiki</a>
	</p>
	</div>
	
	</div>
</body>
</html>
