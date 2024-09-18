package com.mlorenzo.photoapp.orders;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

// Nota: Dos formas diferentes de configurar la seguridad que son equivalentes(La otra forma se encuentra en la clase WebSecurityConfig2)
// Esta es la forma antigua para configurar la seguridad y está deprecada desde las versiones de Spring Boot que utilizan la versión 5.7 de Spring Security o más nuevas

// Se comenta porque actualmente se está usando la implementación de la clase WebSecurityConfig2
//@EnableWebSecurity // Esta anotación ya incluye la anotación @Configuration
public class WebSecurityConfig1 extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests().anyRequest().authenticated()
			.and()
			// Se desactiva el manejo de sesión de Spring Security(por defecto activado) porque la autenticación basada en JWT(Jason Web Token) es sin estado(stateless)
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
	        .oauth2ResourceServer()
	        .jwt();
	}
	
	

}
