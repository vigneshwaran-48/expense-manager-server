package com.vapps.expense.repository;

import com.vapps.expense.model.StaticResource;

import java.util.Optional;

public interface StaticResourceRepository {

    Optional<StaticResource> findById(String id);

    Optional<StaticResource> findByOwnerIdAndId(String ownerId, String id);

    StaticResource save(StaticResource staticResource);

    void deleteByIdAndOwnerId(String id, String ownerId);

}
