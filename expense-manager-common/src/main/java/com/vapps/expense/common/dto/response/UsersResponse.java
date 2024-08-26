package com.vapps.expense.common.dto.response;

import com.vapps.expense.common.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersResponse extends Response {
    private List<UserDTO> users;

    public UsersResponse(int status, String message, LocalDateTime time, String path, List<UserDTO> users) {
        super(status, message, time, path);
        this.users = users;
    }
}
