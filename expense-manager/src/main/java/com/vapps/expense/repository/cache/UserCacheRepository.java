package com.vapps.expense.repository.cache;

import com.vapps.expense.model.User;
import com.vapps.expense.repository.UserRepository;
import com.vapps.expense.repository.mongo.UserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserCacheRepository implements UserRepository {

    @Autowired
    private UserMongoRepository userRepository;

    @Override
    @Cacheable(value = "users", key = "'user_' + #id")
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @CachePut(value = "users", key = "'user_' + #user.getId()")
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    @Cacheable(value = "users", key = "#email")
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
