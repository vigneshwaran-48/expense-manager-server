package com.vapps.expense.repository.mongo;

import com.vapps.expense.common.dto.InvitationDTO;
import com.vapps.expense.model.Invitation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InvitationMongoRepository extends MongoRepository<Invitation, String> {

	Optional<Invitation> findByRecipientIdAndFromIdAndType(String recipientId, String fromId, InvitationDTO.Type type);

	List<Invitation> findByRecipientId(String recipientId);

	List<Invitation> findByFromId(String fromId);

	@Query("{'id': ?0,$or: [{ 'recipientId': ?1 }, {'fromId': ?2}]}")
	Optional<Invitation> findByIdAndRecipientIdOrFromId(String id, String recipientId, String fromId);
}
