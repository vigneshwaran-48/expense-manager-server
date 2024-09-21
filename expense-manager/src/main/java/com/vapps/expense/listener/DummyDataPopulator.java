package com.vapps.expense.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.dto.UserDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.FamilyService;
import com.vapps.expense.common.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Component
public class DummyDataPopulator {

	@Autowired
	private UserService userService;

	@Autowired
	private FamilyService familyService;

	@Autowired
	private ObjectMapper objectMapper;

	private static final Logger LOGGER = LoggerFactory.getLogger(DummyDataPopulator.class);

	@EventListener(ContextRefreshedEvent.class)
	public void onEvent() {
		try {
			populateDummyUsers();
			populateDummyFamilies();
		} catch (AppException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
	}

	private void populateDummyUsers() throws AppException {
		InputStream inputStream = getClass().getResourceAsStream("/data/users.json");
		try {
			List<UserDTO> users = Arrays.asList(objectMapper.readValue(inputStream, UserDTO[].class));
			for (UserDTO user : users) {
				if (userService.getUser(user.getId()).isPresent()) {
					continue;
				}
				userService.addUser(user);
			}
		} catch (Exception e) {
			throw new AppException("Error while creating dummy users!");
		}
	}

	private void populateDummyFamilies() throws AppException {
		InputStream inputStream = getClass().getResourceAsStream("/data/families.json");
		try {
			List<FamilyDTO> families = Arrays.asList(objectMapper.readValue(inputStream, FamilyDTO[].class));
			for (FamilyDTO family : families) {
				if (familyService.getFamilyById(family.getCreatedBy().getId(), family.getId()).isPresent()) {
					continue;
				}
				familyService.createFamily(family.getCreatedBy().getId(), family);
			}
		} catch (Exception e) {
			throw new AppException("Error while creating dummy families!");
		}
	}
}
