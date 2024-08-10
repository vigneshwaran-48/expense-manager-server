package com.vapps.expense.service;

import com.vapps.expense.annotation.UserIdValidator;
import com.vapps.expense.common.dto.StaticResourceDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.StaticResourceService;
import com.vapps.expense.common.service.UserService;
import com.vapps.expense.model.StaticResource;
import com.vapps.expense.repository.StaticResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StaticResourceServiceImpl implements StaticResourceService {

    @Autowired
    private StaticResourceRepository staticResourceRepository;

    @Autowired
    private UserService userService;

    @Override
    @UserIdValidator(positions = 0)
    public Optional<StaticResourceDTO> getResource(String userId, String resourceId) throws AppException {
        Optional<StaticResource> staticResource = staticResourceRepository.findByOwnerIdAndId(userId, resourceId);
        if (staticResource.isEmpty()) {
            staticResource =
                    staticResourceRepository.findByIdAndVisibility(resourceId, StaticResourceDTO.Visibility.PUBLIC);
        }
        if (staticResource.isPresent()) {
            return Optional.of(staticResource.get().toDTO());
        }
        return Optional.empty();
    }

    @Override
    @UserIdValidator(positions = 0)
    public StaticResourceDTO addResource(String userId, StaticResourceDTO staticResourceDTO) throws AppException {
        if (staticResourceDTO.getData() == null) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Empty data given for resource!");
        }
        if (staticResourceDTO.getType() == null) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Provide the content type of the resource!");
        }
        staticResourceDTO.setOwner(userService.getUser(userId).get());
        StaticResource staticResource = StaticResource.build(staticResourceDTO);
        StaticResource savedStaticResource = staticResourceRepository.save(staticResource);
        if (savedStaticResource == null) {
            throw new AppException("Error while adding static resource!");
        }
        return savedStaticResource.toDTO();
    }

    @Override
    @UserIdValidator(positions = 0)
    public void deleteResource(String userId, String resourceId) throws AppException {
        staticResourceRepository.deleteByIdAndOwnerId(resourceId, userId);
    }
}
