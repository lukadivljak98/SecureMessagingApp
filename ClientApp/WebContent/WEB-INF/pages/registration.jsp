<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="styles/style2.css" type="text/css" rel="stylesheet">
<title>Kreiranje novog naloga</title>
</head>
<body>
	<div class="login">
		<h1>ETF Anonymous Messaging App</h1>
		<h2>Registracija</h2>
		<form method="POST" action="?action=registration">
			Korisni&#269;ko ime*<br /> <input type="text" name="username"
				id="username" /><br /><br /> Lozinka* <br /> <input type="password"
				name="password" id="password" /><br /><br /> Prezime* <br /> <input
				type="text" name="lastName" id="lastName" /><br /><br /> Ime* <br /> <input
				type="text" name="firstName" id="firstName" /><br /><br /> <input
				type="submit" value="Registruj nalog" name="submit" /><br />
			<h3><%=session.getAttribute("notification").toString()%></h3>
			<br /> <a href="/ClientApp">Prijava na sistem &gt;&gt;&gt;</a>
		</form>
	</div>
</body>
</html>