package com.xpto.distancelearning.authuser.services.impl;

import com.xpto.distancelearning.authuser.enums.RoleType;
import com.xpto.distancelearning.authuser.models.RoleModel;
import com.xpto.distancelearning.authuser.repositories.RoleRepository;
import com.xpto.distancelearning.authuser.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Optional<RoleModel> findByRoleName(RoleType roleType) {
        return roleRepository.findByRoleName(roleType);
    }
}