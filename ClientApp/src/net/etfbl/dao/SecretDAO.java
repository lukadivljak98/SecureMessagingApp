package net.etfbl.dao;

import net.etfbl.dto.Secret;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SecretDAO {
    private static ConnectionPool connectionPool = ConnectionPool.getConnectionPool();
    private static final String SQL_INSERT_SECRET = "INSERT INTO \"secret\" (\"sender_id\", \"recipient_id\", \"master_key\") VALUES (?, ?, ?)";
    private static final String SQL_SELECT_SECRET_BY_SENDER_RECIPIENT = "SELECT * FROM \"secret\" WHERE \"sender_id\"=? AND \"recipient_id\"=?";
    
    public static boolean save(Secret secret) {
        boolean result = false;
        Connection connection = null;
        Object values[] = { secret.getSenderId(), secret.getRecipientId(), secret.getMasterKey() };
        
        try {
            connection = connectionPool.checkOut();
            PreparedStatement pstmt = DAOUtil.prepareStatement(connection, SQL_INSERT_SECRET, true, values);
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

    public static Secret get(int senderId, int recipientId) {
        Secret secret = null;
        Connection connection = null;
        ResultSet rs = null;
        Object values[] = { senderId, recipientId };
        
        try {
            connection = connectionPool.checkOut();
            PreparedStatement pstmt = DAOUtil.prepareStatement(connection, SQL_SELECT_SECRET_BY_SENDER_RECIPIENT, false, values);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                secret = new Secret();
                secret.setId(rs.getInt("id"));
                secret.setSenderId(rs.getInt("sender_id"));
                secret.setRecipientId(rs.getInt("recipient_id"));
                secret.setMasterKey(rs.getString("master_key"));
            }
            pstmt.close();
        } catch (SQLException exp) {
            exp.printStackTrace();
        } finally {
            connectionPool.checkIn(connection);
        }
        return secret;
    }
}
