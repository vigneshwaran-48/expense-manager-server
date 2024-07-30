package com.vapps.expense.common.service;

import com.vapps.expense.common.exception.AppException;
import org.thymeleaf.context.Context;

public interface EmailService {

    void sendEmail(String to, String subject, String template, Context context) throws AppException;
}
