<%@ page import="net.etfbl.beans.UserBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Messaging App</title>
<link href="styles/style2.css" type="text/css" rel="stylesheet">
</head>
<body>
	<div class="login">
		<h1>ETF Anonymous Messaging App</h1>
		<h2>Prijava na sistem</h2>
		<form method="POST" action="?action=login">
			Korisni&#269;ko ime<br /> <input type="text" name="username"
				id="username" /><br /><br /> Lozinka <br /> <input type="password"
				name="password" id="password" /><br /><br /> <input type="submit"
				value="Prijavi me" name="submit" /><br />
			<h3><%=session.getAttribute("notification") != null ? session.getAttribute("notification").toString() : ""%></h3>
			<br /> <a href="?action=registration">Kreiraj novi nalog
				&gt;&gt;&gt;</a>
		</form>
	</div>
</body>
</html>