package net.etfbl.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rabbitmq.client.*;

import net.etfbl.mq.ConnectionFactoryUtil;
import net.etfbl.mq.JsonUtil;
import net.etfbl.mq.SegmentObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class MessageServer {
	
	private final String queueName;
	private Connection connection;
    private Channel channel;

    public MessageServer(String queueName) {
        this.queueName = queueName;
    }
    
    public void start() throws IOException, TimeoutException {
    	connection = ConnectionFactoryUtil.createConnection();
        channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);
        System.out.println(" [*] Waiting for messages on queue: " + queueName);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            byte[] receivedBytes = delivery.getBody();
            String receivedJson = new String(receivedBytes, StandardCharsets.UTF_8);
            try {
                SegmentObject receivedSegmentObject = JsonUtil.jsonToObject(receivedJson, SegmentObject.class);
                System.out.println(" [x] Received message for recipient: " + receivedSegmentObject.getRecipientId());

                String recipientQueue = "recipient." + receivedSegmentObject.getRecipientId();
                channel.queueDeclare(recipientQueue, false, false, false, null);
                channel.basicPublish("", recipientQueue, null, receivedBytes);
                System.out.println(" [x] Forwarded message to queue: " + recipientQueue);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }
    
    public void close() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}

