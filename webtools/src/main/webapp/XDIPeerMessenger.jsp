<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="xdi2.core.properties.XDI2Properties" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XDI Peer Messenger</title>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
<script language="javascript" type="text/javascript">
function updateSamples() {
	var categorySelect = document.getElementById('categorySelect');
	var category = categorySelect.options[categorySelect.selectedIndex].value;
	var html = '';
	<% for (int c=0; c<((List<String>) request.getAttribute("sampleCategories")).size(); c++) { %>
		if (category == <%= c %>) {
			<% for (int i=0; i<((List<List<String>>) request.getAttribute("sampleInputs1")).get(c).size(); i++) { %>
				html += '<a href="XDIPeerMessenger?category=<%= c+1 %>&sample=<%= i+1 %>" title="<%= ((List<List<String>>) request.getAttribute("sampleTooltips")).get(c).get(i) %>"><%= i+1 %></a>&nbsp;&nbsp;';
			<% } %>
		}
	<% } %>
	document.getElementById('samples').innerHTML = html;
}
</script>
</head>
<body onload="updateSamples();">
	<div id="imgtop"><img id="imgtopleft" src="images/xdi2-topleft.png"><a href="http://projectdanube.org/"><img id="imgtopright" src="images/xdi2-topright.png"></a></div>
	<div id="main">
	<div class="header">
	<span id="appname"><img src="images/app20b.png"> XDI Peer Messenger</span>
	&nbsp;&nbsp;&nbsp;&nbsp;Examples: 
	<select id="categorySelect" onchange="updateSamples();">
	<% for (int i=0; i<((List<String>) request.getAttribute("sampleCategories")).size(); i++) { %>
		<option value="<%= i %>" <%= (i + 1) == Integer.parseInt((String) request.getAttribute("category")) ? "selected" : "" %>><%= ((List<String>) request.getAttribute("sampleCategories")).get(i) %></option>
	<% } %>
	</select>
	&nbsp;&nbsp;
	<span id="samples"></span>
	<a href="index.jsp">&gt;&gt;&gt; Other Apps...</a><br>
	This is version <%= XDI2Properties.properties.getProperty("project.version") %> <%= XDI2Properties.properties.getProperty("project.build.timestamp") %>, Git commit <%= XDI2Properties.properties.getProperty("git.commit.id").substring(0,6) %> <%= XDI2Properties.properties.getProperty("git.commit.time") %>.
	</div>

	<% if (request.getAttribute("error") != null) { %>

		<p style="font-family: monospace; white-space: pre; color: red;"><%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %></p>

	<% } %>

	<form action="XDIPeerMessenger" method="post" accept-charset="UTF-8">

		<textarea class="input" name="input1" style="width: 45%" rows="12" wrap="off"><%= request.getAttribute("input1") != null ? request.getAttribute("input1") : "" %></textarea>
		<textarea class="input" name="input2" style="width: 45%" rows="12" wrap="off"><%= request.getAttribute("input2") != null ? request.getAttribute("input2") : "" %></textarea><br>
		<textarea class="input" name="message1" style="width: 45%" rows="12" wrap="off"><%= request.getAttribute("message1") != null ? request.getAttribute("message1") : "" %></textarea>
		<textarea class="input" name="message2" style="width: 45%" rows="12" wrap="off"><%= request.getAttribute("message2") != null ? request.getAttribute("message2") : "" %></textarea><br>

		<% String resultFormat = (String) request.getAttribute("resultFormat"); if (resultFormat == null) resultFormat = ""; %>
		<% String writeImplied = (String) request.getAttribute("writeImplied"); if (writeImplied == null) writeImplied = ""; %>
		<% String writeOrdered = (String) request.getAttribute("writeOrdered"); if (writeOrdered == null) writeOrdered = ""; %>
		<% String writePretty = (String) request.getAttribute("writePretty"); if (writePretty == null) writePretty = ""; %>
		<% String useFromInterceptor = (String) request.getAttribute("useFromInterceptor"); if (useFromInterceptor == null) useFromInterceptor = ""; %>
		<% String useToInterceptor = (String) request.getAttribute("useToInterceptor"); if (useToInterceptor == null) useToInterceptor = ""; %>
		<% String useVariablesInterceptor = (String) request.getAttribute("useVariablesInterceptor"); if (useVariablesInterceptor == null) useVariablesInterceptor = ""; %>
		<% String useRefInterceptor = (String) request.getAttribute("useRefInterceptor"); if (useRefInterceptor == null) useRefInterceptor = ""; %>
		<% String useReadOnlyInterceptor = (String) request.getAttribute("useReadOnlyInterceptor"); if (useReadOnlyInterceptor == null) useReadOnlyInterceptor = ""; %>
		<% String useMessagePolicyInterceptor = (String) request.getAttribute("useMessagePolicyInterceptor"); if (useMessagePolicyInterceptor == null) useMessagePolicyInterceptor = ""; %>
		<% String useLinkContractInterceptor = (String) request.getAttribute("useLinkContractInterceptor"); if (useLinkContractInterceptor == null) useLinkContractInterceptor = ""; %>
		<% String usePushCommandInterceptor = (String) request.getAttribute("usePushCommandInterceptor"); if (usePushCommandInterceptor == null) usePushCommandInterceptor = ""; %>

		<p>
		<input name="useFromInterceptor" type="checkbox" <%= useFromInterceptor.equals("on") ? "checked" : "" %>>FromInterceptor&nbsp;
		<input name="useToInterceptor" type="checkbox" <%= useToInterceptor.equals("on") ? "checked" : "" %>>ToInterceptor&nbsp;
		<input name="useVariablesInterceptor" type="checkbox" <%= useVariablesInterceptor.equals("on") ? "checked" : "" %>>VariablesInterceptor&nbsp;
		<input name="useRefInterceptor" type="checkbox" <%= useRefInterceptor.equals("on") ? "checked" : "" %>>RefInterceptor&nbsp;
		<input name="useReadOnlyInterceptor" type="checkbox" <%= useReadOnlyInterceptor.equals("on") ? "checked" : "" %>>ReadOnlyInterceptor&nbsp;
		<input name="useMessagePolicyInterceptor" type="checkbox" <%= useMessagePolicyInterceptor.equals("on") ? "checked" : "" %>>MessagePolicyInterceptor&nbsp;
		<input name="useLinkContractInterceptor" type="checkbox" <%= useLinkContractInterceptor.equals("on") ? "checked" : "" %>>LinkContractInterceptor
		<input name="usePushCommandInterceptor" type="checkbox" <%= usePushCommandInterceptor.equals("on") ? "checked" : "" %>>PushCommandInterceptor
		</p>

		Result Format:
		<select name="resultFormat">
		<option value="XDI/JSON" <%= resultFormat.equals("XDI/JSON") ? "selected" : "" %>>XDI/JSON</option>
		<option value="XDI DISPLAY" <%= resultFormat.equals("XDI DISPLAY") ? "selected" : "" %>>XDI DISPLAY</option>
		<option value="XDI/JSON/TREE" <%= resultFormat.equals("XDI/JSON/TREE") ? "selected" : "" %>>XDI/JSON/TREE</option>
		<option value="XDI/JSON/PARSE" <%= resultFormat.equals("XDI/JSON/PARSE") ? "selected" : "" %>>XDI/JSON/PARSE</option>
		<option value="XDI/JSON/TRIPLE" <%= resultFormat.equals("XDI/JSON/TRIPLE") ? "selected" : "" %>>XDI/JSON/TRIPLE</option>
		<option value="XDI/JSON/QUAD" <%= resultFormat.equals("XDI/JSON/QUAD") ? "selected" : "" %>>XDI/JSON/QUAD</option>
		</select>
		&nbsp;

		<input name="writeImplied" type="checkbox" <%= writeImplied.equals("on") ? "checked" : "" %>>implied=1

		<input name="writeOrdered" type="checkbox" <%= writeOrdered.equals("on") ? "checked" : "" %>>ordered=1

		<input name="writePretty" type="checkbox" <%= writePretty.equals("on") ? "checked" : "" %>>pretty=1

		<input type="hidden" name="category" value="<%= (String) request.getAttribute("category") %>">
		<input type="hidden" name="sample" value="<%= (String) request.getAttribute("sample") %>">
		<input type="submit" value="Go!">
		&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDIPeerMessengerHelp.jsp">What can I do here?</a>

	</form>

	<br><div class="line"></div>

	<% if (request.getAttribute("stats") != null) { %>
		<p>
		<%= request.getAttribute("stats") %>

		<% if (request.getAttribute("output") != null) { %>
			Copy&amp;Paste: <textarea style="width: 100px; height: 1.2em; overflow: hidden"><%= request.getAttribute("output") %></textarea>
		<% } %>
		</p>
	<% } %>

	<% if (request.getAttribute("output") != null) { %>
		<% if (request.getAttribute("outputId") != null && ! "".equals(request.getAttribute("output")) && ! "".equals(request.getAttribute("outputId"))) { %>
			<a class="graphit" target="_blank" href="http://neustar.github.io/xdi-grapheditor/xdi-grapheditor/public_html/index.html?input=<%= request.getRequestURL().toString().replaceFirst("/[^/]+$", "/XDIOutput?outputId=" + request.getAttribute("outputId")) %>">Graph It!</a>
		<% } %>
		<div class="result"><pre><%= request.getAttribute("output") %></pre></div><br>
	<% } %>

	</div>	
</body>
</html>
