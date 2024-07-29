<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="net.etfbl.dto.User"%>
<%@page import="net.etfbl.beans.UserBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Comparator"%>
<%@page import="net.etfbl.mq.Message"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<jsp:useBean id="userBean" type="net.etfbl.beans.UserBean"
	scope="session" />
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="styles/style.css" type="text/css" rel="stylesheet">
<title>Messaging App</title>
</head>
<body>
	<h1>ETF Anonymous Messaging App</h1>
	<a href="?action=logout">Odjava sa sistema</a>
	<hr />
	<div class="container">
		<div class="users-list">
			<h2>Users</h2>
			<ul>
				<%
				for (User user : userBean.getAll()) {
				%>
				<li><a
					href="?action=recipientSelected&recipientId=<%=user.getId()%>"><%=user.getUsername()%></a></li>
				<%
				}
				%>
			</ul>
		</div>
		<div class="chat-area">
			<h2>Chat</h2>
			<div class="messages" id="messagesContainer">
				<jsp:include page="messages.jsp" />
			</div>
			<div class="input-section">
				<form action="?action=sendMessage" method="post">
					<input type="text" name="message"
						placeholder="Type your message..." style="width: 100%;">
					<button type="submit">Send</button>
				</form>
			</div>
		</div>
	</div>
	<script>
        function updateMessages() {
            fetch('Controller?action=fetchMessages')
                .then(response => response.text())
                .then(html => {
                    const messagesContainer = document.getElementById('messagesContainer');
                    messagesContainer.innerHTML = html;
                })
                .catch(error => {
                    console.error('Error updating messages:', error);
                });
        }

        setInterval(updateMessages, 2000);
    </script>
</body>
</html>
