package com.vapps.expense.service;

import com.vapps.expense.annotation.UserIdValidator;
import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.FamilyService;
import com.vapps.expense.repository.FamilyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FamilyServiceImpl implements FamilyService {

    @Autowired
    private FamilyRepository familyRepository;

    @Override
    @UserIdValidator(positions = 0)
    public FamilyDTO createFamily(String userId, FamilyDTO family) throws AppException {
        return null;
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
