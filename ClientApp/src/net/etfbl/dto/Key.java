package net.etfbl.dto;

import java.io.Serializable;

public class Key implements Serializable {

	private static final long serialVersionUID = 1L;
    private int id;
    private int userId;
    private String publicKey;
    private String encryptedPrivateKey;
    private String encodedIv;
    private String encryptionAlgorithm;
    
    public Key() {}
	
	public Key(int id, int userId, String publicKey, String encryptedPrivateKey, String encodedIv,
			String encryptionAlgorithm) {
		super();
		this.id = id;
		this.userId = userId;
		this.publicKey = publicKey;
		this.encryptedPrivateKey = encryptedPrivateKey;
		this.encodedIv = encodedIv;
		this.encryptionAlgorithm = encryptionAlgorithm;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public String getEncryptedPrivateKey() {
		return encryptedPrivateKey;
	}
	public void setEncryptedPrivateKey(String encryptedPrivateKey) {
		this.encryptedPrivateKey = encryptedPrivateKey;
	}
	public String getEncryptionAlgorithm() {
		return encryptionAlgorithm;
	}
	public void setEncryptionAlgorithm(String encryptionAlgorithm) {
		this.encryptionAlgorithm = encryptionAlgorithm;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getEncodedIv() {
		return encodedIv;
	}

	public void setEncodedIv(String encodedIv) {
		this.encodedIv = encodedIv;
	}
}
