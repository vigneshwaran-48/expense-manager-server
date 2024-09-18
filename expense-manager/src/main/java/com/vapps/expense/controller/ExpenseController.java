package com.vapps.expense.controller;

import com.vapps.expense.common.dto.ExpenseCreationPayload;
import com.vapps.expense.common.dto.ExpenseDTO;
import com.vapps.expense.common.dto.ExpenseUpdatePayload;
import com.vapps.expense.common.dto.response.ExpenseResponse;
import com.vapps.expense.common.dto.response.Response;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.ExpenseService;
import com.vapps.expense.common.util.Endpoints;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping(Endpoints.EXPENSE_API)
@CrossOrigin("*")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(@RequestPart(value = "invoices", required = false) MultipartFile[] invoices,
                                                         @RequestPart("payload") ExpenseCreationPayload payload,
                                                         Principal principal, HttpServletRequest request) throws AppException {
        String userId = principal.getName();
        ExpenseDTO expense = null;
        expense = expenseService.addExpense(userId, payload, invoices);
        return ResponseEntity.ok(new ExpenseResponse(HttpStatus.OK.value(), "Created Expense!", LocalDateTime.now(),
                request.getServletPath(), expense));
    }

    @PatchMapping(Endpoints.UPDATE_EXPENSE_PATH)
    public ResponseEntity<ExpenseResponse> updateExpense(@PathVariable String id,
                                                         @RequestPart(value = "invoices", required = false) MultipartFile[] invoices,
                                                         @RequestPart(value = "payload") ExpenseUpdatePayload payload, Principal principal, HttpServletRequest request)
            throws AppException {
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
}
