package com.vapps.expense.common.service;

import com.vapps.expense.common.dto.StaticResourceDTO;
import com.vapps.expense.common.exception.AppException;

import java.util.Optional;

public interface StaticResourceService {

    Optional<StaticResourceDTO> getResource(String userId, String resourceId) throws AppException;

    StaticResourceDTO addResource(String userId, StaticResourceDTO staticResourceDTO) throws AppException;

    void deleteResource(String userId, String resourceId) throws AppException;

}
