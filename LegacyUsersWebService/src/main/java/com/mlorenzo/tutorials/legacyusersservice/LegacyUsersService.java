package com.mlorenzo.tutorials.legacyusersservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Nota: Este proyecto es un Web Service que obtiene datos de usuarios autenticados desde una base de datos H2 para proporcionárselos a
//       nuestro proveedor personalizado "MyRemoteUserStorageProvider" para el servidor Keycloak

@SpringBootApplication
public class LegacyUsersService {

	public static void main(String[] args) {
		SpringApplication.run(LegacyUsersService.class, args);
 
	}
	
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
 
 

}
