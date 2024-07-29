package net.etfbl.mq;

import java.time.LocalDateTime;

public class SegmentObject {
	
	private int recipientId;
	private String segmentText;
	private int segmentIndex;
	private String messageId;
	private int totalSegments;
    private String senderUsername;
    private LocalDateTime timeSent; 
    
    public SegmentObject() {}
	
	public SegmentObject(int recipientId, String segmentText, int segmentIndex, String messageId, int totalSegments, String senderUsername, LocalDateTime timeSent) {
		super();
		this.recipientId = recipientId;
		this.segmentText = segmentText;
		this.segmentIndex = segmentIndex;
		this.messageId = messageId;
		this.totalSegments = totalSegments;
		this.senderUsername = senderUsername;
		this.timeSent = timeSent;
	}
	
	public int getRecipientId() {
		return recipientId;
	}
	public void setRecipientId(int recipientId) {
		this.recipientId = recipientId;
	}
	public String getSegmentText() {
		return segmentText;
	}
	public void setSegmentText(String segmentText) {
		this.segmentText = segmentText;
	}

	public int getSegmentIndex() {
		return segmentIndex;
	}

	public void setSegmentIndex(int segmentIndex) {
		this.segmentIndex = segmentIndex;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public int getTotalSegments() {
		return totalSegments;
	}

	public void setTotalSegments(int totalSegments) {
		this.totalSegments = totalSegments;
	}

	public String getSenderUsername() {
		return senderUsername;
	}

	public void setSenderUsername(String senderUsername) {
		this.senderUsername = senderUsername;
	}

	public LocalDateTime getTimeSent() {
		return timeSent;
	}

	public void setTimeSent(LocalDateTime timeSent) {
		this.timeSent = timeSent;
	}

}
