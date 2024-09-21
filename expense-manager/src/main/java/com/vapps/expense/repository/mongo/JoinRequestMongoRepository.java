package com.vapps.expense.repository.mongo;

import com.vapps.expense.model.JoinRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface JoinRequestMongoRepository extends MongoRepository<JoinRequest, String> {

	List<JoinRequest> findByFamilyId(String familyId);

	List<JoinRequest> findByRequestUserId(String requestUserId);

	Optional<JoinRequest> findByFamilyIdAndRequestUserId(String familyId, String requestUserId);
}
