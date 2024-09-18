package com.mlorenzo.ws.clients.photoappwebclient.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.mlorenzo.ws.clients.photoappwebclient.response.AlbumRest;

@Controller
public class AlbumsController {
	
	@Autowired
	private OAuth2AuthorizedClientService oauth2ClientService; // Usamos este servicio del cliente OAuth2 de Spring para poder obtener el token de acceso JWT

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private WebClient webClient;
	
	// La anotación @AuthenticationPrincipal nos permite mapear los datos del usuario autenticado con el objeto de tipo OidcUser(openID)
	// Un objeto de tipo OidcUser representa un usuario registrado en el proveedor OAuth2(servidor de autorización) que actualmente está autenticado(openID)
	@GetMapping("albums-v1")
	public String getAlbumsWithRestTemplate(Model model, @AuthenticationPrincipal OidcUser principal, Authentication authentication) {
		
		// Tenemos 2 opciones para obtener el objeto de tipo Authentication de Spring Security:
		// Una opción es usar un parámetro de entrada en este método de tipo Authentication para que Spring lo inyecte automáticamente
		// La otra opción, es obtener el objeto de tipo Authentication accediendo al contexto de seguridad de Spring Security mediante esta expresión 
		// Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// Usamos el objeto de tipo Authentication junto con el servicio del cliente OAuth2 "oauth2ClientService" para obtener el token de acceso JWT
		// Otra alternativa para obtener el token de acceso JWT es haciéndolo a través de la anotación @RegisteredOAuth2AuthorizedClient(Ver clase OrdersController del proyecto OrdersWebOauthClient)
		OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken)authentication;
		OAuth2AuthorizedClient oauth2Client = oauth2ClientService.loadAuthorizedClient(oauth2Token.getAuthorizedClientRegistrationId(), oauth2Token.getName());
		String jwtAccessToken = oauth2Client.getAccessToken().getTokenValue();
		System.out.println("jwtAccessToken = " + jwtAccessToken);
		
		System.out.println("principal = " + principal);
		
		// Obtiene el ID token(openID)
		OidcIdToken idToken = principal.getIdToken();
		// Obtiene el valor del ID token(openID)
		String idTokenValue = idToken.getTokenValue();
		System.out.println("idTokenValue = " + idTokenValue);
		
		// Url para obtener el listado de albums usando nuestro Api Gateway(puerto 8082)
		String url = "http://localhost:8082/albums";
		// Cabecera para añadir el token de acceso JWT a la petición http que se realizará a nuestro servidor de recursos de albums para que nos dé el listado
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtAccessToken);
		HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
		
		// Realiza la petición http a nuestro servidor de recursos de albums para que nos dé el listado a través del cliente RestTemplate
		ResponseEntity<List<AlbumRest>> response =  restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<AlbumRest>>() {});
		List<AlbumRest> albums = response.getBody();
		
		model.addAttribute("albums", albums);
		
		/*AlbumRest album1 = new AlbumRest();
		album1.setAlbumId("albumOne");
		album1.setAlbumTitle("Album one title");
		album1.setAlbumDescription("Album one description");
		album1.setAlbumUrl("http://localhost:8082/albums/1");
		
		AlbumRest album2 = new AlbumRest();
		album2.setAlbumId("albumTwo");
		album2.setAlbumTitle("Album two title");
		album2.setAlbumDescription("Album two description");
		album2.setAlbumUrl("http://localhost:8082/albums/2");
		
		model.addAttribute("albums", Arrays.asList(album1, album2));*/
		
		return "albums";
	}
	
	// Como este método handler hace uso del cliente Web Client lugar de RestTemplate, este cliente Web Client se ha configurado previamente para que se añada de forma automática el token JWT en cada petición http(ver clase "PhotoAppWebClientApplication") y, por lo tanto, no hace falta añadir en este método handler código para obtener el token JWT como sí hay que hacer en el método handler anterior que usa RestTemplate  
	@GetMapping("albums-v2")
	public String getAlbumsWithWebClient(Model model) {
		
		// Url para obtener el listado de albums usando nuestro Api Gateway(puerto 8082)
		String url = "http://localhost:8082/albums";
		
		// El cliente Web Client por defecto es un cliente asíncrono no bloqueante
		// Usando este método "block", hacemos que este cliente sea síncrono y bloqueante
		// Usar este cliente de forma síncrona y bloqueante en este caso es válido porque no estamos ante un Stack Reactivo, es decir, los servidores de recursos con los que vamos a comunicarnos no son reactivos
		List<AlbumRest> albums = webClient.get()
				.uri(url)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<AlbumRest>>() {})
				.block();
		
		model.addAttribute("albums", albums);
		
		return "albums";
	}
}
