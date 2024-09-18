package com.mlorenzo.photoapp.oauthServer;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SpringSecurityConfiguration {
	
	@Bean
	SecurityFilterChain configureSecurityFilterChain(HttpSecurity http) throws Exception {
		// Todas las peticiones http requieren autenticación mediante el uso de un formulario de login
		http
			.authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
			.formLogin(Customizer.withDefaults());
		
		return http.build();
	}
	
	// Credenciales de usuarios(En este caso almacenadas en memoria) que pueden hacer login
	@Bean
	public UserDetailsService users() {
		// Por defecto usa el algoritmo BCrypt
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		
		UserDetails user = User.withUsername("manuel")
				.password(encoder.encode("password"))
				.roles("USER")
				.build();
		
		return new InMemoryUserDetailsManager(user);
	}
}
