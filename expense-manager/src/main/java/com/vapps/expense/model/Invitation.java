package com.vapps.expense.model;

import com.vapps.expense.common.dto.InvitationDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Map;

@Document
@Data
public class Invitation {

    @Id
    private String id;

    private String title;
    private String content;
    private Map<String, Object> properties;

    @DocumentReference
    private User recipient;

    @DocumentReference
    private User from;

    private InvitationDTO.Type type;

    public InvitationDTO toDTO() {
        InvitationDTO invitationDTO = new InvitationDTO();
        invitationDTO.setId(id);
        invitationDTO.setContent(content);
        invitationDTO.setFrom(from.toDTO());
        invitationDTO.setTitle(title);
        invitationDTO.setRecipient(recipient.toDTO());
        invitationDTO.setProperties(properties);
        invitationDTO.setType(type);
        return invitationDTO;
    }

    public static Invitation build(InvitationDTO invitationDTO) {
        Invitation invitation = new Invitation();
        invitation.setId(invitationDTO.getId());
        invitation.setContent(invitationDTO.getContent());
        invitation.setFrom(User.build(invitationDTO.getFrom()));
        invitation.setRecipient(User.build(invitationDTO.getRecipient()));
        invitation.setType(invitationDTO.getType());
        invitation.setTitle(invitationDTO.getTitle());
        invitation.setProperties(invitationDTO.getProperties());
        return invitation;
    }
}
