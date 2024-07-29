package net.etfbl.beans;

import java.io.Serializable;

import net.etfbl.dao.KeyDAO;
import net.etfbl.dto.Key;

public class KeyBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public boolean saveKey(Key key) {
		if(KeyDAO.saveKey(key))
			return true;
		else return false;
	}

	public Key getKeyByUserId(int userId) {
		return KeyDAO.getKeyByUserId(userId);
	}
	
	public boolean deleteKey(int userId) {
		if(KeyDAO.deleteKey(userId))
			return true;
		else return false;
	}

}
