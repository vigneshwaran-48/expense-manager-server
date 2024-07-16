package com.vapps.expense.aspect;

import com.vapps.expense.annotation.UserIdValidator;
import com.vapps.expense.common.dto.UserDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@Aspect
public class UserAspect {

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAspect.class);

    @Before("@annotation(userIdValidator)")
    public void checkUserExists(JoinPoint joinPoint, UserIdValidator userIdValidator) throws AppException {
        int[] positionsToCheck = userIdValidator.positions();

        Object[] args = joinPoint.getArgs();

        for (int position : positionsToCheck) {
            String id = (String) args[position];

            if (id == null) {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "User Id is required!");
            }

            Optional<UserDTO> user = userService.getUser(id);
            if (user.isEmpty()) {
                LOGGER.error("User {} not exists", id);
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "User " + id + " not exists!");
            }
        }
    }
}
