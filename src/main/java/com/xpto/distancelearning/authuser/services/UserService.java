package com.xpto.distancelearning.authuser.services;

import com.xpto.distancelearning.authuser.models.UserModel;
import com.xpto.distancelearning.authuser.specifications.SpecificationTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    List<UserModel> findAll();
    Optional<UserModel> findById(UUID userId);
    void delete(UserModel userModel);
    UserModel save(UserModel userModel);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    /**
     * This method is used to filter the data using Specification.
     * It also uses Pagination.
     *
     * @param spec
     * @param pageable
     * @return
     */
    //Page<UserModel> findAll(SpecificationTemplate.UserSpec spec, Pageable pageable);
    Page<UserModel> findAll(Specification<UserModel> spec, Pageable pageable);

    UserModel saveUser(UserModel userModel);
    void deleteUser(UserModel userModel);
    UserModel updateUser(UserModel userModel);
    UserModel updatePassword(UserModel userModel);
}
