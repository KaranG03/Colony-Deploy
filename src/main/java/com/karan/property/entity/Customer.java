package com.karan.property.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;


@Data
@Document(collection = "customers")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @JsonIgnore
    private ObjectId objectId;
    private String custName;
    private Long phNum;
    private String address;

    @Indexed(unique = true)
    private Long aadharNum;
    @DBRef
    private List<Plot> custPlots = new ArrayList<>();

    @JsonProperty("id") // Rename the property to "id" in the JSON response
    public String getId() {
        return objectId != null ? objectId.toHexString() : null; // Convert ObjectId to a string
    }

    public void setId(ObjectId objectId) {
        this.objectId = objectId;
    }
}
