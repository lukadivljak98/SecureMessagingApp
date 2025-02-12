package net.etfbl.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.etfbl.beans.UserBean;
import net.etfbl.dto.User;


public class UserDAO {
	private static ConnectionPool connectionPool = ConnectionPool.getConnectionPool();
	private static final String SQL_SELECT_BY_USERNAME_AND_PASSWORD = "SELECT * FROM \"user\" WHERE \"username\"=? AND \"password\"=?";
	private static final String SQL_IS_USERNAME_USED = "SELECT * FROM \"user\" WHERE \"username\"=?";
	private static final String SQL_INSERT = "INSERT INTO \"user\" (\"username\", \"password\", \"first_name\", \"last_name\") VALUES (?,?,?,?)";
	private static final String SQL_SELECT_ALL = "SELECT * FROM \"user\"";
	private static final String SQL_SELECT_BY_ID = "SELECT * FROM \"user\" WHERE \"id\"=?";
	
	public static User selectByUsernameAndPassword(String username, String password){
		User user = null;
		Connection connection = null;
		ResultSet rs = null;
		Object values[] = {username, password};
		try {
			connection = connectionPool.checkOut();
			PreparedStatement pstmt = DAOUtil.prepareStatement(connection,
					SQL_SELECT_BY_USERNAME_AND_PASSWORD, false, values);
			rs = pstmt.executeQuery();
			if (rs.next()){
				user = new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("last_name"), rs.getString("first_name"));
			}
			pstmt.close();
		} catch (SQLException exp) {
			exp.printStackTrace();
		} finally {
			connectionPool.checkIn(connection);
		}
		return user;
	}
	
	public static boolean isUserNameUsed(String username) {
		boolean result = true;
		Connection connection = null;
		ResultSet rs = null;
		Object values[] = {username};
		try {
			connection = connectionPool.checkOut();
			PreparedStatement pstmt = DAOUtil.prepareStatement(connection,
					SQL_IS_USERNAME_USED, false, values);
			rs = pstmt.executeQuery();
			if (rs.next()){
				result = false;
			}
			pstmt.close();
		} catch (SQLException exp) {
			exp.printStackTrace();
		} finally {
			connectionPool.checkIn(connection);
		}
		return result;
	}
	
	public static boolean insert(User user) {
		boolean result = false;
		Connection connection = null;
		ResultSet generatedKeys = null;
		Object values[] = { user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName() };
		try {
			connection = connectionPool.checkOut();
			PreparedStatement pstmt = DAOUtil.prepareStatement(connection, SQL_INSERT, true, values);
			pstmt.executeUpdate();
			generatedKeys = pstmt.getGeneratedKeys();
			if(pstmt.getUpdateCount()>0) {
				result = true;
			}
			if (generatedKeys.next())
				user.setId(generatedKeys.getInt(1));
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connectionPool.checkIn(connection);
		}
		return result;
	}
	
	public static List<User> getAllUsers() {
	    List<User> userList = new ArrayList<User>();
	    Connection connection = null;
	    try {
	        connection = connectionPool.checkOut();
	        PreparedStatement pstmt = DAOUtil.prepareStatement(connection, SQL_SELECT_ALL, false);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            User user = new User(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("username"),
	                    rs.getString("password"));
	            userList.add(user);
	        }
	        rs.close();
	        pstmt.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        connectionPool.checkIn(connection);
	    }
	    return userList;
	}
	
	public static User getUserById(int id) {
	    User user = null;
	    Connection connection = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    Object values[] = { id };
	    try {
	        connection = connectionPool.checkOut();
	        pstmt = DAOUtil.prepareStatement(connection, SQL_SELECT_BY_ID, false, values);
	        rs = pstmt.executeQuery();
	        if (rs.next()) {
	            user = new User();
	            user.setId(rs.getInt("id"));
	            user.setFirstName(rs.getString("first_name"));
	            user.setLastName(rs.getString("last_name"));
	            user.setPassword(rs.getString("password"));
	            user.setUsername(rs.getString("username"));
	        }
	        rs.close();
	        pstmt.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        connectionPool.checkIn(connection);
	    }
	    return user;
	}
}

