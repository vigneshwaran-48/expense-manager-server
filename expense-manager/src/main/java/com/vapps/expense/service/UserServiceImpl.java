package com.vapps.expense.service;

import com.vapps.expense.annotation.UserIdValidator;
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

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserDTO addUser(UserDTO user) throws AppException {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Email already exists!");
        }
        User userModel = User.build(user);
        User savedUser = userRepository.save(userModel);
        if (savedUser == null) {
            LOGGER.error("Error while creating user, SavedUser is null!");
            throw new AppException("Error while creating user!");
        }
        return savedUser.toDTO();
    }

    @Override
    public Optional<UserDTO> getUser(String userId) throws AppException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(user.get().toDTO());
    }

    @Override
    @UserIdValidator(positions = { 0, 1 })
    public UserDTO updateUser(String currentUserId, String userId, UserDTO user) throws AppException {
        if (!currentUserId.equals(userId)) {
            // TODO In future this may be like admin can edit members.
            LOGGER.error("User {} trying to update user {}", currentUserId, userId);
            throw new AppException(HttpStatus.FORBIDDEN.value(), "You don't have access to update user!");
        }
        UserDTO existingUser = getUser(userId).get();
        user.setId(userId);
        user.setEmail(existingUser.getEmail());
        if (user.getAge() == 0) {
            user.setAge(existingUser.getAge());
        }
        if (user.getFirstName() == null) {
            user.setFirstName(existingUser.getFirstName());
        }
        if (user.getLastName() == null) {
            user.setLastName(existingUser.getLastName());
        }
        if (user.getName() == null) {
            user.setName(existingUser.getName());
        }
        User updatedUser = userRepository.update(User.build(user));
        if (updatedUser == null) {
            throw new AppException("Error while updating user!");
        }
        return updatedUser.toDTO();
    }

    @Override
    public List<UserDTO> findAllUser() throws AppException {
        return userRepository.findAll().stream().map(User::toDTO).toList();
    }

}
