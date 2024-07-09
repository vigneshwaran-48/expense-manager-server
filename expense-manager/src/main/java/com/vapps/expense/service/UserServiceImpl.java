package com.vapps.expense.service;

import com.vapps.expense.common.dto.UserDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.UserService;
import com.vapps.expense.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void addUser(UserDTO user) throws AppException {
    }

    @Override
    public Optional<UserDTO> getUser(String userId) throws AppException {
        return Optional.empty();
    }
}
