<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>XDI Local Messenger</title>
</head>
<body>

	<img src="images/logo_xdi4j.gif" align="middle">&nbsp;&nbsp;&nbsp;<span style="font-weight: bold; border-bottom: 3px solid #45b1fd">XDI Local Messenger</span> by Azigo
	&nbsp;&nbsp;&nbsp;&nbsp;<a href="Other.jsp" style="color: #45b1fd">Other Apps...</a>

	<% if (request.getAttribute("error") != null) { %>
			
		<p><font color="red"><%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %></font></p>

	<% } %>

	<form action="XDILocalMessenger" method="post">

		<table width="100%" cellspacing="0" cellpadding="0" border="0">
		<tr>
		<td width="50%" style="padding-right: 10px">
			<textarea name="input" style="width: 100%" rows="12"><%= request.getAttribute("input") != null ? request.getAttribute("input") : "" %></textarea><br>
		</td>
		<td width="50%" style="padding-left: 10px">
			<textarea name="message" style="width: 100%" rows="12"><%= request.getAttribute("message") != null ? request.getAttribute("message") : "" %></textarea><br>
		</td>
		</tr>
		</table>

		<% String versioningSupport = (String) request.getAttribute("versioningSupport"); if (versioningSupport == null) versioningSupport = ""; %>
		<% String linkContractSupport = (String) request.getAttribute("linkContractSupport"); if (linkContractSupport == null) linkContractSupport = ""; %>
		<% String senderVerification = (String) request.getAttribute("senderVerification"); if (senderVerification == null) senderVerification = ""; %>
		<% String routing = (String) request.getAttribute("routing"); if (routing == null) routing = ""; %>
		<% String to = (String) request.getAttribute("to"); if (to == null) to = ""; %>

		Result Format:
		<select name="to">
		<option value="X3 Standard" <%= to.equals("X3 Standard") ? "selected" : "" %>>X3 Standard</option>
		<option value="X3 Simple" <%= to.equals("X3 Simple") ? "selected" : "" %>>X3 Simple</option>
		<option value="X3 Whitespace" <%= to.equals("X3 Whitespace") ? "selected" : "" %>>X3 Whitespace</option>
		<option value="XDI/XML" <%= to.equals("XDI/XML") ? "selected" : "" %>>XDI/XML</option>
		<option value="X-TRIPLES" <%= to.equals("X-TRIPLES") ? "selected" : "" %>>X-TRIPLES</option>
		<option value="XDI/JSON" <%= to.equals("XDI/JSON") ? "selected" : "" %>>XDI/JSON</option>
		<option value="X3J" <%= to.equals("X3J") ? "selected" : "" %>>X3J</option>
		<option value="XRI" <%= to.equals("XRI") ? "selected" : "" %>>XRI</option>
		</select>
		&nbsp;
		<input name="versioningSupport" type="checkbox" <%= versioningSupport.equals("on") ? "checked" : "" %>>Versioning
		<input name="linkContractSupport" type="checkbox" <%= linkContractSupport.equals("on") ? "checked" : "" %>>Link Contracts&nbsp;
		<input type="submit" value="Go!">
		&nbsp;&nbsp;&nbsp;&nbsp;<a href="Help.jsp" style="color: #45b1fd">What can I do here?</a>

		<% if (request.getAttribute("stats") != null) { %>
			<p>
			<%= request.getAttribute("stats") %>

			<% if (request.getAttribute("output") != null) { %>
				Copy&amp;Paste: <textarea style="width: 100px; height: 1.2em; overflow: hidden"><%= request.getAttribute("output") != null ? request.getAttribute("output") : "" %></textarea>
			<% } %>
			</p>
		<% } %>

		<% if (request.getAttribute("output") != null) { %>
			<div style="background-color: #88cdfd; padding-left: 10px; border: 2px dashed black"><pre><%= request.getAttribute("output") != null ? request.getAttribute("output") : "" %></pre></div><br>
		<% } %>
	</form>
	
</body>
</html>
