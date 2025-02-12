<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
	<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Comparator"%>
<%@page import="net.etfbl.mq.Message"%>
<%@page import="net.etfbl.dto.User"%>
<%@page import="net.etfbl.beans.UserBean"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<jsp:useBean id="userBean" type="net.etfbl.beans.UserBean"
	scope="session" />
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Messaging App</title>
</head>
<body>
	<div class="messages">
		<%
		Map<Integer, List<Message>> recipientMessages = (Map<Integer, List<Message>>) request.getSession()
				.getAttribute("recipientMessages");
		Map<Integer, List<Message>> senderMessages = (Map<Integer, List<Message>>) request.getSession()
				.getAttribute("senderMessages");
		List<Message> messages = new ArrayList<>();
		if (recipientMessages != null && userBean.getRecipient() != null) {
			List<Message> receivedMessages = recipientMessages.get(userBean.getRecipient().getId());
			if (receivedMessages != null) {
				messages.addAll(receivedMessages);
			}
		}
		if (senderMessages != null && userBean.getRecipient() != null) {
			List<Message> sentMessages = senderMessages.get(userBean.getRecipient().getId());
			if (sentMessages != null) {
				messages.addAll(sentMessages);
			}
		}
		messages.sort(Comparator.comparing(Message::getTimeSent));
		if (!messages.isEmpty()) {
			for (Message message : messages) {
		%>
		<div
			class="message <%=message.getSenderUsername().equals(userBean.getUser().getUsername()) ? "sent" : "received"%>">
			<p class="sender-info"><%=message.getSenderUsername()%>
				<span class="time-sent"><%=message.getTimeSent().format(DateTimeFormatter.ofPattern("HH:mm"))%></span>
			</p>
			<p class="message-text"><%=message.getMessageText()%></p>
		</div>
		<%
		}
		}
		%>

	</div>
</body>
</html>