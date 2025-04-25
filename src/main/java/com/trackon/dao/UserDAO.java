package com.trackon.dao;

import com.trackon.model.User;
import java.util.List;

public interface UserDAO extends BaseDAO<User> {
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findByRole(String role);
    boolean authenticate(String username, String password);
} 