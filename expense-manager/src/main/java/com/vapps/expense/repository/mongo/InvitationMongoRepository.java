package com.vapps.expense.repository.mongo;

import com.vapps.expense.common.dto.InvitationDTO;
import com.vapps.expense.model.Invitation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface InvitationMongoRepository extends MongoRepository<Invitation, String> {

    Optional<Invitation> findByRecipientIdAndFromIdAndType(String recipientId, String fromId, InvitationDTO.Type type);

}
