package com.vapps.expense.common.service;

import com.vapps.expense.common.dto.InvitationDTO;
import com.vapps.expense.common.exception.AppException;

public interface InvitationService {

    InvitationDTO addInvitation(String userId, InvitationDTO invitation) throws AppException;

}
