package com.xpto.distancelearning.authuser.repositories;

import com.xpto.distancelearning.authuser.models.UserModel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for UserModel.
 * It is configured to use the JpaSpecificationExecutor to filter the data in the database.
 */
public interface UserRepository extends JpaRepository<UserModel, UUID>, JpaSpecificationExecutor<UserModel> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    /**
     * Although the UserModel has a relationship with the RoleModel, the roles are not loaded by default.
     * This method uses the EntityGraph to load the roles (EAGER).
     *
     * @param username The username of the user.
     * @return An optional of UserModel.
     */
    @EntityGraph(attributePaths = "roles", type = EntityGraph.EntityGraphType.FETCH)
    Optional<UserModel> findByUsername(String username);
}
