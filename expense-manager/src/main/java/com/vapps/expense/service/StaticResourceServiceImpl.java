package com.vapps.expense.service;

import com.vapps.expense.annotation.UserIdValidator;
import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.dto.FamilyMemberDTO;
import com.vapps.expense.common.dto.StaticResourceDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.FamilyService;
import com.vapps.expense.common.service.StaticResourceService;
import com.vapps.expense.common.service.UserService;
import com.vapps.expense.model.Family;
import com.vapps.expense.model.StaticResource;
import com.vapps.expense.repository.StaticResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class StaticResourceServiceImpl implements StaticResourceService {

	@Autowired
	private StaticResourceRepository staticResourceRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private FamilyService familyService;

	private static final Logger LOGGER = LoggerFactory.getLogger(StaticResourceServiceImpl.class);

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
		Optional<FamilyDTO> family = familyService.getUserFamily(userId);
		if (family.isPresent()) {
			staticResource = staticResourceRepository.findByOwnerIdAndId(family.get().getId(), resourceId);
			if (staticResource.isPresent() &&
					staticResource.get().getVisibility() == StaticResourceDTO.Visibility.FAMILY) {
				// Just another level of check, Is it a family level visible resource
				return Optional.of(staticResource.get().toDTO());
			}
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

		staticResourceDTO.setOwnerId(userId);
		if (staticResourceDTO.getVisibility() == StaticResourceDTO.Visibility.FAMILY) {
			Optional<FamilyDTO> family = familyService.getUserFamily(userId);
			if (family.isEmpty()) {
				throw new AppException(HttpStatus.BAD_REQUEST.value(),
						"User is not in a family to create resource in family level");
			}
			staticResourceDTO.setOwnerId(family.get().getId());
		}
		StaticResource staticResource = StaticResource.build(staticResourceDTO);
		StaticResource savedStaticResource = staticResourceRepository.save(staticResource);
		if (savedStaticResource == null) {
			throw new AppException("Error while adding static resource!");
		}
		return savedStaticResource.toDTO();
	}

	@Override
	public StaticResourceDTO addResource(String userId, MultipartFile resource, StaticResourceDTO.Visibility visibility)
			throws AppException {

		StaticResourceDTO.ContentType contentType = StaticResourceDTO.ContentType.getContentType(
				resource.getContentType());
		if (contentType == null) {
			throw new AppException(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
					"Unsupported Resource type " + resource.getContentType());
		}
		StaticResourceDTO staticResource = new StaticResourceDTO();
		try {
			staticResource.setType(contentType);
			staticResource.setName(resource.getName());
			staticResource.setData(resource.getBytes());
			staticResource.setVisibility(visibility);
		} catch (Exception e) {
			LOGGER.error("Error while parsing the resource", e);
			throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error while parsing the resource");
		}
		return addResource(userId, staticResource);
	}

	@Override
	@UserIdValidator(positions = 0)
	public void deleteResource(String userId, String resourceId) throws AppException {
		Optional<StaticResourceDTO> staticResource = getResource(userId, resourceId);
		if (staticResource.isEmpty()) {
			throw new AppException(HttpStatus.BAD_REQUEST.value(), "Static resource " + resourceId + " not found!");
		}
		String ownerId = userId;
		if (staticResource.get().getVisibility() == StaticResourceDTO.Visibility.FAMILY) {
			Optional<FamilyDTO> family = familyService.getUserFamily(userId);
			FamilyMemberDTO familyMember = familyService.getFamilyMember(userId,
					family.get().getId(), userId).get();
			/**
			 * Considering only that static resource will be added only behalf of the family
			 * in case of the family expense. So checking with family expense creation
			 * roles access.
			 */
			if (!familyService.getFamilySettings(userId, family.get().getId()).getFamilyExpenseRoles()
					.contains(familyMember.getRole())) {
				throw new AppException(HttpStatus.FORBIDDEN.value(),
						"You are not allowed delete this family's resources!");
			}
			ownerId = family.get().getId(); // Family exists validation will be done in getResource().
		}
		staticResourceRepository.deleteByIdAndOwnerId(resourceId, ownerId);
	}
}
