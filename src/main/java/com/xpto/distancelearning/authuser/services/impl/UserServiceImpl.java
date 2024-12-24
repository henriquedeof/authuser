package com.xpto.distancelearning.authuser.services.impl;

import com.xpto.distancelearning.authuser.clients.CourseClient;
import com.xpto.distancelearning.authuser.enums.ActionType;
import com.xpto.distancelearning.authuser.models.UserModel;
import com.xpto.distancelearning.authuser.publishers.UserEventPublisher;
import com.xpto.distancelearning.authuser.repositories.UserRepository;
import com.xpto.distancelearning.authuser.services.UserService;
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

//    @Autowired
//    UserCourseRepository userCourseRepository;

    @Autowired
    private CourseClient courseClient;

    @Autowired
    private UserEventPublisher userEventPublisher;

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
        userRepository.delete(userModel);

//        NOTE: The code below was commented out as the communication is now asynchronous
//        boolean deleteUserCourseInCourse = false;
//        List<UserCourseModel> userCourseModelList = userCourseRepository.findAllUserCourseIntoUser(userModel.getUserId());
//        if(!userCourseModelList.isEmpty()){
//            userCourseRepository.deleteAll(userCourseModelList);
//            deleteUserCourseInCourse = true;
//        }
//        userRepository.delete(userModel);
//        if(deleteUserCourseInCourse){
//            courseClient.deleteUserInCourse(userModel.getUserId());
//        }
    }

    @Override
    public UserModel save(UserModel userModel) {
        return userRepository.save(userModel);
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

    @Transactional
    @Override
    public UserModel saveUser(UserModel userModel) {
        userModel = userRepository.save(userModel);
        userEventPublisher.publishUserEvent(userModel.convertToUserEventDto(), ActionType.CREATE);
        return userModel;
    }

    @Transactional
    @Override
    public void deleteUser(UserModel userModel) {
        delete(userModel);
        userEventPublisher.publishUserEvent(userModel.convertToUserEventDto(), ActionType.DELETE);
    }

    @Transactional
    @Override
    public UserModel updateUser(UserModel userModel) {
        userModel = save(userModel);
        userEventPublisher.publishUserEvent(userModel.convertToUserEventDto(), ActionType.UPDATE);
        return userModel;
    }

    @Override
    public UserModel updatePassword(UserModel userModel) {
        return save(userModel);
    }
}
