package com.procuregov.dao;

import com.procuregov.model.User;
import java.util.List;

public interface UserDAO {
    boolean saveUser(User user);
    User findByEmail(String email);
    User findByUsername(String username);
    User findByRegistrationNumber(String regNumber);
    User findById(int userId);
    List<User> findAllByRole(String role);
    boolean updateFailedAttempts(String email, int attempts);
    boolean lockAccount(String email);
    boolean unlockAccount(String email);
    boolean updatePassword(String email, String newPasswordHash);
    boolean updateUser(User user);
    boolean deleteUser(int userId);
}
