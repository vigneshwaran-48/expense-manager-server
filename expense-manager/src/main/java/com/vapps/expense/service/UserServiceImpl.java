package com.vapps.expense.service;

import com.vapps.expense.common.dto.UserDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.UserService;
import com.vapps.expense.model.User;
import com.vapps.expense.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public void addUser(UserDTO user) throws AppException {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Email already exists!");
        }
        User userModel = User.build(user);
        User savedUser = userRepository.save(userModel);
        if (savedUser == null) {
            LOGGER.error("Error while creating user, SavedUser is null!");
            throw new AppException("Error while creating user!");
        }
    }

    @Override
    public Optional<UserDTO> getUser(String userId) throws AppException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(user.get().toDTO());
    }
}
