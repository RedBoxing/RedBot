package fr.redboxing.redbot.database.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "role_menus")
public class RoleMenuEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String messageId;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
