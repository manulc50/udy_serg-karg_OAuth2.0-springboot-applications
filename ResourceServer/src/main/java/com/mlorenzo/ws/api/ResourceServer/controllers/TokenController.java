package com.mlorenzo.ws.api.ResourceServer.controllers;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
public class TokenController {
	
	// La anotación @AuthenticationPrincipal mapea los detalles del usuario actual autenticado(también llamado Principal) con el objeto de tipo Jwt que se pasa como argumento de entrada a este método
	// El objeto de tipo Jwt nos permite acceder a los datos del token JWT, como por ejemplo, el token de acceso, sus claims(derechos proporcionados a dicho token JWT), etc...
	@GetMapping
	public Jwt getToken(@AuthenticationPrincipal Jwt jwt) {
		return jwt;
	}

}
