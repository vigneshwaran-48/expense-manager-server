package com.vapps.expense.repository;

import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.model.Family;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FamilyRepository {

    Optional<Family> findById(String id);

    Family save(Family family);

    Family update(Family family);

    void deleteById(String id);

    Optional<Family> findByCreatedById(String createdById);

    List<Family> findByIdOrNameContainingIgnoreCaseAndVisibility(String id, String query,
            FamilyDTO.Visibility visibility, Pageable pageable);

    List<Family> findByIdOrNameContainingIgnoreCaseAndVisibility(String id, String query,
            FamilyDTO.Visibility visibility);
}
