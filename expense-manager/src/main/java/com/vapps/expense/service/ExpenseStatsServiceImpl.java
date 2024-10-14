package com.vapps.expense.service;

import com.vapps.expense.annotation.UserIdValidator;
import com.vapps.expense.common.dto.*;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.ExpenseService;
import com.vapps.expense.common.service.ExpenseStatsService;
import com.vapps.expense.common.service.FamilyService;
import com.vapps.expense.model.ExpenseStats;
import com.vapps.expense.repository.ExpenseStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
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
		stats.setTopUsers(List.of());
		stats.setTopCategories(List.of());
		Map<DayOfWeek, Long> weekAmount = new HashMap<>();
		for (DayOfWeek weekDay : DayOfWeek.values()) {
			weekAmount.put(weekDay, 0L);
		}
		resetWeekDayAmountSpent(weekAmount);
		stats.setWeekAmount(weekAmount);
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

		if (expenseService.getExpense(expense.getCreatedBy().getId(), expense.getId()).isEmpty()) {
			throw new AppException("Expense not found");
		}
		ExpenseStatsDTO stats = getStats(expense.getCreatedBy().getId(),
				expense.getFamily() != null ? expense.getFamily().getId() : expense.getOwnerId(),
				expense.getFamily() != null
						? ExpenseStatsDTO.ExpenseStatsType.FAMILY
						: ExpenseStatsDTO.ExpenseStatsType.PERSONAL).get();

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
		updateStats(stats);
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
}
