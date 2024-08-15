package com.vapps.expense.model;

import com.vapps.expense.common.dto.ExpenseDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;
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
    private List<String> invoices; // For referencing the invoice attachment's id

    @DocumentReference
    private Family family;

    @DocumentReference
    private User createdBy;

    private String ownerId;
    private ExpenseDTO.ExpenseType type;
    private LocalDateTime time;

    public static Expense build(ExpenseDTO expenseDTO) {
        Expense expense = new Expense();
        expense.setId(expenseDTO.getId());
        if (expenseDTO.getFamily() != null) {
            expense.setFamily(Family.build(expenseDTO.getFamily()));
        }
        expense.setCreatedBy(User.build(expenseDTO.getCreatedBy()));
        expense.setOwnerId(expenseDTO.getOwnerId());
        expense.setName(expenseDTO.getName());
        expense.setDescription(expenseDTO.getDescription());
        expense.setInvoices(expenseDTO.getInvoices());
        expense.setAmount(expenseDTO.getAmount());
        expense.setType(expenseDTO.getType());
        expense.setCurrency(expenseDTO.getCurrency());
        expense.setTime(expenseDTO.getTime());
        return expense;
    }

    public ExpenseDTO toDTO() {
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setId(id);
        if (family != null) {
            expenseDTO.setFamily(family.toDTO());
        }
        expenseDTO.setCreatedBy(createdBy.toDTO());
        expenseDTO.setOwnerId(ownerId);
        expenseDTO.setName(name);
        expenseDTO.setDescription(description);
        expenseDTO.setInvoices(invoices);
        expenseDTO.setAmount(amount);
        expenseDTO.setType(type);
        expenseDTO.setCurrency(currency);
        expenseDTO.setTime(time);
        return expenseDTO;
    }

}
