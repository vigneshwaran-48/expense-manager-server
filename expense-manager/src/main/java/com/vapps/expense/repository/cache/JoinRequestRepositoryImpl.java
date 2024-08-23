package com.vapps.expense.repository.cache;

import com.vapps.expense.model.JoinRequest;
import com.vapps.expense.repository.JoinRequestRepository;
import com.vapps.expense.repository.mongo.JoinRequestMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JoinRequestRepositoryImpl implements JoinRequestRepository {

    @Autowired
    private JoinRequestMongoRepository joinRequestRepository;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "join_request_family_id", key = "'join_request_family_id_' + #request.getFamilyId()"),
            @CacheEvict(value = "join_request_user_id", key = "'join_request_user_id_' + #request.getRequestUser().getId()")
    })
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

    @Override
    @Caching(evict = {
            @CacheEvict(value = "join_request", key = "'join_request_' + #id"),
            @CacheEvict(value = "join_request_family_id"),
            @CacheEvict(value = "join_request_user_id")
    })
    public void deleteById(String id) {
        joinRequestRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = "join_request_user_id", key = "'join_request_user_id_' + #requestUserId")
    public List<JoinRequest> findByRequestUserId(String requestUserId) {
        return joinRequestRepository.findByRequestUserId(requestUserId);
    }
}
