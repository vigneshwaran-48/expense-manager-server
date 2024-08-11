package com.vapps.expense.controller;

import com.vapps.expense.common.dto.CategoryDTO;
import com.vapps.expense.common.dto.response.CategoryResponse;
import com.vapps.expense.common.dto.response.Response;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.CategoryService;
import com.vapps.expense.common.util.Endpoints;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping(Endpoints.CATEGORY_API)
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> addCategory(@RequestBody CategoryDTO category, Principal principal,
            HttpServletRequest request) throws AppException {

        String userId = principal.getName();
        CategoryDTO categoryDTO = categoryService.addCategory(userId, category);

        return ResponseEntity.ok(new CategoryResponse(HttpStatus.OK.value(), "Created Category!", LocalDateTime.now(),
                request.getServletPath(), categoryDTO));
    }

    @PatchMapping(Endpoints.UPDATE_CATEGORY_PATH)
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable String id, @RequestBody CategoryDTO category,
            Principal principal, HttpServletRequest request) throws AppException {

        String userId = principal.getName();
        CategoryDTO categoryDTO = categoryService.updatedCategory(userId, id, category);

        return ResponseEntity.ok(new CategoryResponse(HttpStatus.OK.value(), "Updated Category!", LocalDateTime.now(),
                request.getServletPath(), categoryDTO));
    }

    @GetMapping(Endpoints.GET_CATEGORY_PATH)
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable String id, Principal principal,
            HttpServletRequest request) throws AppException {

        String userId = principal.getName();
        Optional<CategoryDTO> categoryDTO = categoryService.getCategory(userId, id);

        if (categoryDTO.isEmpty()) {
            throw new AppException(HttpStatus.NOT_FOUND.value(), "Category " + id + " not found!");
        }

        return ResponseEntity.ok(
                new CategoryResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(), request.getServletPath(),
                        categoryDTO.get()));
    }

    @DeleteMapping(Endpoints.DELETE_CATEGORY_PATH)
    public ResponseEntity<Response> deleteCategory(@PathVariable String id, Principal principal,
            HttpServletRequest request) throws AppException {

        String userId = principal.getName();
        categoryService.deleteCategory(userId, id);

        return ResponseEntity.ok(
                new Response(HttpStatus.OK.value(), "Delete category!", LocalDateTime.now(), request.getServletPath()));
    }
}
