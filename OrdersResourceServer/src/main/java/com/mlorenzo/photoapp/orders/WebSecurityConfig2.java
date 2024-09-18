package com.mlorenzo.photoapp.orders;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

// Nota: Dos formas diferentes de configurar la seguridad que son equivalentes(La otra forma se encuentra en la clase WebSecurityConfig1)
// Esta es la nueva forma para configurar la seguridad que aparece en las versiones de Spring Boot que utilizan la versión 5.7 de Spring Security o más nuevas

@EnableWebSecurity // Esta anotación ya incluye la anotación @Configuration
public class WebSecurityConfig2 {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeRequests().anyRequest().authenticated()
			.and()
			// Se desactiva el manejo de sesión de Spring Security(por defecto activado) porque la autenticación basada en JWT(Jason Web Token) es sin estado(stateless)
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
	        .oauth2ResourceServer()
	        .jwt();
		
		return http.build();
	}
}
