<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>XDI Grapher - Help</title>
<script type="text/javascript" src="tabber.js"></script>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body>

	<img src="images/logo_xdi4j.gif" align="middle">&nbsp;&nbsp;&nbsp;<span style="font-weight: bold; border-bottom: 3px solid #707070">XDI Grapher</span> by Azigo
	&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDIGrapher" style="color: #707070">Back...</a>

	<div class="tabber">

    <div class="tabbertab">

	<h2>Information</h2>

	<p>This tool implements methods for visualizing XDI graphs.</p>
	<p>The following graphing methods are supported:</p>
	<p><b>XDI RDF Box Graph</b></p> 
	<p>The motivation is to have an easy, intuitive way to illustrate the special capability of 
	XDI RDF to express context, i.e., the ability to nest addressable RDF graphs to any depth.</p>
	<p>In XDI box graphs, every RDF node and every RDF arc is represented by a box. The reason 
	boxes are used is that with XDI addressing, every segment of an XRI structured identifier
	(whether an XDI subject, predicate, or reference) may itself represent an RDF graph. So a 
	complete XDI address may represent the union of dozens of component RDF graphs. In fact 
	the only XDI addresses that do not represent RDF graphs (because they are graph "primitives") 
	are: a) single delimiter characters, and b) literals.</p>
	<p><b>XDI S/P/O Graph</b></p>
	<p>An experimental graphing method for emphasizing the basic triple structure (subject/predicate/object) of XDI.</p>

	</div>

    <div class="tabbertab">

	<h2>More information</h2>

	<p><a href="http://www.oasis-open.org/committees/download.php/35590/xdi-rdf-box-graphs-v2.pdf">xdi-rdf-box-graphs-v2.pdf</a></p>

	</div>
	
	</div>

</body>
</html>
