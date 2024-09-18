package com.mlorenzo.keycloak;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/users") // Ruta base donde vamos a realizar las peticiones http
@Consumes(MediaType.APPLICATION_JSON) // Añade la cabecera "Accept" con el valor "application/json" en todas las peticiones http que se hagan, es decir, se espera recibir un Json en todas las peticiones http
public interface UsersApiService {
	
	// Nota: En nuestro caso, el username es el email del usuario ya que en el proceso de login en el servidor Keycloak se introduce el email del usuario en el campo del username
	
	// Realiza una petición http Get a la ruta "/users/{username}" y el Json recibido se mapea a un objeto de tipo User
	@GET
	@Path("/{username}") // La ruta final es "/users/{userName}"
	User getUserDetails(@PathParam("username") String username);
	
	// Realiza una petición http Post a la ruta  "/users/{username}/verify-password" y el Json recibido se mapea a un objeto de tipo VerifyPasswordResponse
	@POST
	@Produces(MediaType.APPLICATION_JSON) // Para indicar que el tipo de contenido del cuerpo de esta petición http POST es un Json
	@Path("/{username}/verify-password") // La ruta final es "/users/{username}/verify-password"
	VerifyPasswordResponse verifyUserPassword(@PathParam("username") String name, String password);
}
