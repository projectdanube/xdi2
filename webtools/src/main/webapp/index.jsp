<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="xdi2.core.properties.XDI2Properties" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XDI2 Web Tools</title>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
<link rel="shortcut icon" href="favicon.ico" />
<style type="text/css">
#main { background-color: rgba(248,248,248,0.7); margin-left: 110px; margin-right: 110px; padding-left: 40px; padding-right: 40px; }
div.line { margin-left: -150px; }
</style>

</head>

<body>

	<div id="imgtop"><img id="imgtopleft" src="images/xdi2-topleft.png"><a href="http://projectdanube.org/"><img id="imgtopright" src="images/xdi2-topright.png"></a></div>
	<div id="main">
	<div class="header">
	<span id="appname"><img src="images/app20b.png"> XDI2</span>
	&nbsp;&nbsp;&nbsp;&nbsp;
	<strong>See <a href="https://xdi2.org/">https://xdi2.org/</a> for information, and Github <a href="http://github.com/projectdanube/xdi2">projectdanube/xdi2</a> for code.</strong><br>
	This is version <%= XDI2Properties.properties.getProperty("project.version") %> <%= XDI2Properties.properties.getProperty("project.build.timestamp") %>, Git commit <%= XDI2Properties.properties.getProperty("git.commit.id").substring(0,6) %> <%= XDI2Properties.properties.getProperty("git.commit.time") %>.
	</div>
	
	<p>XDI2 (“XDI Two”) is a general-purpose, lightweight and modular Java implementation of XDI specifications.<br>
	This is an example deployment of an XDI2 server with a set of web-based XDI tools.</p>

	<hr noshade>

	Click here for the local XDI server admin interface: <a href="<%= request.getRequestURL().substring(0, request.getRequestURL().toString().lastIndexOf('/')) %>/xdi/"><%= request.getRequestURL().substring(0, request.getRequestURL().toString().lastIndexOf('/')) %>/xdi/</a>
	
	<hr noshade>

	<p class="subheader">XDI Tools</p>

	<table cellpadding="5">
	<tr>
	<td valign="middle"><img src="images/app20.png"></td><td style="width: 195px;" valign="middle"><a href="XDIParser">XDI Parser</a></td>
	<td valign="middle"><img src="images/app20.png"></td><td style="width: 195px;" valign="middle"><a href="XDIValidator">XDI Validator</a></td>
	<td valign="middle"><img src="images/app20.png"></td><td style="width: 195px;" valign="middle"><a href="XDIConverter">XDI Converter</a></td>
	</tr><tr>
	<td valign="middle"><img src="images/app20.png"></td><td style="width: 195px;" valign="middle"><a href="XDIMessenger">XDI Messenger</a></td>
	<td valign="middle"><img src="images/app20.png"></td><td style="width: 195px;" valign="middle"><a href="XDILocalMessenger">XDI Local Messenger</a></td>
	<td valign="middle"><img src="images/app20.png"></td><td style="width: 195px;" valign="middle"><a href="XDIPeerMessenger">XDI Peer Messenger</a></td>
	</tr><tr>
	<td valign="middle"><img src="images/app20.png"></td><td style="width: 195px;" valign="middle"><a href="XDIDiscoverer">XDI Discoverer</a></td>
	<td valign="middle"><img src="images/app20.png"></td><td style="width: 195px;" valign="middle"><a href="XDIEncrypter">XDI Encrypter</a></td>
	<td valign="middle"><img src="images/app20.png"></td><td style="width: 195px;" valign="middle"><a href="XDISigner">XDI Signer</a></td>
	</tr><tr>
	<td valign="middle"><img src="images/app20.png"></td><td style="width: 195px;" valign="middle"><a href="XDIOperator">XDI Operator</a></td>
	<td valign="middle"><img src="images/app20.png"></td><td style="width: 195px;" valign="middle"><a href="XDIGrapher">XDI Grapher</a></td>
	</tr>
	</table>
	
	<hr noshade>

	<p class="subheader">Community</p>
	<table cellpadding="5"><tr>
	<td><a href="http://groups.google.com/group/xdi2">Google Group</a></td>
	<td><a href="https://github.com/projectdanube/xdi2/wiki/XDI2-Weekly-Call">Weekly Call</a></td>
	<td><a href="irc://irc.freenode.net:6667/xdi">IRC</a></td>
	<td><a href="http://en.wikipedia.org/wiki/XDI">XDI on Wikipedia</a></td>
	<td><a href="http://www.oasis-open.org/committees/xdi/">OASIS XDI TC</a></td>
	<td><a href="http://wiki.oasis-open.org/xdi/">OASIS XDI TC Wiki</a></td>
	</tr></table>

	</div>

</body>
</html>
