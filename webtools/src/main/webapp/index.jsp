<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="xdi2.core.properties.XDI2Properties" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XDI2</title>
<script type="text/javascript" src="tabber.js"></script>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
<link rel="shortcut icon" href="favicon.ico" />
</head>
<body>
	<div id="imgtop"><img id="imgtopleft" src="images/xdi2-topleft.png"><img id="imgtopright" src="images/xdi2-topright.png"></div>
	<div id="main">
	<div class="header">
	<span id="appname"><img src="images/app20b.png"> XDI2</span>
	&nbsp;&nbsp;&nbsp;&nbsp;
	See <a href="http://github.com/projectdanube/xdi2">http://github.com/projectdanube/xdi2</a> for the code and documentation.
	</div>
	
	<p>XDI2 ("XDI Two") is an XDI library for Java, designed to be a general-purpose, lightweight and modular implementation of XDI specifications.</p>

	<p class="subheader">XDI Tools</p>

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
	</p>
	
	<div class="line"></div>

	<p class="subheader">XDI Server</p>
	<p>
	<table cellpadding="5"><tr>
	<td>Click here to access the local XDI server: <a href="<%= request.getRequestURL().substring(0, request.getRequestURL().toString().lastIndexOf('/')) %>/xdi/"><%= request.getRequestURL().substring(0, request.getRequestURL().toString().lastIndexOf('/')) %>/xdi/</a></td>
	</tr></table>
	</p>
	
	<div class="line"></div>

	<p class="subheader">Community</p>
	<p>
	<table cellpadding="5"><tr>
	<td><a href="http://github.com/projectdanube/xdi2">Github</a></td>
	<td><a href="http://groups.google.com/group/xdi2">Google Group</a></td>
	<td><a href="https://github.com/projectdanube/xdi2/wiki/XDI2-Weekly-Call">Weekly Call</a></td>
	<td><a href="irc://irc.freenode.net:6667/xdi">IRC</a></td>
	</tr></table>
	</p>
	
	<div class="line"></div>

	<p class="subheader">Information</p>
	<p>
	<table cellpadding="5"><tr>
	<td><a href="http://en.wikipedia.org/wiki/XDI">XDI on Wikipedia</a></td>
	<td><a href="http://www.oasis-open.org/committees/xdi/">OASIS XDI TC</a></td>
	<td><a href="http://wiki.oasis-open.org/xdi/">OASIS XDI TC Wiki</a></td>
	</tr></table>
	</p>
	
	<div class="line"></div>

	<p>This is version <strong><%= XDI2Properties.properties.getProperty("project.version") %> <%= XDI2Properties.properties.getProperty("project.build.timestamp") %></strong>, Git commit <strong><%= XDI2Properties.properties.getProperty("git.commit.id") %> <%= XDI2Properties.properties.getProperty("git.commit.time") %></strong>.</p>

	</div>
</body>
</html>
