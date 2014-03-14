<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator"
	prefix="decorator"%>
<%@page contentType="text/html; charset=UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<meta name="description" content="PDF-Signatur" />
<meta name="author" content="EGIZ" />
<link rel="shortcut icon" href="assets/ico/favicon.png" />

<title><decorator:title></decorator:title></title>
<decorator:head></decorator:head>
</head>

<body onload="<decorator:getProperty property="body.onload" />">
	<h3>PDF-Signatur</h3>

	<p>
		<decorator:body></decorator:body>
	</p>
</body>
</html>