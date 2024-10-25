package com.vapps.expense.service;

import com.vapps.expense.annotation.ExpenseIdValidator;
import com.vapps.expense.annotation.Stats;
import com.vapps.expense.annotation.UserIdValidator;
import com.vapps.expense.common.dto.*;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.*;
import com.vapps.expense.model.Category;
import com.vapps.expense.model.Expense;
import com.vapps.expense.model.Family;
import com.vapps.expense.model.User;
import com.vapps.expense.repository.ExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

	@Autowired
	private ExpenseRepository expenseRepository;

	@Autowired
	private FamilyService familyService;

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private StaticResourceService staticResourceService;

	private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseServiceImpl.class);

	@Override
	@UserIdValidator(positions = 0)
	@Stats
	public ExpenseDTO addExpense(String userId, ExpenseCreationPayload payload, MultipartFile[] invoices)
			throws AppException {

		checkCurrency(payload.getCurrency());
		validateExpenseData(userId, payload.getCategoryId(), payload.getType(), payload.getFamilyId());
		checkExpenseAccess(userId, payload);

		Expense expense = new Expense();
		expense.setCreatedBy(User.build(userService.getUser(userId).get()));
		expense.setName(payload.getName());
		expense.setDescription(payload.getDescription());
		expense.setAmount(payload.getAmount());
		expense.setCurrency(payload.getCurrency());
		expense.setType(payload.getType());
		expense.setOwnerId(userId);
		if (payload.getCategoryId() != null) {
			expense.setCategory(Category.build(categoryService.getCategory(userId, payload.getCategoryId()).get()));
		}
		if (expense.getType() == ExpenseDTO.ExpenseType.FAMILY) {
			FamilyDTO family = familyService.getUserFamily(userId).get();
			expense.setOwnerId(family.getId());
			expense.setFamily(Family.build(family));
		}
		expense.setTime(payload.getTime());
		if (expense.getTime() == null) {
			expense.setTime(LocalDateTime.now());
		}
		if (payload.getType() == ExpenseDTO.ExpenseType.PERSONAL && payload.getFamilyId() != null) {
			expense.setFamily(Family.build(familyService.getFamilyById(userId, payload.getFamilyId()).get()));
		}

		if (invoices != null) {
			List<String> invoiceIds = new ArrayList<>();
			for (MultipartFile invoice : invoices) {
				StaticResourceDTO invoiceDTO = staticResourceService.addResource(userId, invoice,
						getStaticResourceVisibility(expense.getType()));
				invoiceIds.add(invoiceDTO.getId());
			}
			expense.setInvoices(invoiceIds);
		} else {
			expense.setInvoices(List.of());
		}

		expense = expenseRepository.save(expense);
		if (expense == null) {
			throw new AppException("Error while saving expense!");
		}
		return expense.toDTO();
	}

	@Override
	@UserIdValidator(positions = 0)
	@ExpenseIdValidator(userIdPosition = 0, positions = 1)
	@Stats(type = Stats.StatsType.EXPENSE_UPDATE)
	public ExpenseDTO updateExpense(String userId, String expenseId, ExpenseUpdatePayload payload,
			MultipartFile[] newInvoices) throws AppException {
		if (payload.getCurrency() != null) {
			checkCurrency(payload.getCurrency());
		}
		Expense expense = Expense.build(getExpense(userId, expenseId).get());

		if (payload.getName() != null) {
			expense.setName(payload.getName());
		}
		if (payload.getDescription() != null) {
			expense.setDescription(payload.getDescription());
		}
		if (payload.getTime() != null) {
			expense.setTime(payload.getTime());
		}
		if (payload.getCurrency() != null) {
			expense.setCurrency(payload.getCurrency());
		}
		if (payload.getAmount() > 0) {
			expense.setAmount(payload.getAmount());
		}
		if (payload.getCategoryId() != null && !expense.getCategory().getId().equals(payload.getCategoryId())) {
			validateExpenseData(userId, payload.getCategoryId(), expense.getType(), expense.getFamily().getId());
			expense.setCategory(Category.build(categoryService.getCategory(userId, payload.getCategoryId()).get()));
		}

		List<String> invoices = new ArrayList<>(expense.getInvoices());
		if (payload.getInvoices() != null) {
			for (String invoiceId : expense.getInvoices()) {
				if (!payload.getInvoices().contains(invoiceId)) {
					invoices.remove(invoiceId);
					staticResourceService.deleteResource(userId, invoiceId);
				}
			}
		}
		if (newInvoices != null) {
			for (MultipartFile newInvoice : newInvoices) {
				StaticResourceDTO newInvoiceDTO = staticResourceService
						.addResource(userId, newInvoice, getStaticResourceVisibility(expense.getType()));
				invoices.add(newInvoiceDTO.getId());
			}
		}
		expense.setInvoices(invoices);

		expense = expenseRepository.update(expense);
		if (expense == null) {
			throw new AppException("Error while updating expense!");
		}
		return expense.toDTO();
	}

	@Override
	@UserIdValidator(positions = 0)
	public Optional<ExpenseDTO> getExpense(String userId, String expenseId) {
		Optional<Expense> expense = expenseRepository.findByIdAndOwnerId(expenseId, userId);
		if (expense.isEmpty()) {
			Optional<FamilyDTO> familyDTO = familyService.getUserFamily(userId);
			if (familyDTO.isEmpty()) {
				return Optional.empty();
			}
			expense = expenseRepository.findByIdAndOwnerId(expenseId, familyDTO.get().getId());
		}
		return expense.map(Expense::toDTO);
	}

	@Override
	@UserIdValidator(positions = 0)
	@ExpenseIdValidator(userIdPosition = 0, positions = 1)
	@Stats(type = Stats.StatsType.EXPENSE_DELETE)
	public void deleteExpense(String userId, String expenseId) throws AppException {
		ExpenseDTO expense = getExpense(userId, expenseId).get();
		if (expense.getType() == ExpenseDTO.ExpenseType.FAMILY && !familyService.getFamilySettings(userId,
				expense.getFamily().getId()).getFamilyExpenseRoles().contains(familyService.getUserRoleInFamily(userId,
				expense.getOwnerId()))) {
			throw new AppException(HttpStatus.FORBIDDEN.value(), "You are not allowed to delete family's expense!");
		}
		expenseRepository.deleteById(expenseId);

		if (expense.getInvoices().isEmpty()) {
			return;
		}
		for (String invoiceId : expense.getInvoices()) {
			staticResourceService.deleteResource(userId, invoiceId);
			LOGGER.info("Deleted expense {}'s invoice {}", expenseId, invoiceId);
		}
	}

	@Override
	@UserIdValidator(positions = 0)
	public List<ExpenseDTO> getAllExpense(String userId, ExpenseFilter filter) throws AppException {
		List<Expense> expenses;
		if (filter.isFamily()) {
			Optional<FamilyDTO> family = familyService.getUserFamily(userId);
			if (family.isPresent()) {
				expenses = expenseRepository.findByFamilyId(family.get().getId());
			} else {
				expenses = List.of();
			}
		} else {
			expenses = expenseRepository.findByOwnerIdAndFamilyIsNull(userId);
		}

		if (filter.getStart() != null) {
			expenses = expenses.stream().filter(expense -> expense.getTime().isAfter(filter.getStart()))
					.collect(Collectors.toList());
		}
		if (filter.getEnd() != null) {
			expenses = expenses.stream().filter(expense -> expense.getTime().isBefore(filter.getEnd()))
					.collect(Collectors.toList());
		}
		if (filter.getQuery() != null) {
			if (filter.getSearchBy() == null) {
				filter.setSearchBy(ExpenseFilter.SearchBy.ALL);
			}
			expenses = filterExpensesBySearch(userId, expenses, filter.getQuery(), filter.getSearchBy());
		}
		return expenses.stream().map(Expense::toDTO).collect(Collectors.toList());
	}

	private List<Expense> filterExpensesBySearch(String userId, List<Expense> expenses, String query,
			ExpenseFilter.SearchBy searchBy) throws AppException {

		switch (searchBy) {
			case NAME -> expenses = expenses.stream().filter(expense -> isNameMatches(query, expense))
					.collect(Collectors.toList());
			case DESCRIPTION -> expenses = expenses.stream()
					.filter(expense -> isDescriptionMatches(query, expense)).collect(Collectors.toList());
			case OWNER -> expenses = expenses.stream().filter(expense -> isOwnerMatches(userId, query, expense))
					.collect(Collectors.toList());
			case CATEGORY -> expenses = expenses.stream()
					.filter(expense -> isCategoryMatches(query, expense)).collect(Collectors.toList());
			case ALL -> expenses = expenses.stream()
					.filter(expense -> isNameMatches(query, expense) || isDescriptionMatches(query,
							expense) || isOwnerMatches(userId, query, expense) || isCategoryMatches(query, expense))
					.collect(Collectors.toList());
			default -> throw new AppException("Search by option " + searchBy + " not implemented!");
		}
		LOGGER.info("Search Option: {}", searchBy);
		return expenses;
	}

	private boolean isNameMatches(String query, Expense expense) {
		return expense.getName().toLowerCase().contains(query.toLowerCase());
	}

	private boolean isDescriptionMatches(String query, Expense expense) {
		return expense.getDescription().toLowerCase().contains(query.toLowerCase());
	}

	private boolean isOwnerMatches(String userId, String query, Expense expense) {
		try {
			if (expense.getType() == ExpenseDTO.ExpenseType.FAMILY) {
				FamilyDTO family = familyService.getFamilyById(userId, expense.getOwnerId()).get();
				return family.getName().toLowerCase().contains(query.toLowerCase());
			}
			UserDTO owner = userService.getUser(expense.getOwnerId()).get();
			return owner.getName().toLowerCase().contains(query.toLowerCase());
		} catch (AppException ex) {
			LOGGER.error("Error while filtering expenses by owner, At expense {} for user {}", expense.getId(),
					userId);
		}
		return false;
	}

	private boolean isCategoryMatches(String query, Expense expense) {
		return expense.getCategory() != null && expense.getCategory().getName().toLowerCase()
				.contains(query.toLowerCase());
	}

	private void checkCurrency(String currency) throws AppException {
		if (Currency.getAvailableCurrencies().stream().noneMatch(curr -> curr.getCurrencyCode().equals(currency))) {
			throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invalid currency " + currency);
		}
	}

	private void checkExpenseAccess(String userId, ExpenseCreationPayload expense) throws AppException {
		if (expense.getType() == ExpenseDTO.ExpenseType.FAMILY) {
			Optional<FamilyDTO> familyDTO = familyService.getUserFamily(userId);
			if (familyDTO.isEmpty()) {
				throw new AppException(HttpStatus.BAD_REQUEST.value(),
						"You should be in a family to create family type expense!");
			}
			if (familyService.getUserRoleInFamily(userId, familyDTO.get().getId()) == FamilyMemberDTO.Role.MEMBER) {
				throw new AppException(HttpStatus.FORBIDDEN.value(),
						"You are not allowed to add expense in behalf of your family!");
			}
		} else if (expense.getFamilyId() != null && familyService.getFamilyById(userId, expense.getFamilyId())
				.isEmpty()) {
			throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invalid family id " + expense.getFamilyId());
		}
	}

	private void validateExpenseData(String userId, String categoryId, ExpenseDTO.ExpenseType type, String familyId)
			throws AppException {
		Optional<CategoryDTO> category = categoryService.getCategory(userId, categoryId);
		if (categoryId != null && category.isEmpty()) {
			throw new AppException(HttpStatus.BAD_REQUEST.value(), "Category not exists!");
		}
		Optional<FamilyDTO> family = familyService.getUserFamily(userId);
		if (type == ExpenseDTO.ExpenseType.FAMILY) {
			if (family.isEmpty()) {
				throw new AppException(HttpStatus.BAD_REQUEST.value(), "You should be in a family");
			}
			if (category.isPresent() && !category.get().getOwnerId().equals(family.get().getId())) {
				throw new AppException(HttpStatus.BAD_REQUEST.value(), "Given category not belongs to the family!");
			}
		} else {
			if (familyId != null && (family.isEmpty() || !family.get().getId().equals(familyId))) {
				throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invalid familyId");
			}
			if (category.isPresent() && category.get()
					.getType() == CategoryDTO.CategoryType.FAMILY && familyId == null) {
				throw new AppException(HttpStatus.BAD_REQUEST.value(),
						"Only personal categories can be used for personal expenses!");
			}
			if (familyId != null && category.isPresent() && !category.get().getOwnerId().equals(familyId)) {
				throw new AppException(HttpStatus.BAD_REQUEST.value(), "Given category not belongs to the family!");
			}
		}
	}

	private StaticResourceDTO.Visibility getStaticResourceVisibility(ExpenseDTO.ExpenseType type) {
		return type == ExpenseDTO.ExpenseType.PERSONAL
				? StaticResourceDTO.Visibility.PRIVATE : StaticResourceDTO.Visibility.FAMILY;
	}
}
