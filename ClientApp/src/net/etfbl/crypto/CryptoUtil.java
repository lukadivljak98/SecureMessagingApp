package net.etfbl.crypto;

import java.nio.charset.StandardCharsets;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class CryptoUtil {
	
	//private static SecretKey secret;
	
	public static String getHash(String input) {
		 StringBuilder hexString = new StringBuilder();
		 try {
	        MessageDigest md = MessageDigest.getInstance("SHA-256");
	        byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
	        hexString = new StringBuilder();
	        for (byte b : hash) {
	            String hex = Integer.toHexString(0xff & b);
	            if (hex.length() == 1) {
	                hexString.append('0');
	            }
	            hexString.append(hex);
	        }
	        
		 }catch(NoSuchAlgorithmException e) {
			 
		 }
		 return hexString.toString();
	}
	
	public static SecretKey generateSharedSecret(String recipientPublicKeyBase64, String encryptedSenderPrivateKeyBase64, String masterKeyBase64, String ivBase64) {
		SecretKey sharedSecret = null;
		try {
	        System.out.println("recipientPublicKeyBase64: " + recipientPublicKeyBase64);
	        System.out.println("encryptedSenderPrivateKeyBase64: " + encryptedSenderPrivateKeyBase64);
	        System.out.println("masterKeyBase64: " + masterKeyBase64);
	        System.out.println("ivBase64: " + ivBase64);

	        byte[] masterKeyBytes = Base64.getDecoder().decode(masterKeyBase64);
	        SecretKey masterKey = new SecretKeySpec(masterKeyBytes, "AES");

	        byte[] encryptedSenderPrivateKeyBytes = Base64.getDecoder().decode(encryptedSenderPrivateKeyBase64);
	        byte[] ivBytes = Base64.getDecoder().decode(ivBase64);
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
	        cipher.init(Cipher.DECRYPT_MODE, masterKey, ivParameterSpec);
	        byte[] senderPrivateKeyBytes = cipher.doFinal(encryptedSenderPrivateKeyBytes);
	        System.out.println("senderPrivateKeyBytes: " + Arrays.toString(senderPrivateKeyBytes));
	        System.out.println("senderPrivateKeyBytes length: " + senderPrivateKeyBytes.length);

	        byte[] recipientPublicKeyBytes = Base64.getDecoder().decode(recipientPublicKeyBase64);
	        KeyFactory keyFactory = KeyFactory.getInstance("DH");
	        PublicKey recipientPublicKey = keyFactory.generatePublic(new X509EncodedKeySpec(recipientPublicKeyBytes));
	        System.out.println("recipientPublicKey: " + recipientPublicKey);
	        PrivateKey senderPrivateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(senderPrivateKeyBytes));
	        System.out.println("senderPrivateKey: " + senderPrivateKey);
	        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
	        keyAgreement.init(senderPrivateKey);
	        keyAgreement.doPhase(recipientPublicKey, true);
	        byte[] sharedSecretBytes = keyAgreement.generateSecret();
	        sharedSecret = new SecretKeySpec(sharedSecretBytes, 0, 16, "AES");

	        return sharedSecret;

	    } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
	        e.printStackTrace();
	    }
	    return sharedSecret;
	}

	public static String encryptSegmentText(String segmentText, SecretKey secret) {
	    try {
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

	        SecureRandom secureRandom = new SecureRandom();
	        byte[] iv = new byte[16];
	        secureRandom.nextBytes(iv);
	        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

	        cipher.init(Cipher.ENCRYPT_MODE, secret, ivParameterSpec);

	        byte[] plaintextBytes = segmentText.getBytes(StandardCharsets.UTF_8);
	        byte[] encryptedBytes = cipher.doFinal(plaintextBytes);

	        byte[] combinedBytes = new byte[iv.length + encryptedBytes.length];
	        System.arraycopy(iv, 0, combinedBytes, 0, iv.length);
	        System.arraycopy(encryptedBytes, 0, combinedBytes, iv.length, encryptedBytes.length);

	        String encryptedSegmentTextBase64 = Base64.getEncoder().encodeToString(combinedBytes);

	        return encryptedSegmentTextBase64;
	    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
	        e.printStackTrace();
	    }

	    return null;
	}
	
	public static String decryptSegmentText(String encryptedSegmentTextBase64, SecretKey secret) {
	    try {
	        byte[] combinedBytes = Base64.getDecoder().decode(encryptedSegmentTextBase64);

	        byte[] iv = new byte[16];
	        System.arraycopy(combinedBytes, 0, iv, 0, iv.length);
	        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

	        byte[] encryptedBytes = new byte[combinedBytes.length - iv.length];
	        System.arraycopy(combinedBytes, iv.length, encryptedBytes, 0, encryptedBytes.length);

	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

	        cipher.init(Cipher.DECRYPT_MODE, secret, ivParameterSpec);

	        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

	        String decryptedSegmentText = new String(decryptedBytes, StandardCharsets.UTF_8);

	        return decryptedSegmentText;
	    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
	        e.printStackTrace();
	    }

	    return null;
	}
	
	public static String encryptSecretKey(String secretKeyBase64, String masterKeyBase64) {
        try {
            byte[] masterKeyBytes = Base64.getDecoder().decode(masterKeyBase64);
            SecretKey masterKey = new SecretKeySpec(masterKeyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            SecureRandom secureRandom = new SecureRandom();
            byte[] iv = new byte[16];
            secureRandom.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, masterKey, ivParameterSpec);

            byte[] secretKeyBytes = Base64.getDecoder().decode(secretKeyBase64);
            byte[] encryptedSecretKeyBytes = cipher.doFinal(secretKeyBytes);

            byte[] combinedBytes = new byte[iv.length + encryptedSecretKeyBytes.length];
            System.arraycopy(iv, 0, combinedBytes, 0, iv.length);
            System.arraycopy(encryptedSecretKeyBytes, 0, combinedBytes, iv.length, encryptedSecretKeyBytes.length);

            String encryptedSecretKeyBase64 = Base64.getEncoder().encodeToString(combinedBytes);

            return encryptedSecretKeyBase64;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String decryptSecretKey(String encryptedSecretKeyBase64, String masterKeyBase64) {
        try {
            byte[] masterKeyBytes = Base64.getDecoder().decode(masterKeyBase64);
            SecretKey masterKey = new SecretKeySpec(masterKeyBytes, "AES");

            byte[] combinedBytes = Base64.getDecoder().decode(encryptedSecretKeyBase64);

            byte[] iv = new byte[16];
            System.arraycopy(combinedBytes, 0, iv, 0, iv.length);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            byte[] encryptedSecretKeyBytes = new byte[combinedBytes.length - iv.length];
            System.arraycopy(combinedBytes, iv.length, encryptedSecretKeyBytes, 0, encryptedSecretKeyBytes.length);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE, masterKey, ivParameterSpec);

            byte[] decryptedSecretKeyBytes = cipher.doFinal(encryptedSecretKeyBytes);

            String decryptedSecretKeyBase64 = Base64.getEncoder().encodeToString(decryptedSecretKeyBytes);

            return decryptedSecretKeyBase64;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return null;
    }
}
