package com.mlorenzo.tutorials.legacyusersservice.service;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mlorenzo.tutorials.legacyusersservice.data.UserEntity;
import com.mlorenzo.tutorials.legacyusersservice.data.UsersRepository;
import com.mlorenzo.tutorials.legacyusersservice.response.UserRest;

@Service
public class UsersServiceImpl implements UsersService {
    private UsersRepository usersRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UsersServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.usersRepository = usersRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    
    @Override
    public UserRest getUserDetails(String userEmail) {
        UserRest returnValue = new UserRest();

        UserEntity userEntity = usersRepository.findByEmail(userEmail);
        if (userEntity == null) {
            return returnValue;
        }

        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;
    }

    @Override
    public UserRest getUserDetails(String userEmail, String password) {
        UserRest returnValue = null;

        UserEntity userEntity = usersRepository.findByEmail(userEmail);

        if (userEntity == null) {
            return returnValue;
        }

        // El método "matches" encripta usando BCrypt la password del primer argumento de entrada y la compara con la password del segundo argumento
        if (bCryptPasswordEncoder.matches(password, userEntity.getEncryptedPassword())) {
            System.out.println("password matches!!!");
            returnValue = new UserRest();
            BeanUtils.copyProperties(userEntity, returnValue);
        }

        return returnValue;
    }

}
