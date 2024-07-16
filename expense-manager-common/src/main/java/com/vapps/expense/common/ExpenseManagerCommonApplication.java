package com.vapps.expense.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ExpenseManagerCommonApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpenseManagerCommonApplication.class, args);
	}

}
