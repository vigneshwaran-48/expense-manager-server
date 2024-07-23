package com.vapps.expense;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootTest
@EnableCaching
@EnableAspectJAutoProxy
@ComponentScan(basePackages = { "com.vapps.security", "com.vapps.expense" })
class ExpenseManagerApplicationTests {

	@Test
	void contextLoads() {
	}

}
