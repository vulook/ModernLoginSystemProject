package edu.cbsystematics.com.modernloginsystemproject.repository;

import edu.cbsystematics.com.modernloginsystemproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Update User
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.firstName = :firstName, u.lastName = :lastName, u.email = :email, u.password = :password WHERE u.id = :id")
    void updateUser(
            @Param("id") Long id,
            @Param("firstName") String firstName,
            @Param("lastName") String lastNam,
            @Param("email") String email,
            @Param("password") String password
    );


    // Update Password
    @Modifying
    @Transactional
    @Query("update User u set u.password = :password where u.id = :id")
    void updatePassword(
            @Param("id") Long id,
            @Param("password") String password
    );

    // Get users by role
    @Query("SELECT u FROM User u JOIN u.roles r WHERE :role IN (SELECT r.roleName FROM Role r WHERE r.id = u.id)")
    List<User> findByRole(@Param("role") String role);

    // Get Email
    User findByEmail(String email);

}
