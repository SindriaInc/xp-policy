package org.sindria.xppolicy.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue
    private String id;

//    @ManyToMany(mappedBy = "policies")
//    Set<Policy> users;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User() {}

    /**
     * User constructor
     */
    public User(String id) {
        super();
        this.id = id;
    }
}
