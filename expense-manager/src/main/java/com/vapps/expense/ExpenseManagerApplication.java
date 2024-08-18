package com.vapps.expense;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableCaching
@EnableAspectJAutoProxy
@ComponentScan(basePackages = { "com.vapps.security", "com.vapps.expense" })
@EnableMongoRepositories
public class ExpenseManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpenseManagerApplication.class, args);
	}

}
