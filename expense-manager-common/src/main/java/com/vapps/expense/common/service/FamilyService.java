package com.vapps.expense.common.service;

import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.exception.AppException;

import java.util.Optional;

public interface FamilyService {

    FamilyDTO createFamily(String userId, FamilyDTO family) throws AppException;

    FamilyDTO updateFamily(String userId, FamilyDTO family) throws AppException;

    Optional<FamilyDTO> getFamilyById(String userId, String id) throws AppException;

    void deleteFamilyById(String userId, String id) throws AppException;
}
