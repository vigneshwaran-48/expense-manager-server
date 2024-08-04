package com.vapps.expense.repository.mongo;

import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.model.Family;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FamilyMongoRepository extends MongoRepository<Family, String> {
    Optional<Family> findByCreatedById(String createdById);

    @Query("{$or: [{ 'id': { $regex: ?0, $options: 'i' } }, { 'name': { $regex: ?1, $options: 'i' } }], 'visibility':" +
            " ?2}")
    List<Family> findByIdOrNameContainingIgnoreCaseAndVisibility(String id, String query,
            FamilyDTO.Visibility visibility, Pageable pageable);

    @Query("{$or: [{ 'id': { $regex: ?0, $options: 'i' } }, { 'name': { $regex: ?1, $options: 'i' } }], 'visibility':" +
            " ?2}")
    List<Family> findByIdOrNameContainingIgnoreCaseAndVisibility(String id, String query,
            FamilyDTO.Visibility visibility);
}
