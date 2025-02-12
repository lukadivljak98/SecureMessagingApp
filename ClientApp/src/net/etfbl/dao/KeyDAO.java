package net.etfbl.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.etfbl.dto.Key;

public class KeyDAO {
	private static ConnectionPool connectionPool = ConnectionPool.getConnectionPool();
	private static final String SQL_INSERT_KEY = "INSERT INTO \"key\" (\"user_id\", \"public_key\", \"encrypted_private_key\", \"encoded_iv\", \"encryption_algorithm\") VALUES (?,?,?,?,?)";
	private static final String SQL_SELECT_KEY_BY_USER_ID = "SELECT * FROM \"key\" WHERE \"user_id\"=?";
	private static final String SQL_DELETE_BY_USER_ID = "DELETE FROM \"key\" WHERE \"user_id\"=?";

	public static boolean saveKey(Key key) {
		boolean result = false;
		Connection connection = null;
		Object values[] = { key.getUserId(), key.getPublicKey(), key.getEncryptedPrivateKey(),key.getEncodedIv(),
				key.getEncryptionAlgorithm() };
		try {
			connection = connectionPool.checkOut();
			PreparedStatement pstmt = DAOUtil.prepareStatement(connection, SQL_INSERT_KEY, true, values);
			pstmt.executeUpdate();
			if (pstmt.getUpdateCount() > 0) {
				result = true;
			}
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connectionPool.checkIn(connection);
		}
		return result;
	}

	public static Key getKeyByUserId(int userId) {
		Key key = null;
		Connection connection = null;
		ResultSet rs = null;
		Object values[] = { userId };
		try {
			connection = connectionPool.checkOut();
			PreparedStatement pstmt = DAOUtil.prepareStatement(connection, SQL_SELECT_KEY_BY_USER_ID, false, values);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				key = new Key();
				key.setId(rs.getInt("id"));
				key.setUserId(rs.getInt("user_id"));
				key.setPublicKey(rs.getString("public_key"));
				key.setEncryptedPrivateKey(rs.getString("encrypted_private_key"));
				key.setEncodedIv(rs.getString("encoded_iv"));
				key.setEncryptionAlgorithm(rs.getString("encryption_algorithm"));
			}
			pstmt.close();
		} catch (SQLException exp) {
			exp.printStackTrace();
		} finally {
			connectionPool.checkIn(connection);
		}
		return key;
	}

	public static boolean deleteKey(int userId) {
		boolean result = false;
		Connection connection = null;
		Object values[] = { userId };
		try {
			connection = connectionPool.checkOut();
			PreparedStatement pstmt = DAOUtil.prepareStatement(connection, SQL_DELETE_BY_USER_ID, false, values);
			int affectedRows = pstmt.executeUpdate();
			pstmt.close();

			if (affectedRows > 0) {
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connectionPool.checkIn(connection);
		}
		return result;
	}
}
