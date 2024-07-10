package com.vapps.expense.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {

    private int status;
    private String message;
    private LocalDateTime time;
    private String path;

}
