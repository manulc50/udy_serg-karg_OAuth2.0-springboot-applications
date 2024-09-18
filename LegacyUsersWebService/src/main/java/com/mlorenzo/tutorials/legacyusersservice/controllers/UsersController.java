package com.mlorenzo.tutorials.legacyusersservice.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mlorenzo.tutorials.legacyusersservice.response.UserRest;
import com.mlorenzo.tutorials.legacyusersservice.response.VerifyPasswordResponse;
import com.mlorenzo.tutorials.legacyusersservice.service.UsersService;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    UsersService usersService;

    @GetMapping("/{userEmail}")
    public UserRest getUser(@PathVariable String userEmail) {
        return usersService.getUserDetails(userEmail);
    }

    @PostMapping("/{userEmail}/verify-password")
    public VerifyPasswordResponse verifyUserPassword(@PathVariable("userEmail") String email,
            @RequestBody String password) {
        VerifyPasswordResponse returnValue = new VerifyPasswordResponse(false);

        UserRest user = usersService.getUserDetails(email, password);

        if (user != null) {
            returnValue.setResult(true);
        }

        return returnValue;
    }

}
