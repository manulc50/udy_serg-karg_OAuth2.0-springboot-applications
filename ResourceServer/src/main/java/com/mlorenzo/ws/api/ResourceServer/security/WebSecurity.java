package com.mlorenzo.ws.api.ResourceServer.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;


// El atributo "securedEnabled" con valor true habilita el uso de la anotación de seguridad @Secured que puede ser usada en los métodos de las clases que son controladores o en los métodos de las clases de la capa de servicio
// El atributo "prePostEnabled" con valor true habilita el uso de las anotaciónes de seguridad @PreAuthorized y @PostAuthorized que pueden ser usadas en los métodos de las clases que son controladores o en los métodos de las clases de la capa de servicio
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
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
		
		http
		// Añade un filtro de seguridad CORS(Sólo tiene sentido si hay algún origen que se comunica directamente con este servidor de recursos. Es decir, si la comunicación es a través de un Api Gateway, no tiene sentido este filtro CORS en este servidor de recursos ya que se habilitaría en ese Api Gateway. Además, si se tiene habilitado ambos filtros de seguridad CORS, se produciría un error en la respuesta de cada petición http)
		//.cors() // Se comenta porque estamos usando el filtro de seguridad CORS de nuestro Api Gateway
		//.and()
		.authorizeRequests(requests -> 
			// Sólo se permite el acceso a la ruta "/users/status/check" mediante peticiones http de tipo GET a los usuarios autenticados que tengan el scope "profile"
			// Dicho de otra manera, para acceder a la ruta "/users/status/check", las peticiones http tiene que ser de tipo GET y los token JWT que viajan en la cabecera(cabecera "Authorization") de esas peticiones tienen que ser válidos y contener el scope "profile"
			/* requests.antMatchers(HttpMethod.GET, "/users/status/check").hasAuthority("SCOPE_profile")*/ // El prefijo "SCOPE_" es necesario indicarlo aquí porque, cuando Spring Security obtiene por debajo los scopes de los token JWT, añade ese prefijo de forma automática a esos scopes
			// Sólo se permite el acceso a la ruta "/users/status/check" mediante peticiones http de tipo GET a los usuarios autenticados que tengan el role "developer"
			// Dicho de otra manera, para acceder a la ruta "/users/status/check", las peticiones http tiene que ser de tipo GET y los token JWT que viajan en la cabecera(cabecera "Authorization") de esas peticiones tienen que ser válidos y contener el role "developer"	
			requests.antMatchers(HttpMethod.GET, "/users/status/check").hasRole("developer") // Spring Security maneja por debajo los roles que obtiene del token JWT usando el prefijo "ROLE_". Entonces, si usamos el método "hasRole" o el método "hasAnyRole" para el control de acceso a los recursos, no es necesario especificar el prefijo "ROLE_" en esos métodos porque ya lo tienen en cuenta
																	   //.hasAuthority("ROLE_developer") // Spring Security maneja por debajo los roles que obtiene del token JWT usando el prefijo "ROLE_". Entonces, si usamos el método "hasAuthority" o el método "hasAnyAuthority" para el control de acceso a los recursos, es necesario especificar el prefijo "ROLE_" en esos métodos porque no lo tienen en cuenta
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
	
	// Se comenta porque estamos usando el filtro de seguridad CORS de nuestro Api Gateway
	// Crea un bean de Spring con nuestra configuración de seguridad CORS(Sólo si se activa el filtro de seguridad CORS(ver que se invoca al método "cors" en el método de arriba "configure")
	/*@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:8181")); // "*" para permitir cualquier origen
		corsConfiguration.setAllowedMethods(Arrays.asList("GET","POST")); // "*" para permitir cualquier tipo de petición http
		corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization")); // "*" para permitir cualquier cabecera en la petición http
	
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration); // Registramos nuestra configuración de seguridad CORS para que se aplique a cualquier ruta de este servidor de recursos que se solicite desde el origen
		
		return source;
	}*/
 
	
}
