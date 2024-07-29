package net.etfbl.beans;

import java.io.Serializable;

import net.etfbl.dao.SecretDAO;
import net.etfbl.dto.Secret;

public class SecretBean implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public boolean save(Secret secret) {
		if(SecretDAO.save(secret))
			return true;
		else return false;
	}
	
	public Secret get(int senderId, int recipientId) {
		return SecretDAO.get(senderId, recipientId);
	}
}
