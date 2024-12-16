package com.xpto.distancelearning.authuser.services.impl;

import com.xpto.distancelearning.authuser.models.UserCourseModel;
import com.xpto.distancelearning.authuser.models.UserModel;
import com.xpto.distancelearning.authuser.repositories.UserCourseRepository;
import com.xpto.distancelearning.authuser.repositories.UserRepository;
import com.xpto.distancelearning.authuser.services.UserService;
import com.xpto.distancelearning.authuser.specifications.SpecificationTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserCourseRepository userCourseRepository;

    @Override
    public List<UserModel> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<UserModel> findById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    @Override
    public void delete(UserModel userModel) {
        List<UserCourseModel> userCourseModelList = userCourseRepository.findAllUserCourseIntoUser(userModel.getUserId());
        if(!userCourseModelList.isEmpty()){
            userCourseRepository.deleteAll(userCourseModelList);
        }
        userRepository.delete(userModel);
    }

    @Override
    public void save(UserModel userModel) {
        userRepository.save(userModel);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

//    /**
//     * @See UserService#findAll(SpecificationTemplate.UserSpec, Pageable)
//     */
//    @Override
//    public Page<UserModel> findAll(SpecificationTemplate.UserSpec spec, Pageable pageable) {
//        return userRepository.findAll(spec, pageable);
//    }
    @Override
    public Page<UserModel> findAll(Specification<UserModel> spec, Pageable pageable) {
        return userRepository.findAll(spec, pageable);
    }
}
