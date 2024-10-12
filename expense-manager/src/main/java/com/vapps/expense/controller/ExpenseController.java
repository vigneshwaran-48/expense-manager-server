package com.vapps.expense.controller;

import com.vapps.expense.common.dto.ExpenseCreationPayload;
import com.vapps.expense.common.dto.ExpenseDTO;
import com.vapps.expense.common.dto.ExpenseFilter;
import com.vapps.expense.common.dto.ExpenseUpdatePayload;
import com.vapps.expense.common.dto.response.ExpenseResponse;
import com.vapps.expense.common.dto.response.ExpensesResponse;
import com.vapps.expense.common.dto.response.Response;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.ExpenseService;
import com.vapps.expense.common.service.ExpenseStatsService;
import com.vapps.expense.common.util.Endpoints;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(Endpoints.EXPENSE_API)
@CrossOrigin("*")
public class ExpenseController {

	@Autowired
	private ExpenseService expenseService;

	private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseController.class);

	@PostMapping
	public ResponseEntity<ExpenseResponse> createExpense(
			@RequestPart(value = "invoices", required = false) MultipartFile[] invoices,
			@RequestPart("payload") ExpenseCreationPayload payload, Principal principal, HttpServletRequest request)
			throws AppException {
		String userId = principal.getName();
		ExpenseDTO expense = null;
		expense = expenseService.addExpense(userId, payload, invoices);
		return ResponseEntity.ok(new ExpenseResponse(HttpStatus.OK.value(), "Created Expense!", LocalDateTime.now(),
				request.getServletPath(), expense));
	}

	@PatchMapping(Endpoints.UPDATE_EXPENSE_PATH)
	public ResponseEntity<ExpenseResponse> updateExpense(@PathVariable String id,
			@RequestPart(value = "invoices", required = false) MultipartFile[] invoices,
			@RequestPart(value = "payload") ExpenseUpdatePayload payload, Principal principal,
			HttpServletRequest request) throws AppException {
		String userId = principal.getName();
		ExpenseDTO expense = expenseService.updateExpense(userId, id, payload, invoices);
		return ResponseEntity.ok(new ExpenseResponse(HttpStatus.OK.value(), "Updated Expense!", LocalDateTime.now(),
				request.getServletPath(), expense));
	}

	@GetMapping(Endpoints.GET_EXPENSE_PATH)
	public ResponseEntity<ExpenseResponse> getExpense(@PathVariable String id, Principal principal,
			HttpServletRequest request) throws AppException {
		String userId = principal.getName();
		Optional<ExpenseDTO> expense = expenseService.getExpense(userId, id);
		if (expense.isEmpty()) {
			throw new AppException(HttpStatus.BAD_REQUEST.value(), "Expense " + id + " not found!");
		}
		return ResponseEntity.ok(
				new ExpenseResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(), request.getServletPath(),
						expense.get()));
	}

	@DeleteMapping(Endpoints.DELETE_EXPENSE_PATH)
	public ResponseEntity<Response> deleteExpense(@PathVariable String id, Principal principal,
			HttpServletRequest request) throws AppException {
		String userId = principal.getName();
		expenseService.deleteExpense(userId, id);
		return ResponseEntity.ok(new Response(HttpStatus.OK.value(), "Delete the expense!", LocalDateTime.now(),
				request.getServletPath()));
	}

	@GetMapping
	public ResponseEntity<ExpensesResponse> getAllExpenses(
			@RequestParam(required = false, defaultValue = "true") boolean isFamily,
			@RequestParam(required = false) LocalDateTime start, @RequestParam(required = false) LocalDateTime end,
			@RequestParam(required = false) String query,
			@RequestParam(required = false, defaultValue = "ALL") ExpenseFilter.SearchBy searchBy, Principal principal,
			HttpServletRequest request) throws AppException {

		String userId = principal.getName();
		ExpenseFilter filter = new ExpenseFilter();
		filter.setQuery(query);
		filter.setEnd(end);
		filter.setStart(start);
		filter.setFamily(isFamily);
		filter.setSearchBy(searchBy);
		LOGGER.info(filter.toString());
		List<ExpenseDTO> expenses = expenseService.getAllExpense(userId, filter);

		return ResponseEntity.ok(
				new ExpensesResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(), request.getServletPath(),
						expenses));
	}
}
