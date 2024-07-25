package com.xpto.distancelearning.authuser.repositories;

import com.xpto.distancelearning.authuser.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * Repository for UserModel.
 * It is configured to use the JpaSpecificationExecutor to filter the data in the database.
 */
public interface UserRepository extends JpaRepository<UserModel, UUID>, JpaSpecificationExecutor<UserModel> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
