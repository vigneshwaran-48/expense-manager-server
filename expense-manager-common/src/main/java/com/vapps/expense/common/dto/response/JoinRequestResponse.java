package com.vapps.expense.common.dto.response;

import com.vapps.expense.common.dto.JoinRequestDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JoinRequestResponse extends Response {

    private JoinRequestDTO request;

    public JoinRequestResponse(int status, String message, LocalDateTime time, String path, JoinRequestDTO request) {
        super(status, message, time, path);
        this.request = request;
    }
}
