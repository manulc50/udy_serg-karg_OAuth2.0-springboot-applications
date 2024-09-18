package com.mlorenzo.ws.api.photos.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;


@EnableWebSecurity // Esta anotación ya incluye la anotación @Configuration
public class WebSecurity extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private KeycloakRoleConverter keycloakRoleConverter;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		// Convertidor personalizado que se encarga de convertir los roles contenidos en los claims(derechos) de cada token JWT en una colección de tipo GrantedAuthority de Spring Security en función de la estructura de claims(derechos) creada por el servidor de autorización Keycloak
		// Es necesario este convertidor porque, cuando manejamos roles en lugar de scopes para controlar el acceso a los recursos, cada servidor de autorización(En nuestro caso Keycloak) crea su propia estructura de claims(derechos) para los token JWT y, por lo tanto, Spring Security no sabe en un principio como obtener los roles contenidos en esos claims(derechos) de esos token JWT para crear sus colecciones de tipo GrantedAuthority 
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(keycloakRoleConverter);
		
		http.authorizeRequests((requests) -> 
			// Sólo se permite el acceso a las rutas que coincidan con la expresión "/photos/**" mediante peticiones http de tipo GET a los usuarios autenticados que tengan el scope "profile"
			// Dicho de otra manera, para acceder a las rutas que coincidan con la expresión "/photos/**", las peticiones http tiene que ser de tipo GET y los token JWT que viajan en la cabecera(cabecera "Authorization") de esas peticiones tienen que ser válidos y contener el scope "profile"
			/* requests.antMatchers(HttpMethod.GET, "/photos/**").hasAuthority("SCOPE_profile")*/ // El prefijo "SCOPE_" es necesario indicarlo aquí porque, cuando Spring Security obtiene por debajo los scopes de los token JWT, añade ese prefijo de forma automática a esos scopes
			// Sólo se permite el acceso a las rutas que coincidan con la expresión "/photos/**" mediante peticiones http de tipo GET a los usuarios autenticados que tengan el role "developer"
			// Dicho de otra manera, para acceder a las rutas que coincidan con la expresión "/photos/**", las peticiones http tiene que ser de tipo GET y los token JWT que viajan en la cabecera(cabecera "Authorization") de esas peticiones tienen que ser válidos y contener el role "developer"	
			requests.antMatchers(HttpMethod.GET, "/photos").hasAuthority("ROLE_developer") // Spring Security maneja por debajo los roles que obtiene del token JWT usando el prefijo "ROLE_". Entonces, si usamos el método "hasAuthority" o el método "hasAnyAuthority" para el control de acceso a los recursos, es necesario especificar el prefijo "ROLE_" en esos métodos porque no lo tienen en cuenta
														   //.hasRole("developer") // Spring Security maneja por debajo los roles que obtiene del token JWT usando el prefijo "ROLE_". Entonces, si usamos el método "hasRole" o el método "hasAnyRole" para el control de acceso a los recursos, no es necesario especificar el prefijo "ROLE_" en esos métodos porque ya lo tienen en cuenta
																	   
			.anyRequest().authenticated())
		// Crea por debajo un filtro de autorización que intercepta las peticiones http que llegan para extraer la cabecera "Authorization" de esas peticiones
		// Este filtro espera que la cabecera "Authorization" contenga el prefijo "Bearer " seguido de un token JWT y, a coninuación, valida dicho token JWT 
		// Si el token JWT es correcto, Spring Security creará un objeto Principal(representa el usuario autenticado) usando la información contenida en ese token JWT
		.oauth2ResourceServer()
		.jwt()
		// Registramos nuestro convertidor personalizado que se encarga de convertir los roles contenidos en los claims(derechos) de cada token JWT en una colección de tipo GrantedAuthority de Spring Security
		.jwtAuthenticationConverter(jwtAuthenticationConverter);
		
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Se desactiva el manejo de sesión de Spring Security(por defecto activado) porque la autenticación basada en JWT(Jason Web Token) es sin estado(stateless)
	}
}
