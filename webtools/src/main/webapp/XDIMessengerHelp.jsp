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

	<img src="images/logo_xdi4j.gif" align="middle">&nbsp;&nbsp;&nbsp;<span style="font-weight: bold; border-bottom: 3px solid #7070a0">XDI Messenger</span> by Azigo
	&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDIMessenger" style="color: #7070a0">Back...</a>

	<div class="tabber">

    <div class="tabbertab">

	<h2>Information</h2>

	Here you can send XDI messages to XDI endpoints and view results.

	<ul>
	<li>Messages can be entered in any format.</li>
	<li>Messages are sent to the endpoint in X3 Standard format (although XDI4j endpoints can receive all formats).</li>
	<li>Message results are requested from the endpoint in X3 Standard format (although XDI4j endpoints can send results in all formats).</li>
	<li>Message results are displayed in X3 Simple (use the XDI Converter to convert to different formats).</li>
	<li>Only $get operations will result in a direct response.</li>
	<li>If you get the error "Unknown serialization format", use the XDI Validator to debug your message.</li>
	</ul>

	Graphs can also be accessed with HTTP GET requests containing an XDI address. If you enter the endpoint URI in your browser, you will get the whole graph. If you enter a subject XRI after the endpoint URI, you will get only the statements rooted in that subject, etc.

	</div>

    <div class="tabbertab">

	<h2>Native XDI endpoints</h2>

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

    <div class="tabbertab">

	<h2>Other XDI endpoints</h2>

	<p>These endpoints can also be accessed using XDI, but may not accept all messages.</p>
	
	<p style="font-weight: bold"><a target="_blank" href="https://graceland.parityinc.net/xdi-endpoint/xml-file/">https://graceland.parityinc.net/xdi-endpoint/xml-file/</a></p>
	<p>This is an XDI endpoint backed by an XML file.<br>
	It accepts only $get messages.<br>
	<b>Versioning:</b> disabled. <b>Link contracts:</b> disabled.</p>
	
	<p style="font-weight: bold"><a target="_blank" href="https://graceland.parityinc.net/xdi-endpoint/xrd-file/">https://graceland.parityinc.net/xdi-endpoint/xrd-file/</a></p>
	<p>This is an XDI endpoint backed by an XRD file.<br>
	It accepts only $get messages.<br>
	<b>Versioning:</b> disabled. <b>Link contracts:</b> disabled.</p>
	
	<p style="font-weight: bold"><a target="_blank" href="https://graceland.parityinc.net/xdi-endpoint/text-file/">https://graceland.parityinc.net/xdi-endpoint/text-file/</a></p>
	<p>This is an XDI endpoint backed by a simple text file.<br>
	A $add message in the form [=yourname[$add[[$[$type$mime$text["test text"]]]]]] adds a line to the text file.<br>
	A $del message in the form [=yourname[$del[[+line*3]]]] deletes a line from the text file.<br>
	A $mod message in the form [=yourname[$mod[[+line*3[$type$mime$text["Edit line"]]]]]] replaces a line in the file.<br>
	A $get message in the form [=yourname[$get]] reads the whole file.<br>
	A $get message in the form [=yourname[$get[[+line*3]]]] reads a single line.<br>
	A $del message in the form [=yourname[$del]] deletes the whole file.<br>
	<b>Versioning:</b> disabled. <b>Link contracts:</b> disabled.</p>
	
	<p style="font-weight: bold"><a target="_blank" href="https://graceland.parityinc.net/xdi-idas/context-ldap/">https://graceland.parityinc.net/xdi-idas/context-ldap/</a></p>
	<p>This is an XDI endpoint backed by a Higgins IdAS LDAP context.<br>
	It accepts only $get messages.<br>
	<b>Versioning:</b> disabled. <b>Link contracts:</b> disabled.</p>
	
	<p style="font-weight: bold"><a target="_blank" href="https://graceland.parityinc.net/xdi-endpoint/counter/">https://graceland.parityinc.net/xdi-endpoint/counter/</a></p>
	<p>This is an XDI endpoint that exposes a single integer value.<br>
	A $add message increments the integer.<br>
	A $del message decrements the integer.<br>
	A $mod message can change the integer to an arbitrary value.<br>
	<b>Versioning:</b> disabled. <b>Link contracts:</b> disabled.</p>
	
	<p style="font-weight: bold"><a target="_blank" href="https://graceland.parityinc.net/xdi-endpoint/echo/">https://graceland.parityinc.net/xdi-endpoint/echo/</a></p>
	<p>This is an XDI endpoint that simply returns all messages you send to it.<br>
	<b>Versioning:</b> disabled. <b>Link contracts:</b> disabled.</p>

	</div>

    <div class="tabbertab">

	<h2>Example messages</h2>

	<p style="font-weight: bold">Add something to a graph</p>
	<p style="font-family: Courier">[=yourname[$add[[=yourname[+name["First Last"]]]]]]</p>
	<p style="font-family: Courier">[=yourname[$add[[=yourname[+friend[=yourfriend1][=yourfriend2]]]]]]</p>

	<p style="font-weight: bold">Delete everything at a graph</p>
	<p style="font-family: Courier">[=yourname[$del]]</p>

	<p style="font-weight: bold">Get everything from the graph</p>
	<p style="font-family: Courier">[=yourname[$get]]</p>

	<p style="font-weight: bold">Get something from the graph</p>
	<p style="font-family: Courier">[=yourname[$get[[=yourname]]]]</p>
	<p style="font-family: Courier">[=yourname[$get[[=yourname[+friend]]]]]</p>

	<p style="font-weight: bold">Change something at the graph</p>
	<p style="font-family: Courier">[=yourname[$mod[[=yourname[+name["First Middle Last"]]]]]]</p>

	</div>

	</div>

</body>
</html>
