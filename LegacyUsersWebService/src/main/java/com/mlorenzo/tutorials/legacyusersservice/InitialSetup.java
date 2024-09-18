package com.mlorenzo.tutorials.legacyusersservice;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.mlorenzo.tutorials.legacyusersservice.data.UserEntity;
import com.mlorenzo.tutorials.legacyusersservice.data.UsersRepository;

@Component
public class InitialSetup {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @EventListener
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        UserEntity user = new UserEntity(
                1L,
                "qswe3mg84mfjtu",
                "Manuel",
                "Lorenzo",
                "test2@test.com",
                bCryptPasswordEncoder.encode("12345678"),
                "",
                false);

        usersRepository.save(user);
    }
}
