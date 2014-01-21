<%@page import="at.gv.egiz.pdfas.web.config.WebConfiguration"%>
<html>
<head>
<title>PDF-Signatur</title>
</head>
<body>
	<form role="form" action="Sign" method="POST"
		enctype="multipart/form-data">
		<input type="hidden" name="source" id="source" value="internal"/>
		<div class="form-group <% if(request.getAttribute("FILEERR") != null) { %> has-error  <% }  %>">
			<label for="exampleInputFile">Signieren: </label> <input type="file"
				name="pdfFile" id="pdfFile" accept="application/pdf">
			<p class="help-block">
			<% if(request.getAttribute("FILEERR") != null) { %>
			Bitte die zu signierende PDF Datei angeben.
			<% } else { %>
			Zu signierende PDF Datei 
			<% } %></p>
		</div>
		<% if(WebConfiguration.getOnlineBKUURL() != null || 
			  WebConfiguration.getLocalBKUURL() != null) { %>
		<div class="form-group">
			<!-- button type="submit" value="jks" name="connector" class="btn btn-primary">JKS</button-->
			<label for="bku"><img src="assets/img/onlineBKU.png" /></label>
			<% if(WebConfiguration.getLocalBKUURL() != null) { %>
			<button type="submit" value="bku" name="connector"
				class="btn btn-primary" id="bku">Lokale BKU</button>
				<% }  %>
				<% if(WebConfiguration.getOnlineBKUURL() != null) { %>
			<button type="submit" value="onlinebku" name="connector"
				class="btn btn-primary" id="onlinebku">Online BKU</button>
				<% }  %>
		</div>
		<% }  %>
		<% if(WebConfiguration.getHandyBKUURL() != null) { %>
		<div class="form-group">
			<label for="mobilebku"><img src="assets/img/mobileBKU.png" /></label>
			<button type="submit" value="mobilebku" name="connector"
				class="btn btn-primary" id="mobilebku">Handy</button>
			<!-- button type="submit" value="moa" name="connector" class="btn btn-primary">MOA-SS</button -->
		</div>
		<% }  %>
		<% if(WebConfiguration.getKeystoreEnabled()) { %>
		<div class="form-group">
			<button type="submit" value="jks" name="connector"
				class="btn btn-primary" id="jks">Server Keystore</button>
			<!-- button type="submit" value="moa" name="connector" class="btn btn-primary">MOA-SS</button -->
		</div>
		<% }  %>
	</form>
</body>
</html>