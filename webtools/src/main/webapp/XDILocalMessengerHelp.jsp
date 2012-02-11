<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>XDI Local Messenger - Help</title>
<script type="text/javascript" src="tabber.js"></script>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body>

	<img src="images/logo_xdi4j.gif" align="middle">&nbsp;&nbsp;&nbsp;<span style="font-weight: bold; border-bottom: 3px solid #45b1fd">XDI Local Messenger</span> by Azigo
	&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDILocalMessenger" style="color: #45b1fd">Back...</a>

	<div class="tabber">

    <div class="tabbertab">

	<h2>Information</h2>

	<p>Here you can apply XDI messages to a local XDI document and view results.</p>

	<ul>
	<li>Messages can be entered in any format.</li>
	<li>Message results are displayed in X3 Simple (use the XDI Converter to convert to different formats).</li>
	<li>$add, $mod and $del operation will modify the XDI document in the left input text area.</li>
	<li>$get operations will result in a direct result.</li>
	<li>If you get the error "Unknown serialization format", use the XDI Validator to debug your input document and message.</li>
	</ul>

	</div>

    <div class="tabbertab">

	<h2>Versioning</h2>

	<p>If "Versioning" is enabled, every $add, $mod and $del operation will create
	new versions of the affected subjects in the graphs.</p>

	</div>

    <div class="tabbertab">

	<h2>HINs</h2>

	<p>HINs (Hash I-Numbers) are a way of having multiple literal values on a
	predicate, without losing addressability of all nodes in the XDI graph. If this
	feature is enabled, HINs are automatically created when new literals are added
	to the XDI graph.</p>

	</div>

    <div class="tabbertab">

	<h2>Link Contracts</h2>

	<p>Link contracts govern permissions on a graph. If this feature is enabled,
	certain operations may not be possible, depending on the link contracts in
	the target graph.</p>

	</div>

    <div class="tabbertab">

	<h2>Sender Verification</h2>

	<p>If "Sender Verification" is enabled, the sender of an XDI message is verified
	before the message is applied. In order for this to work, the following steps are
	performed:</p>
	
	<ul>
	<li>There must be a signature on the message.</li>
	<li>When resolved, the sender XRI's XRDS document must contain a certificate
	with a public key.</li>
	<li>The signature on the message is verified using the discovered public key.</li>
	</ul> 

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
