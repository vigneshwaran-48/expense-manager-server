package com.vapps.expense.aspect;

import com.vapps.expense.annotation.Stats;
import com.vapps.expense.common.dto.ExpenseDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.ExpenseStatsService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class StatsAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(StatsAspect.class);

	@Autowired
	private ExpenseStatsService statsService;

	@AfterReturning(value = "@annotation(stats)", returning = "result")
	public void stats(Object result, Stats stats) throws AppException {

		if (!(result instanceof ExpenseDTO)) {
			return;
		}

		Thread statsProcessThread = new Thread(() -> {
			try {
				processStats((ExpenseDTO) result, stats);
			} catch (AppException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		});
		statsProcessThread.start();
	}

	private void processStats(ExpenseDTO expense, Stats stats) throws AppException {
		statsService.addExpense(expense);
	}
}
