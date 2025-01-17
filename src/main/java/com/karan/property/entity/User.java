package com.karan.property.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @JsonIgnore
    private ObjectId objectId;

    @Indexed(unique = true)
    @NonNull
    private String username;
    @NonNull
    private String password;

    @NonNull
    private String mail;

    private int primeStatus;

    @DBRef
    private List<Colony> colonies = new ArrayList<>();

    @DBRef
    private List<Customer> customers = new ArrayList<>();

    @JsonProperty("id") // Rename the property to "id" in the JSON response
    public String getId() {
        return objectId != null ? objectId.toHexString() : null; // Convert ObjectId to a string
    }

    public void setId(ObjectId objectId) {
        this.objectId = objectId;
    }
}

