package com.vapps.expense.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Stats {
	enum StatsType {
		EXPENSE_ADD,
		EXPENSE_UPDATE,
		EXPENSE_DELETE
	}

	StatsType type() default StatsType.EXPENSE_ADD;
}
