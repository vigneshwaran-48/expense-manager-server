package com.vapps.expense.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppErrorResponse {

    private int status;
    private String error;
    private LocalDateTime time;
    private String path = "/";

}
