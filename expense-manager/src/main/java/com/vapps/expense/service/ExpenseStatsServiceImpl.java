package com.vapps.expense.service;

import com.vapps.expense.annotation.UserIdValidator;
import com.vapps.expense.common.dto.ExpenseDTO;
import com.vapps.expense.common.dto.ExpenseStatsDTO;
import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.dto.FamilyMemberDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.ExpenseService;
import com.vapps.expense.common.service.ExpenseStatsService;
import com.vapps.expense.common.service.FamilyService;
import com.vapps.expense.model.ExpenseStats;
import com.vapps.expense.repository.ExpenseStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		stats.setAmountSpentPerDay(Map.of());
		stats = expenseStatsRepository.save(stats);
		if (stats == null) {
			throw new AppException("Error while saving Expense Stats");
		}
		return stats.toDTO();
	}

	@Override
	@UserIdValidator(positions = 0)
	public Optional<ExpenseStatsDTO> getStats(String userId, String ownerId, ExpenseStatsDTO.ExpenseStatsType type)
			throws AppException {
		if (type == ExpenseStatsDTO.ExpenseStatsType.FAMILY) {
			Optional<FamilyDTO> family = familyService.getUserFamily(userId);
			if (family.isEmpty() || !family.get().getId().equals(ownerId)) {
				throw new AppException("Invalid family id for getting stats");
			}
		}
		Optional<ExpenseStats> statsOptional = expenseStatsRepository.findByOwnerIdAndType(ownerId, type);
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
		stats.getAmountSpentPerDay().put(date,
				stats.getAmountSpentPerDay().containsKey(date) ? stats.getAmountSpentPerDay()
						.get(date) + expense.getAmount() : expense.getAmount());
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
}
