package com.vapps.expense.common.service;

import com.vapps.expense.common.exception.AppException;

public interface EmailService {

    void sendEmail(String to, String subject, String content) throws AppException;
}
