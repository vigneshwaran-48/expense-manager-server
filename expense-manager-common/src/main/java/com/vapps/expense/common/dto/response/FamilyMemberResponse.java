package com.vapps.expense.common.dto.response;

import com.vapps.expense.common.dto.FamilyMemberDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FamilyMemberResponse extends Response {

    private FamilyMemberDTO member;

    public FamilyMemberResponse(int status, String message, LocalDateTime time, String path, FamilyMemberDTO member) {
        super(status, message, time, path);
        this.member = member;
    }
}
