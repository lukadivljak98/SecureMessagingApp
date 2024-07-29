package net.etfbl.beans;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import net.etfbl.dao.UserDAO;
import net.etfbl.dto.User;

public class UserBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private User user = new User();
	private User recipient;
	private boolean isLoggedIn = false;
	
	public UserBean() {
		this.recipient = this.getAll().get(0);
	}

	public boolean login(String username, String password) {
		if ((user = UserDAO.selectByUsernameAndPassword(username, password)) != null) {
			isLoggedIn = true;
			return true;
		}
		return false;
	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	public void logout() {
		user = new User();
		isLoggedIn = false;
	}

	public User getUser() {
		return user;
	}
	
	public User getRecipient() {
		return this.recipient;
	}
	
	public void setRecipient(User recipient) {
		this.recipient = recipient;
	}

	public boolean isUserNameAllowed(String username) {
		return UserDAO.isUserNameUsed(username);
	}
	
	public boolean add(User user) {
		return UserDAO.insert(user);
	}
	
	public List<User> getAll(){
		return UserDAO.getAllUsers().stream().filter((user) -> user.getId() != this.user.getId()).collect(Collectors.toList());
	}
	
	public User getUserById(int id) {
		return UserDAO.getUserById(id);
	}
}

