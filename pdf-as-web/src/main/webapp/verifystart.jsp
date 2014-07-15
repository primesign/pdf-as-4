<html>
<head>
</head>
<body>
<h1>Verify PDF-Dokument</h1>
<form action="Verify" method="POST" enctype="multipart/form-data">

	<select name="format">
		<option selected="selected">html</option>
		<option>json</option>
	</select>

	<input type="file" name="pdf-file" />

	<input type="submit" value="Verfiy">
</form>
</body>
</html>