package com.vapps.expense.repository.cache;

import com.vapps.expense.common.dto.InvitationDTO;
import com.vapps.expense.model.Invitation;
import com.vapps.expense.repository.InvitationRepository;
import com.vapps.expense.repository.mongo.InvitationMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InvitationCacheRepository implements InvitationRepository {

    @Autowired
    private InvitationMongoRepository invitationRepository;

    @Override
    public Invitation save(Invitation invitation) {
        return invitationRepository.save(invitation);
    }

    @Override
    @Cacheable(value = "invitation", key = "'invitation_' + #id")
    public Optional<Invitation> findById(String id) {
        return invitationRepository.findById(id);
    }

    @Override
    @CacheEvict(value = "invitation", key = "'invitation_' + #id")
    public void deleteById(String id) {
        invitationRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = "invitation", key = "'invitation_' + #recipientId + '_' + #fromId + '_' + #type")
    public Optional<Invitation> findByRecipientIdAndFromIdAndType(String recipientId, String fromId,
            InvitationDTO.Type type) {
        return invitationRepository.findByRecipientIdAndFromIdAndType(recipientId, fromId, type);
    }

    @Override
    @Cacheable(value = "invitation", key = "'user_all_invitation_' + #recipientId")
    public List<Invitation> findByRecipientId(String recipientId) {
        return invitationRepository.findByRecipientId(recipientId);
    }
}
