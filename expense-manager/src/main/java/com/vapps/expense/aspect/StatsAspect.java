package com.vapps.expense.aspect;

import com.vapps.expense.annotation.Stats;
import com.vapps.expense.common.service.ExpenseStatsService;
import org.aspectj.lang.annotation.After;
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
	public void stats(Object result, Stats stats) {

		LOGGER.info("Got entity: {}", result);
	}
}
