package com.vapps.expense.model;

import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.dto.UserDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
@Document
public class Expense {

    @Id
    private String id;

    private String name;
    private String description;
    private long amount;
    private String currency;
    private List<String> invoices;

    @DocumentReference
    private FamilyDTO family;

    @DocumentReference
    private UserDTO owner;

}
