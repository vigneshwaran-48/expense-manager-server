package com.vapps.expense.repository.cache;

import com.vapps.expense.common.dto.InvitationDTO;
import com.vapps.expense.model.Invitation;
import com.vapps.expense.repository.InvitationRepository;
import com.vapps.expense.repository.mongo.InvitationMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InvitationCacheRepository implements InvitationRepository {

    @Autowired
    private InvitationMongoRepository invitationRepository;

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "invitationFromId", allEntries = true),
                    @CacheEvict(value = "invitationRecipient", allEntries = true),
                    @CacheEvict(value = "invitationRecipientFromType", allEntries = true)
            }
    )
    public Invitation save(Invitation invitation) {
        return invitationRepository.save(invitation);
    }

    @Override
    @Cacheable(value = "invitation", key = "'invitation_' + #id")
    public Optional<Invitation> findById(String id) {
        return invitationRepository.findById(id);
    }

    @Override
    @Caching(
            evict = {@CacheEvict(value = "invitation", key = "'invitation_' + #id"),
                    @CacheEvict(value = "invitationFromId", allEntries = true),
                    @CacheEvict(value = "invitationRecipient", allEntries = true),
                    @CacheEvict(value = "invitationRecipientFromType", allEntries = true)
            }
    )
    public void deleteById(String id) {
        invitationRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = "invitationRecipientFromType", key = "'invitation_' + #recipientId + '_' + #fromId + '_' + #type")
    public Optional<Invitation> findByRecipientIdAndFromIdAndType(String recipientId, String fromId,
                                                                  InvitationDTO.Type type) {
        return invitationRepository.findByRecipientIdAndFromIdAndType(recipientId, fromId, type);
    }

    @Override
    @Cacheable(value = "invitationRecipient", key = "'user_all_invitation_' + #recipientId")
    public List<Invitation> findByRecipientId(String recipientId) {
        return invitationRepository.findByRecipientId(recipientId);
    }

    @Override
    @Cacheable(value = "invitationFromId", key = "'invitation_from_id_' + #fromId")
    public List<Invitation> findByFromId(String fromId) {
        return invitationRepository.findByFromId(fromId);
    }

    @Override
    @Cacheable(value = "invitationIdRecipientOrFrom",
            key = "'invitation_id_' + #id + '_recipient_' + #recipientId + '_from_id_' + #fromId")
    public Optional<Invitation> findByIdAndRecipientIdOrFromId(String id, String recipientId, String fromId) {
        return invitationRepository.findByIdAndRecipientIdOrFromId(id, recipientId, fromId);
    }

    @Override
    @CachePut(value = "invitation", key = "'invitation_' + #invitation.getId()")
    @Caching(
            evict = {
                    @CacheEvict(value = "invitationFromId", allEntries = true),
                    @CacheEvict(value = "invitationRecipient", allEntries = true),
                    @CacheEvict(value = "invitationRecipientFromType", allEntries = true)
            }
    )
    public Invitation update(Invitation invitation) {
        return invitationRepository.save(invitation);
    }
}
