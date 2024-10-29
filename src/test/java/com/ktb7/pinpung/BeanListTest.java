package com.ktb7.pinpung;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
public class BeanListTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void listAllBeans() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        System.out.println("등록된 빈 수: " + beanNames.length);
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            System.out.println("빈 이름: " + beanName + " - 클래스: " + bean.getClass().getName());
        }
    }
}
