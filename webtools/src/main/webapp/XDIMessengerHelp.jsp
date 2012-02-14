<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>XDI Messenger - Help</title>
<script type="text/javascript" src="tabber.js"></script>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body>

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

	<p>A native XDI endpoint is backed by an actual XDI document and can potentially process any XDI message.</p>
	
	<p style="font-weight: bold"><a target="_blank" href="https://graceland.parityinc.net/xdi-endpoint/mem-graph/">https://graceland.parityinc.net/xdi-endpoint/mem-graph/</a></p>
	<p>This is an XDI endpoint backed by an in-memory store.<br>
	<b>Versioning:</b> disabled. <b>Link contracts:</b> disabled.</p>
	
	<p style="font-weight: bold"><a target="_blank" href="https://graceland.parityinc.net/xdi-endpoint/mem-graph-ver/">https://graceland.parityinc.net/xdi-endpoint/mem-graph-ver/</a></p>
	<p>This is an XDI endpoint backed by an in-memory store.<br>
	<b>Versioning:</b> enabled. <b>Link contracts:</b> disabled.</p>
	
	<p style="font-weight: bold"><a target="_blank" href="https://graceland.parityinc.net/xdi-endpoint/xml-graph/">https://graceland.parityinc.net/xdi-endpoint/xml-graph/</a></p>
	<p>This is an XDI endpoint backed by an XDI/XML store.<br>
	<b>Versioning:</b> disabled. <b>Link contracts:</b> disabled.</p>
	
	<p style="font-weight: bold"><a target="_blank" href="https://graceland.parityinc.net/xdi-endpoint/xml-graph-ver/">https://graceland.parityinc.net/xdi-endpoint/xml-graph-ver/</a></p>
	<p>This is an XDI endpoint backed by an XDI/XML store.<br>
	<b>Versioning:</b> enabled. <b>Link contracts:</b> disabled.</p>
	
	<p style="font-weight: bold"><a target="_blank" href="https://graceland.parityinc.net/xdi-endpoint/bdb-graph/">https://graceland.parityinc.net/xdi-endpoint/bdb-graph/</a></p>
	<p>This is an XDI endpoint backed by a Berkely DB store.<br>
	<b>Versioning:</b> disabled. <b>Link contracts:</b> disabled.</p>
	
	<p style="font-weight: bold"><a target="_blank" href="https://graceland.parityinc.net/xdi-endpoint/bdb-graph-ver/">https://graceland.parityinc.net/xdi-endpoint/bdb-graph-ver/</a></p>
	<p>This is an XDI endpoint backed by a Berkely DB store.<br>
	<b>Versioning:</b> enabled. <b>Link contracts:</b> disabled.</p>
	
	<p style="font-weight: bold"><a target="_blank" href="https://graceland.parityinc.net/xdi-endpoint/hibernate-graph-ver/">https://graceland.parityinc.net/xdi-endpoint/hibernate-graph-ver/</a></p>
	<p>This is an XDI endpoint backed by a Hibernate store using an Apache Derby database.<br>
	<b>Versioning:</b> enabled. <b>Link contracts:</b> disabled.</p>
	
	<p style="font-weight: bold"><a target="_blank" href="https://graceland.parityinc.net/xdi-endpoint/x3-graph/">https://graceland.parityinc.net/xdi-endpoint/x3-graph/</a></p>
	<p>This is an XDI endpoint backed by a X3 text file.<br>
	<b>Versioning:</b> disabled. <b>Link contracts:</b> disabled.</p>

	</div>

	</div>

</body>
</html>
