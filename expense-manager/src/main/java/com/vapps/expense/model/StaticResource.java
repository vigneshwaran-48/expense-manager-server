package com.vapps.expense.model;

import com.vapps.expense.common.dto.StaticResourceDTO;
import com.vapps.expense.common.dto.StaticResourceDTO.ContentType;
import com.vapps.expense.common.dto.StaticResourceDTO.Visibility;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Data
@Document
public class StaticResource {

    @Id
    private String id;

    private String ownerId;

    private ContentType type;

    private byte[] data;

    private Visibility visibility;

    private String name;

    public StaticResourceDTO toDTO() {
        StaticResourceDTO staticResourceDTO = new StaticResourceDTO();
        staticResourceDTO.setData(data);
        staticResourceDTO.setId(id);
        staticResourceDTO.setType(type);
        staticResourceDTO.setOwnerId(ownerId);
        staticResourceDTO.setVisibility(visibility);
        staticResourceDTO.setName(name);
        return staticResourceDTO;
    }

    public static StaticResource build(StaticResourceDTO staticResourceDTO) {
        StaticResource staticResource = new StaticResource();
        staticResource.setId(staticResourceDTO.getId());
        staticResource.setData(staticResourceDTO.getData());
        staticResource.setOwnerId(staticResourceDTO.getOwnerId());
        staticResource.setType(staticResourceDTO.getType());
        staticResource.setVisibility(staticResourceDTO.getVisibility());
        staticResource.setName(staticResourceDTO.getName());
        return staticResource;
    }
}
