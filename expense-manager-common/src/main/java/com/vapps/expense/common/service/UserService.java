package com.vapps.expense.common.service;

import com.vapps.expense.common.dto.UserDTO;
import com.vapps.expense.common.exception.AppException;

import java.util.Optional;

public interface UserService {

    void addUser(UserDTO user) throws AppException;

    Optional<UserDTO> getUser(String userId) throws AppException;
}
