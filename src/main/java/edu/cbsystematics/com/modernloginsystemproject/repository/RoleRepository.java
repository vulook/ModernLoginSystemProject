package edu.cbsystematics.com.modernloginsystemproject.repository;

import edu.cbsystematics.com.modernloginsystemproject.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Update Role Information
    @Modifying
    @Transactional
    @Query("UPDATE Role r SET r.roleName = :roleName WHERE r.id = :id")
    void updateRole(
            @Param("id") Long id,
            @Param("roleName") String roleName);


    // Finds a Role object by its name
    Optional<Role> findByRoleName(String name);

}