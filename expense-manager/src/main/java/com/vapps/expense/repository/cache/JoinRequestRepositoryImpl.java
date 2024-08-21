package com.vapps.expense.repository.cache;

import com.vapps.expense.model.JoinRequest;
import com.vapps.expense.repository.JoinRequestRepository;
import com.vapps.expense.repository.mongo.JoinRequestMongoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JoinRequestRepositoryImpl implements JoinRequestRepository {

    private JoinRequestMongoRepository joinRequestRepository;

    @Override
    @CacheEvict(value = "join_request_family_id", key = "'join_request_family_id_' + #request.getFamilyId()")
    public JoinRequest save(JoinRequest request) {
        return joinRequestRepository.save(request);
    }

    @Override
    @Cacheable(value = "join_request_family_id", key = "'join_request_family_id_' + #familyId")
    public List<JoinRequest> findByFamilyId(String familyId) {
        return joinRequestRepository.findByFamilyId(familyId);
    }

    @Override
    @Cacheable(value = "join_request", key = "'join_request_' + #id")
    public Optional<JoinRequest> findById(String id) {
       return joinRequestRepository.findById(id);
    }
}
