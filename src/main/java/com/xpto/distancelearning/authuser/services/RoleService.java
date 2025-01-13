package com.xpto.distancelearning.authuser.services;

import com.xpto.distancelearning.authuser.enums.RoleType;
import com.xpto.distancelearning.authuser.models.RoleModel;

import java.util.Optional;

public interface RoleService {
    Optional<RoleModel> findByRoleName(RoleType roleType);
}