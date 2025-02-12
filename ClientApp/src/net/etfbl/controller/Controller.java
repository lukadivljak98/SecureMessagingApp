package net.etfbl.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.InvalidAlgorithmParameterException;
import java.util.Base64;

import net.etfbl.beans.KeyBean;
import net.etfbl.beans.SecretBean;
import net.etfbl.beans.UserBean;
import net.etfbl.dto.Key;
import net.etfbl.dto.Secret;
import net.etfbl.dto.User;
import net.etfbl.mq.ConnectionFactoryUtil;
import net.etfbl.mq.JsonUtil;
import net.etfbl.mq.Message;
import net.etfbl.mq.MessageUtil;
import com.rabbitmq.client.GetResponse;
import net.etfbl.mq.SegmentObject;
import net.etfbl.crypto.CryptoUtil;
import net.etfbl.dao.KeyDAO;

/**
 * Servlet implementation class Controller
 */
@WebServlet("/Controller")
public class Controller extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final static String QUEUE_NAME1 = "messages";
	private final static String QUEUE_NAME2 = "messages2";
	private final static String QUEUE_NAME3 = "messages3";
	private final static String QUEUE_NAME4 = "messages4";
	private final static String QUEUE_NAME5 = "keys";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Controller() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String address = "/WEB-INF/pages/login.jsp";
		String action = request.getParameter("action");
		HttpSession session = request.getSession();

		session.setAttribute("notification", "");

		if (action == null || action.equals("")) {
			address = "/WEB-INF/pages/login.jsp";
		} else if (action.equals("logout")) {
			Connection connection = (Connection) session.getAttribute("connection");
			Channel channel = (Channel) session.getAttribute("channel");
			String consumerTag = (String) session.getAttribute("consumerTag");
			if (consumerTag != null) {
				try {
					channel.basicCancel(consumerTag);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (connection != null && connection.isOpen()) {
				connection.close();
			}
			try {
				if (channel != null && channel.isOpen()) {
					channel.close();
				}
			} catch (IOException | TimeoutException e) {
				e.printStackTrace();
			}
			UserBean userBean = (UserBean) session.getAttribute("userBean");
			if (userBean != null && userBean.getUser() != null) {
				int userId = userBean.getUser().getId();
				KeyDAO.deleteKey(userId);
				userBean.logout();
			}
			session.invalidate();
			address = "/WEB-INF/pages/login.jsp";
		} else if (action.equals("login")) {
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			UserBean userBean = new UserBean();
			KeyBean keyBean = new KeyBean();
			SecretBean secretBean = new SecretBean();
			if (userBean.login(username, CryptoUtil.getHash(password))) {
				session.setAttribute("userBean", userBean);
				session.setAttribute("keyBean", keyBean);
				session.setAttribute("secretBean", secretBean);
				Connection connection = null;
				try {
					connection = ConnectionFactoryUtil.createConnection();
				} catch (IOException | TimeoutException e) {
					e.printStackTrace();
				}
				Channel channel = connection.createChannel();
				channel.queueDeclare(QUEUE_NAME1, false, false, false, null);
				channel.queueDeclare(QUEUE_NAME2, false, false, false, null);
				channel.queueDeclare(QUEUE_NAME3, false, false, false, null);
				channel.queueDeclare(QUEUE_NAME4, false, false, false, null);
				channel.queueDeclare(QUEUE_NAME5, false, false, false, null);
				session.setAttribute("connection", connection);
				session.setAttribute("channel", channel);
				address = "/WEB-INF/pages/chat.jsp";

				KeyPairGenerator keyPairGenerator = null;
				try {
					keyPairGenerator = KeyPairGenerator.getInstance("DH");
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
				keyPairGenerator.initialize(2048);
				KeyPair keyPair = keyPairGenerator.generateKeyPair();

				String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
				String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

				String masterKeyBase64 = System.getenv("MASTER_KEY");
				byte[] masterKeyBytes = Base64.getDecoder().decode(masterKeyBase64);
				SecretKeySpec secretKeySpec = new SecretKeySpec(masterKeyBytes, "AES");

				Cipher cipher = null;
				try {
					cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
					e.printStackTrace();
				}

				SecureRandom secureRandom = new SecureRandom();
				byte[] iv = new byte[16];
				secureRandom.nextBytes(iv);
				IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

				try {
					cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
				} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
					e.printStackTrace();
				}

				byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
				byte[] encryptedPrivateKeyBytes = null;
				try {
					encryptedPrivateKeyBytes = cipher.doFinal(privateKeyBytes);
				} catch (IllegalBlockSizeException | BadPaddingException e) {
					e.printStackTrace();
				}

				String encryptedPrivateKey = Base64.getEncoder().encodeToString(encryptedPrivateKeyBytes);
				String encodedIv = Base64.getEncoder().encodeToString(iv);

				int userId = userBean.getUser().getId();
				Key key = new Key(0, userId, publicKey, encryptedPrivateKey, encodedIv, "DH");
				KeyDAO.saveKey(key);

				startQueueListener(channel, userBean, session);
			} else {
				session.setAttribute("notification", "Pogresni parametri za pristup");
			}
		} else if (action.equals("registration")) {
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			UserBean userBean = new UserBean();
			try {
				if (username != null && password != null && request.getParameter("lastName") != null
						&& request.getParameter("firstName") != null) {
					if (userBean.isUserNameAllowed(request.getParameter("username"))) {
						User user = new User(0, username, CryptoUtil.getHash(password),
								request.getParameter("lastName"), request.getParameter("firstName"));
						if (userBean.add(user)) {
							address = "/WEB-INF/pages/login.jsp";
						}
					} else {
						session.setAttribute("notification", "Username je zauzet");
						address = "/WEB-INF/pages/registration.jsp";
					}
				} else {
					session.setAttribute("notification", "Sva polja moraju biti popunjena");
					address = "/WEB-INF/pages/registration.jsp";
				}
			} catch (Exception e) {
				session.setAttribute("notification", "ERROR: " + e.getMessage());
			}
		} else {
			UserBean userBean = (UserBean) session.getAttribute("userBean");
			if (userBean == null || !userBean.isLoggedIn()) {
				address = "/WEB-INF/pages/login.jsp";
			} else if (action.equals("recipientSelected")) {
				int recipientId = Integer.parseInt(request.getParameter("recipientId"));
				KeyBean keyBean = (KeyBean) session.getAttribute("keyBean");
				Key recipientKey = keyBean.getKeyByUserId(recipientId);
				Key senderKey = keyBean.getKeyByUserId(userBean.getUser().getId());
				String masterKey = System.getenv("MASTER_KEY");
				SecretKey secretKey = CryptoUtil.generateSharedSecret(recipientKey.getPublicKey(),
						senderKey.getEncryptedPrivateKey(), masterKey, senderKey.getEncodedIv());
				byte[] secretKeyBytes = secretKey.getEncoded();
				String secretKeyBase64 = Base64.getEncoder().encodeToString(secretKeyBytes);
				String encryptedSecretKey = CryptoUtil.encryptSecretKey(secretKeyBase64, masterKey);
				try {
					User recipient = userBean.getUserById(recipientId);
					userBean.setRecipient(recipient);
					SecretBean secretBean = (SecretBean) session.getAttribute("secretBean");
					Secret secret = new Secret(0, userBean.getUser().getId(), recipient.getId(), encryptedSecretKey);
					secretBean.save(secret);
				} catch (Exception e) {
					e.printStackTrace();
				}
				address = "/WEB-INF/pages/chat.jsp";
			} else if (action.equals("sendMessage")) {
				String message = request.getParameter("message");
				List<String> segments = MessageUtil.divideMessage(message);
				Channel channel = (Channel) session.getAttribute("channel");
				String messageId = UUID.randomUUID().toString();
				SecretBean secretBean = (SecretBean) session.getAttribute("secretBean");
				for (int i = 0; i < segments.size(); i++) {
					String segment = segments.get(i);
					Secret secret = secretBean.get(userBean.getUser().getId(), userBean.getRecipient().getId());
					String secretKeyBase64 = CryptoUtil.decryptSecretKey(secret.getMasterKey(), System.getenv("MASTER_KEY"));
					byte[] secretKeyBytes = Base64.getDecoder().decode(secretKeyBase64);
					SecretKey secretKey = new SecretKeySpec(secretKeyBytes, "AES");
					String encryptedSegment = CryptoUtil.encryptSegmentText(segment, secretKey);
					String queueName = MessageUtil.getNextQueueName();
					SegmentObject segmentObject = new SegmentObject(userBean.getRecipient().getId(), encryptedSegment,
							i, messageId, segments.size(), userBean.getUser().getUsername(), LocalDateTime.now(),
							userBean.getUser().getId());
					String segmentJsonString = JsonUtil.objectToJson(segmentObject);
					byte[] segmentJsonBytes = segmentJsonString.getBytes(StandardCharsets.UTF_8);
					channel.basicPublish("", queueName, null, segmentJsonBytes);
				}
				Map<Integer, List<Message>> senderMessages = (Map<Integer, List<Message>>) session
						.getAttribute("senderMessages");
				if (senderMessages == null) {
					senderMessages = new HashMap<>();
				}
				LocalDateTime timeSent = LocalDateTime.now();
				String senderUsername = userBean.getUser().getUsername();
				Message msg = new Message(message, senderUsername, timeSent);

				int recipientId = userBean.getRecipient().getId();
				List<Message> senderMessagesList = senderMessages.getOrDefault(recipientId, new ArrayList<>());
				senderMessagesList.add(msg);
				senderMessages.put(recipientId, senderMessagesList);

				session.setAttribute("senderMessages", senderMessages);
				address = "/WEB-INF/pages/chat.jsp";
			} else if (action.equals("fetchMessages")) {
				address = "/WEB-INF/pages/messages.jsp";
			} else {
				address = "/WEB-INF/pages/404.jsp";
			}
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher(address);
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	private void startQueueListener(Channel channel, UserBean userBean, HttpSession session) {
		new Thread(() -> {
			try {
				String queueName = "recipient." + userBean.getUser().getId();
				channel.queueDeclare(queueName, false, false, false, null);

				Map<String, List<SegmentObject>> receivedSegmentsMap = new ConcurrentHashMap<>();

				DeliverCallback deliverCallback = (consumerTag, delivery) -> {
					String messageJson = new String(delivery.getBody(), StandardCharsets.UTF_8);
					SegmentObject segmentObject = JsonUtil.jsonToObject(messageJson, SegmentObject.class);

					receivedSegmentsMap.putIfAbsent(segmentObject.getMessageId(), new ArrayList<>());
					receivedSegmentsMap.get(segmentObject.getMessageId()).add(segmentObject);

					if (receivedSegmentsMap.get(segmentObject.getMessageId()).size() == segmentObject
							.getTotalSegments()) {
						List<SegmentObject> messageSegments = receivedSegmentsMap.get(segmentObject.getMessageId());
						messageSegments.sort(Comparator.comparingInt(SegmentObject::getSegmentIndex));

						StringBuilder messageBuilder = new StringBuilder();
						SecretBean secretBean = (SecretBean) session.getAttribute("secretBean");
						for (SegmentObject segment : messageSegments) {
							Secret secret = secretBean.get(segment.getSenderId(), segment.getRecipientId());
							String secretKeyBase64 = CryptoUtil.decryptSecretKey(secret.getMasterKey(), System.getenv("MASTER_KEY"));
							byte[] secretKeyBytes = Base64.getDecoder().decode(secretKeyBase64);
							SecretKey secretKey = new SecretKeySpec(secretKeyBytes, "AES");
							String decryptedSegment = CryptoUtil.decryptSegmentText(segment.getSegmentText(), secretKey);
							messageBuilder.append(decryptedSegment);
						}

						String reconstructedMessage = messageBuilder.toString();
						System.out.println(reconstructedMessage);
						System.out.println(segmentObject.getSenderUsername());
						Message msg = new Message(reconstructedMessage, segmentObject.getSenderUsername(),
								segmentObject.getTimeSent());

						synchronized (session) {
							Map<Integer, List<Message>> receivedMessagesMap = (Map<Integer, List<Message>>) session
									.getAttribute("recipientMessages");
							if (receivedMessagesMap == null) {
								receivedMessagesMap = new HashMap<>();
							}
							int senderId = segmentObject.getSenderId();
							List<Message> receivedMessages = receivedMessagesMap.get(senderId);
							if (receivedMessages == null) {
								receivedMessages = new ArrayList<>();
								receivedMessagesMap.put(senderId, receivedMessages);
							}
							receivedMessages.add(msg);
							session.setAttribute("recipientMessages", receivedMessagesMap);
						}
					}
				};

				String consumerTag = channel.basicConsume(queueName, true, deliverCallback, cancelConsumerTag -> {
				});
				session.setAttribute("consumerTag", consumerTag);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
}