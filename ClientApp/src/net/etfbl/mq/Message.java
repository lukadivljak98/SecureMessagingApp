package net.etfbl.mq;

import java.time.LocalDateTime;

public class Message {
    private String messageText;
    private String senderUsername;
    private LocalDateTime timeSent;

    public Message(String messageText, String senderUsername, LocalDateTime timeSent) {
        this.messageText = messageText;
        this.senderUsername = senderUsername;
        this.timeSent = timeSent;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public LocalDateTime getTimeSent() {
        return timeSent;
    }
}

