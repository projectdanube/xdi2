<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>XDI Converter - Help</title>
<script type="text/javascript" src="tabber.js"></script>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body>

	<div class="header">
	<img src="images/logo64.png" align="middle">&nbsp;&nbsp;&nbsp;<span id="appname">XDI Converter</span>
	&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDIConverter">Back...</a>
	</div>

	<div class="tabber">

    <div class="tabbertab">

	<h2>Information</h2>

	<p>Here you can convert XDI documents between different serialization formats.</p>

	<p><b>Example X3 documents for Copy&amp;Paste:</b></p>
	
	<p>Simple subject-predicate-object statement:<br>
	<input type="text" size="80" value="[=markus[+friend[=drummond]]]"></p>
	
	<p>Simple example with all types of comments:<br>
	<input type="text" size="80" value="[<-- pre-sub -->=markus<-- post-sub -->[<-- pre-pred -->+test<-- post-pred -->[<-- pre-X3 -->[<-- pre-sub -->=drummond<-- post-sub -->[<-- pre-pred -->$is<-- post-pred -->[<-- pre-ref -->=bum<-- post-ref -->]]]<-- post-X3 -->]]]"></p>

	<p>Some special cases:<br>
	<input type="text" size="80" value="[=markus[+predicate.with.nothing.in.it]][=subject.with.nothing.in.it][=drummond[+predicate.with.empty.inner.graph[]]]"></p>

	<p>Long example with $xml and $json typed literals:<br>
	<input type="text" size="80" value="[=drummond[$is[=drummond.reed][=!F83.62B1.44F.2813]][$is$a[+person][@oasis+chair]][$has[+home][+work][+friend][@cordance][@parity]][+email[[=drummond+home[+email]][=drummond+work[+email]]]][+email+signature$type$xml[&quot;<?xml version=&quot;1.0&quot; encoding=&quot;ISO-8859-1&quot;?><signature xmlns=&quot;http://example.com/ns&quot;><name>Drummond Reed</name><affiliation>Cordance</affiliation><affiliation>Parity</affiliation><affiliation>OASIS</affiliation></signature>&quot;]][+picture$type$json[&quot;{&quot;Image&quot;:{&quot;Width&quot;:800,&quot;Height&quot;:600,&quot;IDs&quot;:[116,943,234,38793],&quot;Title&quot;:&quot;Drummond at Graduation&quot;,&quot;Thumbnail&quot;:{&quot;Width&quot;:&quot;100&quot;,&quot;Height&quot;:125,&quot;Url&quot;:&quot;http://www.example.com/image/481989943&quot;}}}&quot;]][=web*markus[=drummond+friend]]][=drummond+home[+email[&quot;drummond@example.com&quot;]]][=drummond+work[+email[[=drummond@cordance[+email]][=drummond@parity[+email]]]]][=drummond@cordance[+email[&quot;drummond.reed@cordance.example.com&quot;]]][=drummond@parity[+email[&quot;drummond.reed@parity.example.com&quot;]]][=drummond+friend[$is$a[$contract][@identity.commons$contract][@identity.commons+personal$contract]][$get[[=drummond+home][=drummond+work]]]]"></p>

	<p>Long example with versioning and comments:<br>
	<input type="text" size="80" value="[=drummond[$has[$v]][$v[!5]][+email[&quot;drummond.reed@gmail.com&quot;]][+email$v[!3]][$is[=!F83.62B1.44F.2813]]][=drummond$v[$has[!5]][$has$a[=drummond$v!5]][=drummond$v!1[[=drummond[$d[$d*2008-01-01T12:13:14Z]][$add[[=drummond[+person+name[&quot;Drummond Reed&quot;]][+email[&quot;drummond.reed@cordance.net&quot;]][+email$v<-- PREDICATE VERSIONING IS ON -->]]]]]]][=drummond$v!2[[=drummond[$d[$d*2008-01-02T07:22:59Z]][$mod[[=drummond[+email[&quot;drummond.reed@parityinc.net&quot;]]]]]]]][=drummond$v!3[[=drummond[$d[$d*2008-01-03T11:28:11Z]][$add[[=drummond[$is[=!F83.62B1.44F.2813]]]]]]]][=drummond$v!4[[=drummond[$d[$d*2008-01-04T02:30:41Z]][$mod[[=drummond[+email[&quot;drummond.reed@gmail.com&quot;]]]]]]]][=drummond$v!5[[=drummond[$d[$d*2008-01-05T03:34:52Z]][$del[[=drummond[+person+name]]]]]]]][=drummond$v!1[+person+name[&quot;Drummond Reed&quot;]][+email[&quot;drummond.reed@cordance.net&quot;]][+email$v[!1<-- +EMAIL VERSION NUMBER -->]]][=drummond$v!2[+person+name[&quot;Drummond Reed&quot;]][+email[&quot;drummond.reed@parityinc.net&quot;]][+email$v[!2<-- +EMAIL VERSION NUMBER -->]]][=drummond$v!3[+person+name[&quot;Drummond Reed&quot;]][+email[&quot;drummond.reed@parityinc.net&quot;]][$is[=!F83.62B1.44F.2813]]][=drummond$v!4[+person+name[&quot;Drummond Reed&quot;]][+email[&quot;drummond.reed@gmail.com&quot;]][+email$v[!3<-- +EMAIL VERSION NUMBER -->]][$is[=!F83.62B1.44F.2813]]][=drummond$v!5[+person+name[&quot;Drummond Reed&quot;]][$is[=!F83.62B1.44F.2813]]]"></p>

	</div>

    <div class="tabbertab">

	<h2>Formats</h2>

	<p><b>X3 Standard</b><br>
	Preserves all information in the XDI graph.</p>

	<p><b>X3 Simple</b><br>
	Preserves all information in the XDI graph.</p>

	<p><b>X3 Whitespace</b><br>
	Preserves all information in the XDI graph.</p>
	
	<p><b>XDI/XML</b><br>
	Preserves all information in the XDI graph.</p>

	<p><b>X-TRIPLES</b><br>
	Does not preserve inner graphs.<br>
	Does not preserve comments.</p>

	<p><b>XDI/JSON</b><br>
	Does not (currently) preserve comments on references, literals and inner graphs.</p>

	<p><b>XRI</b><br>
	Does not preserve comments.</p>

	</div>
	
	</div>

</body>
</html>
