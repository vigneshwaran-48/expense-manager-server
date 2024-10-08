package com.vapps.expense.controller;

import com.vapps.expense.common.dto.ExpenseStatsDTO;
import com.vapps.expense.common.dto.response.ExpenseStatsResponse;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.ExpenseStatsService;
import com.vapps.expense.common.util.Endpoints;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequestMapping(Endpoints.STATS_API)
public class StatsController {

	@Autowired
	private ExpenseStatsService statsService;

	@GetMapping(Endpoints.GET_FAMILY_STATS_PATH)
	public ResponseEntity<ExpenseStatsResponse> getFamilyStats(Principal principal, HttpServletRequest request)
			throws AppException {

		String userId = principal.getName();
		ExpenseStatsDTO stats = statsService.getFamilyStats(userId).get();

		return ResponseEntity.ok(
				new ExpenseStatsResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(),
						request.getServletPath(),
						stats));
	}

	@GetMapping(Endpoints.GET_PERSONAL_STATS_PATH)
	public ResponseEntity<ExpenseStatsResponse> getPersonalStats(Principal principal, HttpServletRequest request)
			throws AppException {

		String userId = principal.getName();
		ExpenseStatsDTO stats = statsService.getPersonalStats(userId).get();

		return ResponseEntity.ok(
				new ExpenseStatsResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(),
						request.getServletPath(),
						stats));
	}
}
