package com.mlorenzo.tutorials.legacyusersservice.service;

import com.mlorenzo.tutorials.legacyusersservice.response.UserRest;

public interface UsersService {
   UserRest getUserDetails(String userEmail, String password);
   UserRest getUserDetails(String userEmail);
}
