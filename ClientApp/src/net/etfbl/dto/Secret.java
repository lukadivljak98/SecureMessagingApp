package net.etfbl.dto;

import java.io.Serializable;

public class Secret implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private int senderId;
	private int recipientId;
	private String masterKey;

	public Secret() {
	}

	public Secret(int id, int senderId, int recipientId, String masterKey) {
		super();
		this.id = id;
		this.senderId = senderId;
		this.recipientId = recipientId;
		this.masterKey = masterKey;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

	public int getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(int recipientId) {
		this.recipientId = recipientId;
	}

	public String getMasterKey() {
		return masterKey;
	}

	public void setMasterKey(String masterKey) {
		this.masterKey = masterKey;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
