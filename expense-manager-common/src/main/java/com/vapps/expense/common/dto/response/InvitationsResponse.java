package com.vapps.expense.common.dto.response;

import com.vapps.expense.common.dto.InvitationDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvitationsResponse extends Response {

    private List<InvitationDTO> invitations;

    public InvitationsResponse(int status, String message, LocalDateTime time, String path,
            List<InvitationDTO> invitations) {
        super(status, message, time, path);
        this.invitations = invitations;
    }
}
