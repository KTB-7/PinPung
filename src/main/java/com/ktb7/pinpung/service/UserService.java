//package com.ktb7.pinpung.service;
//
//import com.ktb7.pinpung.entity.User;
//import com.ktb7.pinpung.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class UserService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    // 유저 저장 또는 업데이트 메서드
//    public User saveOrUpdateUser(User user) {
//        Optional<User> existingUser = userRepository.findByUserId(user.getUserId());
//
//        if (existingUser.isPresent()) {
//            // 이미 존재하면 기존 유저 정보를 업데이트
//            User userToUpdate = existingUser.get();
//            userToUpdate.setUserName(user.getUserName());
//            // 필요한 다른 필드도 여기에 추가하여 업데이트할 수 있음
//            return userRepository.save(userToUpdate);
//        } else {
//            // 존재하지 않으면 새 유저로 삽입
//            return userRepository.save(user);
//        }
//    }
//}
