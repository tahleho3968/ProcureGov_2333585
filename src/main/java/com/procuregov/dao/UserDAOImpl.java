package com.procuregov.dao;

import com.procuregov.model.User;
import com.procuregov.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UserDAOImpl implements UserDAO {
    
    private static final Logger logger = Logger.getLogger(UserDAOImpl.class.getName());
    
    @Override
    public boolean saveUser(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, full_name, " +
                     "registration_number, contact_number, physical_address, role) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setString(4, user.getFullName());
            pstmt.setString(5, user.getRegistrationNumber());
            pstmt.setString(6, user.getContactNumber());
            pstmt.setString(7, user.getPhysicalAddress());
            pstmt.setString(8, user.getRole());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.severe("Error saving user: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding user by email: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding user by username: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public User findByRegistrationNumber(String regNumber) {
        String sql = "SELECT * FROM users WHERE registration_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, regNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding user by registration number: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<User> findAllByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            logger.severe("Error finding users by role: " + e.getMessage());
        }
        return users;
    }
    
    @Override
    public boolean updateFailedAttempts(String email, int attempts) {
        String sql = "UPDATE users SET failed_attempts = ? WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, attempts);
            pstmt.setString(2, email);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.severe("Error updating failed attempts: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean lockAccount(String email) {
        String sql = "UPDATE users SET account_locked = true WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.severe("Error locking account: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean unlockAccount(String email) {
        String sql = "UPDATE users SET account_locked = false, failed_attempts = 0 WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.severe("Error unlocking account: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean updatePassword(String email, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPasswordHash);
            pstmt.setString(2, email);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.severe("Error updating password: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username=?, full_name=?, contact_number=?, " +
                     "physical_address=? WHERE user_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getFullName());
            pstmt.setString(3, user.getContactNumber());
            pstmt.setString(4, user.getPhysicalAddress());
            pstmt.setInt(5, user.getUserId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.severe("Error updating user: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.severe("Error deleting user: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public User findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
        
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        
        } catch (SQLException e) {
            logger.severe("Error finding user by ID: " + e.getMessage());
        }
        return null;
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setRegistrationNumber(rs.getString("registration_number"));
        user.setContactNumber(rs.getString("contact_number"));
        user.setPhysicalAddress(rs.getString("physical_address"));
        user.setRole(rs.getString("role"));
        user.setAccountLocked(rs.getBoolean("account_locked"));
        user.setFailedAttempts(rs.getInt("failed_attempts"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }
}
