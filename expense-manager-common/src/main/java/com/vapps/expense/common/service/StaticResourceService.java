package com.vapps.expense.common.service;

import com.vapps.expense.common.dto.StaticResourceDTO;
import com.vapps.expense.common.exception.AppException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface StaticResourceService {

    Optional<StaticResourceDTO> getResource(String userId, String resourceId) throws AppException;

    StaticResourceDTO addResource(String userId, StaticResourceDTO staticResourceDTO) throws AppException;

    StaticResourceDTO addResource(String userId, MultipartFile file, StaticResourceDTO.Visibility visibility) throws AppException;

    void deleteResource(String userId, String resourceId) throws AppException;

}
