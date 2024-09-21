package com.vapps.expense.repository.mongo;

import com.vapps.expense.common.dto.StaticResourceDTO;
import com.vapps.expense.model.StaticResource;
import jakarta.transaction.Transactional;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StaticResourceMongoRepository extends MongoRepository<StaticResource, String> {

	@Transactional
	void deleteByIdAndOwnerId(String id, String ownerId);

	Optional<StaticResource> findByOwnerIdAndId(String ownerId, String id);

	Optional<StaticResource> findByIdAndVisibility(String id, StaticResourceDTO.Visibility visibility);
}
