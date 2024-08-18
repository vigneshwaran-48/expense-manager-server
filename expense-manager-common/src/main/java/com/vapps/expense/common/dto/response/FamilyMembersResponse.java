package com.vapps.expense.common.dto.response;

import com.vapps.expense.common.dto.FamilyMemberDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FamilyMembersResponse extends Response {

    private List<FamilyMemberDTO> members;

    public FamilyMembersResponse(int status, String message, LocalDateTime time, String path,
            List<FamilyMemberDTO> members) {
        super(status, message, time, path);
        this.members = members;
    }
}
