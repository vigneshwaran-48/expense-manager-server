package com.vapps.expense.common.service;

import com.vapps.expense.common.dto.UserDTO;
import com.vapps.expense.common.exception.AppException;

import java.util.List;
import java.util.Optional;

public interface UserService {

	UserDTO addUser(UserDTO user) throws AppException;

	Optional<UserDTO> getUser(String userId) throws AppException;

	UserDTO updateUser(String currentUserId, String userId, UserDTO user) throws AppException;

	List<UserDTO> findAllUser() throws AppException;
}
