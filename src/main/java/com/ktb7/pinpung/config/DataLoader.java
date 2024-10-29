//package com.ktb7.pinpung.config;
//
//import com.ktb7.pinpung.entity.User;
//import com.ktb7.pinpung.repository.UserRepository;
//import com.ktb7.pinpung.service.UserService;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DataLoader implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final UserService userService;
//
//    public DataLoader(UserRepository userRepository, UserService userService) {
//        this.userRepository = userRepository;
//        this.userService = userService;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        // 가짜 유저 데이터 생성
//        User user1 = new User();
//        user1.setUserEmail("fakeuser1@example.com");
//        user1.setUserName("Fake User 1");
//
//        User user2 = new User();
//        user2.setUserEmail("fakeuser2@example.com");
//        user2.setUserName("Fake User 2");
//
//        // 데이터베이스에 가짜 유저 저장
//        userService.saveOrUpdateUser(user1);
//        userService.saveOrUpdateUser(user2);
//
//    }
//}
//
