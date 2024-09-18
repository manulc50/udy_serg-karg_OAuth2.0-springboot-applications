package com.mlorenzo.ws.api.albums.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

// Esta clase se corresponde con nuestro convertidor personalizado que se encarga de convertir los roles contenidos en los claims(derechos) de cada token JWT en una colección de tipo GrantedAuthority de Spring Security en función de la estructura de claims(derechos) creada por el servidor de autorización Keycloak
// Es necesario este convertidor porque, cuando manejamos roles en lugar de scopes para controlar el acceso a los recursos, cada servidor de autorización(En nuestro caso Keycloak) crea su propia estructura de claims(derechos) para los token JWT y, por lo tanto, Spring Security no sabe en un principio como obtener los roles contenidos en esos claims(derechos) de esos token JWT para crear sus colecciones de tipo GrantedAuthority 

@Component
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>>{

	// El parámetro de entrada "Jwt jwt" respresenta el token JWT
	// El retorno de tipo Collection<GrantedAuthority> es la colección de tipo GrantedAuthority de Spring Security que va a contener los nombres de los roles usando el prefijo "ROLE_"
	@SuppressWarnings("unchecked")
	@Override
	public Collection<GrantedAuthority> convert(Jwt jwt) {
		// El servidor de autorización Keycloak crea en los claims(derechos) de cada token JWT una estructura llamada "realm_access" que contiene otra estructura llamada "roles" y, dentro de esa estructura, se encuentra una lista con los nombres de los roles
		Map<String, Object> realmAccess = (Map<String, Object>)jwt.getClaims().get("realm_access");
		
		if(realmAccess == null || realmAccess.isEmpty())
			return new ArrayList<GrantedAuthority>();
		
		Collection<GrantedAuthority> authorities = ((List<String>)realmAccess.get("roles")).stream()
				.map("ROLE_"::concat) // Versión simplificada de la expresión "roleName -> "ROLE_".concat(roleName)"
				.map(SimpleGrantedAuthority::new) // Versión simplificada de la expresión "roleName -> new SimpleGrantedAuthority(roleName)"
				.collect(Collectors.toList());
		
		return authorities;
	}

}
