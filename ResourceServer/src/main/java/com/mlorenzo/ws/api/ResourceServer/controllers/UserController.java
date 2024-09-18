package com.mlorenzo.ws.api.ResourceServer.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mlorenzo.ws.api.ResourceServer.response.UserRest;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@GetMapping("/status/check")
	public String status(@Value("${local.server.port}") String port) {
		return "Working on port: " + port;
	}
	
	// Con esta anotación, se debe usar el prefijo "ROLE_" para los roles
	//@Secured("ROLE_developer") // Esta anotación es menos poderosa que la anotación @PreAuthorize porque no permite el uso de expresiones. Las anotaciones @PreAuthorize y @PostAuthorize son más modernas que la anotación @Secured ya que permiten el uso de expresiones
	//@PreAuthorize("hasRole('developer')") // Spring Security maneja por debajo los roles que obtiene del token JWT usando el prefijo "ROLE_". Entonces, si usamos el método "hasRole" o el método "hasAnyRole" para el control de acceso a los recursos, no es necesario especificar el prefijo "ROLE_" en esos métodos porque ya lo tienen en cuenta
	// El servidor de autorización Keycloak utiliza el id del usuario como Subject del token JWT
	// "#userId" hace referencia al parámetro de entrada "userId" de este método handler
	// "#jwt" hace referencia al parámetro de entrada "jwt" de este método handler
	// La anotación @AuthenticationPrincipal mapea los detalles del usuario actual autenticado(también llamado Principal) con el objeto de tipo Jwt que se pasa como argumento de entrada a este método
	// El objeto de tipo Jwt nos permite acceder a los datos del token JWT, como por ejemplo, el token de acceso, sus claims(derechos proporcionados a dicho token JWT), etc...
	@PreAuthorize("hasAuthority('ROLE_developer') or #userId == #jwt.subject") // Spring Security maneja por debajo los roles que obtiene del token JWT usando el prefijo "ROLE_". Entonces, si usamos el método "hasAuthority" o el método "hasAnyAuthority" para el control de acceso a los recursos, es necesario especificar el prefijo "ROLE_" en esos métodos porque no lo tienen en cuenta
	@DeleteMapping(path = "/{id}")
	public String deleteUser(@PathVariable(name = "id") String userId, @AuthenticationPrincipal Jwt jwt) {
		return "Delete user with id=" + userId + " and JWT subject=" + jwt.getSubject();
	}
	
	// "returnObject" hace referencia al retorno de este método handler que es de tipo UserRest
	// "#jwt" hace referencia al parámetro de entrada "jwt" de este método handler
	@PostAuthorize("returnObject.userId == #jwt.subject") // Con esta anotación, primero se ejecuta el método y, antes de devolverse el dato de retorno, se evalua la expresión de esta anotación. Si la expresión se evalua a true, se devuelve el dato de retorno y, en caso contrario, no se devuelve
	@GetMapping(path = "/{id}")
	public UserRest getUser(@PathVariable(name = "id") String userId, @AuthenticationPrincipal Jwt jwt) {
		return new UserRest("7454199b-47c9-41fb-8af4-22fa4a0dbc39", "Manuel", "Lorenzo");
	}

}
