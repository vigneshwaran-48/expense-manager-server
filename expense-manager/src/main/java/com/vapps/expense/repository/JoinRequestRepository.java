package com.vapps.expense.repository;

import com.vapps.expense.model.JoinRequest;

import java.util.List;
import java.util.Optional;

public interface JoinRequestRepository {

    JoinRequest save(JoinRequest request);

    List<JoinRequest> findByFamilyId(String familyId);

    Optional<JoinRequest> findById(String id);

    void deleteById(String id);

    List<JoinRequest> findByRequestUserId(String requestUserId);
}
