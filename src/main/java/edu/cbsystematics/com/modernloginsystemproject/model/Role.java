package edu.cbsystematics.com.modernloginsystemproject.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Data
@DynamicUpdate
@NoArgsConstructor
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "role_name", nullable = false, columnDefinition = "varchar(20)")
    private String roleName;

    public Role(String roleName) {
        this.roleName = roleName;
    }

}
