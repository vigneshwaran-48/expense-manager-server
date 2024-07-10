package com.vapps.expense.repository;

import com.vapps.expense.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(String id);

    User save(User user);

    User update(User user);

    Optional<User> findByEmail(String email);
}
