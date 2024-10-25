package com.vapps.expense.service;

import com.vapps.expense.annotation.UserIdValidator;
import com.vapps.expense.common.dto.*;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.ExpenseService;
import com.vapps.expense.common.service.ExpenseStatsService;
import com.vapps.expense.common.service.FamilyService;
import com.vapps.expense.model.Category;
import com.vapps.expense.model.ExpenseStats;
import com.vapps.expense.repository.ExpenseStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseStatsServiceImpl implements ExpenseStatsService {

	@Autowired
	private ExpenseStatsRepository expenseStatsRepository;

	@Autowired
	private FamilyService familyService;

	@Autowired
	private ExpenseService expenseService;

	private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseStatsServiceImpl.class);

	@Override
	@UserIdValidator(positions = 0)
	public ExpenseStatsDTO createStats(String userId, String ownerId, ExpenseStatsDTO.ExpenseStatsType type)
			throws AppException {
		if (expenseStatsRepository.findByOwnerIdAndType(ownerId, type).isPresent()) {
			throw new AppException("Stats already present");
		}
		if (type == ExpenseStatsDTO.ExpenseStatsType.PERSONAL && !userId.equals(ownerId)) {
			throw new AppException("Personal type stats creation for other users!");
		}
		if (type == ExpenseStatsDTO.ExpenseStatsType.FAMILY) {
			Optional<FamilyDTO> family = familyService.getUserFamily(userId);
			if (family.isEmpty() || !family.get().getId().equals(ownerId) || familyService.getUserRoleInFamily(userId,
					family.get().getId()) != FamilyMemberDTO.Role.LEADER) {
				throw new AppException("Invalid owner! Invalid family Id for owner or the user is not a leader!");
			}
		}
		ExpenseStats stats = new ExpenseStats();
		stats.setOwnerId(ownerId);
		stats.setType(type);
		stats.setRecentExpenses(List.of());
		Map<DayOfWeek, Long> weekAmount = new HashMap<>();
		for (DayOfWeek weekDay : DayOfWeek.values()) {
			weekAmount.put(weekDay, 0L);
		}
		resetWeekDayAmountSpent(weekAmount);
		stats.setWeekAmount(weekAmount);
		stats.setCategoryAmount(Map.of());
		stats.setUserAmount(Map.of());
		stats = expenseStatsRepository.save(stats);
		if (stats == null) {
			throw new AppException("Error while saving Expense Stats");
		}
		return stats.toDTO();
	}

	@Override
	@UserIdValidator(positions = 0)
	public Optional<ExpenseStatsDTO> getPersonalStats(String userId) throws AppException {
		return getStats(userId, userId, ExpenseStatsDTO.ExpenseStatsType.PERSONAL);
	}

	@Override
	public Optional<ExpenseStatsDTO> getFamilyStats(String userId) throws AppException {

		Optional<FamilyDTO> family = familyService.getUserFamily(userId);
		if (family.isEmpty()) {
			throw new AppException("Not part of any family to get family stats!");
		}
		return getStats(userId, family.get().getId(), ExpenseStatsDTO.ExpenseStatsType.FAMILY);
	}

	@Override
	public void addExpense(ExpenseDTO expense) throws AppException {

		checkExpenseExists(expense);
		ExpenseStatsDTO stats = getStatsForExpense(expense);

		LocalDate date = expense.getTime().toLocalDate();
		Map<DayOfWeek, Long> weekAmount = stats.getWeekAmount();
		weekAmount.put(date.getDayOfWeek(), expense.getAmount());
		stats.setWeekAmount(weekAmount);
		stats.getRecentExpenses().add(expense);
		stats.setRecentExpenses(stats.getRecentExpenses().stream().sorted(Comparator.comparing(ExpenseDTO::getTime))
				.collect(Collectors.toList()));
		if (stats.getRecentExpenses().size() > 5) {
			stats.getRecentExpenses().remove(0);
		}

		// Category based amount spent stats.
		if (expense.getCategory() != null) {
			Map<String, Long> categoryAmount = stats.getCategoryAmount();
			categoryAmount.put(expense.getCategory().getId(),
					categoryAmount.containsKey(expense.getCategory().getId()) ? stats.getCategoryAmount()
							.get(expense.getCategory().getId()) + expense.getAmount() : expense.getAmount());
			stats.setCategoryAmount(categoryAmount);
		}

		// User Amount spent stats.
		if (expense.getType() == ExpenseDTO.ExpenseType.PERSONAL) {
			long oldSpentAmount = stats.getUserAmount().containsKey(expense.getOwnerId()) ? stats.getUserAmount()
					.get(expense.getOwnerId()) + expense.getAmount() : expense.getAmount();
			stats.getUserAmount().put(expense.getOwnerId(), oldSpentAmount);
		}
		updateStats(stats);
	}

	@Override
	public void updateExpense(ExpenseDTO expense) throws AppException {
		checkExpenseExists(expense);
		ExpenseStatsDTO stats = getStatsForExpense(expense);
		checkAndUpdateWeekStats(stats, expense);
		checkAndUpdateCategoryStats(stats, expense);
	}

	private void checkAndUpdateCategoryStats(ExpenseStatsDTO stats, ExpenseDTO expense) throws AppException {

	}

	private void checkAndUpdateWeekStats(ExpenseStatsDTO stats, ExpenseDTO expense) throws AppException {
		LocalDateTime weekStart = LocalDateTime.now().with(DayOfWeek.SUNDAY);
		LocalDateTime weekEnd = LocalDateTime.now().with(DayOfWeek.SATURDAY);

		if (expense.getTime().isBefore(weekStart) || expense.getTime().isAfter(weekEnd)) {
			return;
		}

		ExpenseFilter filter = new ExpenseFilter();
		filter.setStart(LocalDateTime.now().with(DayOfWeek.SUNDAY));
		filter.setEnd(LocalDateTime.now().with(DayOfWeek.SATURDAY));
		filter.setFamily(expense.getFamily() != null);
		List<ExpenseDTO> expenses = expenseService.getAllExpense(expense.getCreatedBy().getId(),
				filter);
		for (Map.Entry<DayOfWeek, Long> entry : stats.getWeekAmount().entrySet()) {
			stats.getWeekAmount().put(entry.getKey(), 0L);
		}
		for (ExpenseDTO currExpense : expenses) {
			DayOfWeek expenseDayOfWeek = currExpense.getTime().getDayOfWeek();
			stats.getWeekAmount()
					.put(expenseDayOfWeek, stats.getWeekAmount().get(expenseDayOfWeek) + currExpense.getAmount());
		}
	}

	private ExpenseStatsDTO updateStats(ExpenseStatsDTO statsDTO) throws AppException {
		ExpenseStats stats = ExpenseStats.build(statsDTO);
		stats = expenseStatsRepository.update(stats);
		if (stats == null) {
			throw new AppException("Error while updating expense");
		}
		return stats.toDTO();
	}

	private Optional<ExpenseStatsDTO> getStats(String userId, String ownerId, ExpenseStatsDTO.ExpenseStatsType type)
			throws AppException {
		Optional<ExpenseStats> statsOptional = expenseStatsRepository.findByOwnerIdAndType(ownerId,
				type);
		ExpenseStatsDTO statsDTO = null;
		if (statsOptional.isEmpty()) {
			// For migrating old users, Need to remove this.
			statsDTO = createStats(userId, ownerId, type);
		}
		if (statsOptional.isPresent()) {
			return Optional.of(statsOptional.get().toDTO());
		}
		return statsDTO != null ? Optional.of(statsDTO) : Optional.empty();
	}

	private void resetWeekDayAmountSpent(Map<DayOfWeek, Long> weekAmount) {
		DayOfWeek currentDayOfWeek = LocalDate.now().getDayOfWeek();
		switch (currentDayOfWeek) {
			case SUNDAY -> {
				weekAmount.put(DayOfWeek.MONDAY, 0L);
				weekAmount.put(DayOfWeek.TUESDAY, 0L);
				weekAmount.put(DayOfWeek.WEDNESDAY, 0L);
				weekAmount.put(DayOfWeek.THURSDAY, 0L);
				weekAmount.put(DayOfWeek.FRIDAY, 0L);
				weekAmount.put(DayOfWeek.SATURDAY, 0L);
			}
			case MONDAY -> {
				weekAmount.put(DayOfWeek.TUESDAY, 0L);
				weekAmount.put(DayOfWeek.WEDNESDAY, 0L);
				weekAmount.put(DayOfWeek.THURSDAY, 0L);
				weekAmount.put(DayOfWeek.FRIDAY, 0L);
				weekAmount.put(DayOfWeek.SATURDAY, 0L);
			}
			case TUESDAY -> {
				weekAmount.put(DayOfWeek.WEDNESDAY, 0L);
				weekAmount.put(DayOfWeek.THURSDAY, 0L);
				weekAmount.put(DayOfWeek.FRIDAY, 0L);
				weekAmount.put(DayOfWeek.SATURDAY, 0L);
			}
			case WEDNESDAY -> {
				weekAmount.put(DayOfWeek.THURSDAY, 0L);
				weekAmount.put(DayOfWeek.FRIDAY, 0L);
				weekAmount.put(DayOfWeek.SATURDAY, 0L);
			}
			case THURSDAY -> {
				weekAmount.put(DayOfWeek.FRIDAY, 0L);
				weekAmount.put(DayOfWeek.SATURDAY, 0L);
			}
			case FRIDAY -> {
				weekAmount.put(DayOfWeek.SATURDAY, 0L);
			}
		}
	}

	private void checkExpenseExists(ExpenseDTO expense) throws AppException {
		if (expenseService.getExpense(expense.getOwnerId(), expense.getId()).isEmpty()) {
			throw new AppException("Expense not exists!");
		}
	}

	private ExpenseStatsDTO getStatsForExpense(ExpenseDTO expense) throws AppException {

		return getStats(expense.getCreatedBy().getId(),
				expense.getFamily() != null ? expense.getFamily().getId() : expense.getOwnerId(),
				expense.getFamily() != null
						? ExpenseStatsDTO.ExpenseStatsType.FAMILY
						: ExpenseStatsDTO.ExpenseStatsType.PERSONAL).get();
	}

}
