package com.mlorenzo.clients.sociallogin.SocialLoginWebClient.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@EnableWebSecurity // Esta anotación ya incluye la anotación @Configuration
public class WebSecurity extends WebSecurityConfigurerAdapter {

	// Por defecto, la autoconfiguración de Spring Boot crea un bean del tipo ClientRegistrationRepository usando un repositorio en memoria a partir de las propiedades sobre los clientes OAuth2 indicadas en los archivos de propiedades como "application.properties"
	// Esta propiedad de tipo ClientRegistrationRepository representa el repositorio donde están registrados los clientes OAuth2
	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests(requests -> 
			requests.antMatchers("/").permitAll()
			.anyRequest().authenticated())
		.oauth2Login() // La autenticación se realiza usando OAuth2
		.and()
		.logout() // Habilta la funcionalidad de logout
		// URL de redirección en caso de que el proceso de logout sea exitoso
		//.logoutSuccessUrl("/") // Se comenta porque ahora usamos nuestro manejador personalizado para el caso de logout con éxito
		.logoutSuccessHandler(oidcLogoutSuccessHandler())
		// Spring Security invalidará la sesión http en caso de logout satisfactorio por parte del usuario. Esto significa que el usuario tendrá que volver a autenticarse si desea volver a acceder a las partes protegidas de esta aplicación web
		//.invalidateHttpSession(true) // Por defecto es true, así que puede omitirse
		// Elimina el objeto de autenticación(objeto de tipo Authentication), que contiene los datos del actual usuario autenticado, en caso de que el proceso de logout haya sido satisfactorio 
		//.clearAuthentication(true) // Por defecto es true, así que puede omitirse
		// En este método podemos especificar las cookies que queremos eliminar cuando el proceso de logout haya sido satisfactorio
		.deleteCookies("JSESSIONID");
	}
	
	// Por defecto, muchos proveedores Oauth,o servidores de autorización, como Google, Facebook, Okta, etc.., mantienen la sesión activa cuando el actual usuario autenticado ha realizado satisfactoriamente en la aplicación cliente(en este caso, esta aplicación web) el proceso de logout
	// Entonces, si ese usuario, que acaba de hacer logout satisfactoriamente, vuelve a acceder a las partes protegidas de la aplicación cliente, automáticamente el proveedor Oauth2 le redirige a esas partes protegidas porque la sesión con la autenticación sigue viva en dicho proveedor
	// Dicho esto, este método es un manejador personalizado para el proceso de logout en caso de éxito que pone fin a la sesión del usuario autenticado en el proveedor OAuth2
	private OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler() {
		OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
		successHandler.setPostLogoutRedirectUri("http://localhost:8080"); // Ruta de redirección en caso de logout con éxito. Establece esta ruta en el parámetro "post_logout_redirect_uri" de la petición http que se hace al proveedor OAuth2 para poner fin a la sesión
		return successHandler;
	}
	
	

}
