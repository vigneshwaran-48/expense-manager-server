package com.vapps.expense.model;

import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.dto.FamilyDTO.Visibility;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;

@Data
@Document
public class Family {

    @Id
    private String id;

    private String name;
    private String description;
    private Visibility visibility;
    private LocalDateTime createdTime;

    @DocumentReference
    private User createdBy;

    public FamilyDTO toDTO() {
        FamilyDTO familyDTO = new FamilyDTO();
        familyDTO.setId(id);
        familyDTO.setName(name);
        familyDTO.setDescription(description);
        familyDTO.setVisibility(visibility);
        familyDTO.setCreatedBy(createdBy.toDTO());
        familyDTO.setCreatedTime(createdTime);
        return familyDTO;
    }

    public static Family build(FamilyDTO familyDTO) {
        Family family = new Family();
        family.setId(familyDTO.getId());
        family.setName(familyDTO.getName());
        family.setDescription(familyDTO.getDescription());
        family.setVisibility(familyDTO.getVisibility());
        family.setCreatedBy(User.build(familyDTO.getCreatedBy()));
        family.setCreatedTime(familyDTO.getCreatedTime());
        return family;
    }
}
