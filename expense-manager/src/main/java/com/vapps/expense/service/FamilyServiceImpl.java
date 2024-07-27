package com.vapps.expense.service;

import com.vapps.expense.annotation.UserIdValidator;
import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.dto.UserDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.FamilyService;
import com.vapps.expense.common.service.UserService;
import com.vapps.expense.model.Family;
import com.vapps.expense.repository.FamilyMemberRepository;
import com.vapps.expense.repository.FamilyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FamilyServiceImpl implements FamilyService {

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(FamilyServiceImpl.class);

    @Override
    @UserIdValidator(positions = 0)
    public FamilyDTO createFamily(String userId, FamilyDTO family) throws AppException {
        if (familyMemberRepository.findByMemberId(userId).isPresent()) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Already present in a family!");
        }
        UserDTO userDTO = userService.getUser(userId).get();
        family.setCreatedBy(userDTO);
        Family familyModel = Family.build(family);
        familyModel.setCreatedTime(LocalDateTime.now());
        Family savedFamily = familyRepository.save(familyModel);
        if (savedFamily == null) {
            LOGGER.error("Created family is null!");
            throw new AppException("Error while creating family");
        }
        return savedFamily.toDTO();
    }

    @Override
    @UserIdValidator(positions = 0)
    public FamilyDTO updateFamily(String userId, FamilyDTO family) throws AppException {
        return null;
    }

    @Override
    @UserIdValidator(positions = 0)
    public Optional<FamilyDTO> getFamilyById(String userId, String id) throws AppException {
        return Optional.empty();
    }

    @Override
    @UserIdValidator(positions = 0)
    public void deleteFamilyById(String userId, String id) throws AppException {

    }
}
