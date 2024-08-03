package com.vapps.expense.repository;

import com.vapps.expense.common.dto.InvitationDTO;
import com.vapps.expense.model.Invitation;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository {

    Invitation save(Invitation invitation);

    Optional<Invitation> findById(String id);

    void deleteById(String id);

    Optional<Invitation> findByRecipientIdAndFromIdAndType(String recipientId, String fromId, InvitationDTO.Type type);

    List<Invitation> findByRecipientId(String recipientId);
}
