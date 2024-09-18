package com.mlorenzo.photoapp.OrdersWebOAuthClient;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class OrdersController {
	
	@Autowired
	private RestTemplate restTemplate;

	// A la anotación @RegisteredOAuth2AuthorizedClient le pasamos el id de registro del cliente que hemos indicado en el archivo de propiedades
	@GetMapping({"/orders", "/"})
	public String getOrders(Model model,
			@RegisteredOAuth2AuthorizedClient("users-client-oidc") OAuth2AuthorizedClient authorizedClient) {
		
		// Obtenemos el token de acceso JWT
		// Otra alternativa para obtener el token de acceso JWT es haciéndolo a través del objeto de Authentication de Spring Security(Ver clase AlbumsController del proyecto PhotoAppWebClient)
		String jwtAccessToken = authorizedClient.getAccessToken().getTokenValue();
		
		// Obtenemos la lista de "Orders" del servidor de recurosos
		String url = "http://localhost:8091/orders";
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtAccessToken));
		ResponseEntity<List<Order>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<Order>>() {});
		
		List<Order> orders = responseEntity.getBody();
		model.addAttribute("orders", orders);
		return "orders-page";
	}
}
