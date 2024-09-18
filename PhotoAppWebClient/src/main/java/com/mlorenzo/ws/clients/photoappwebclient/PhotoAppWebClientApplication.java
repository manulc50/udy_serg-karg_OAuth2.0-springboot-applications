package com.mlorenzo.ws.clients.photoappwebclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class PhotoAppWebClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotoAppWebClientApplication.class, args);
	}
	
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	
	// El parámetro de entrada de tipo ClientRegistrationRepository representa el repositorio donde están registrados los clientes OAuth2. Por defecto, la autoconfiguración de Spring Boot crea un bean del tipo ClientRegistrationRepository usando un repositorio en memoria a partir de las propiedades sobre los clientes OAuth2 indicadas en los archivos de propiedades como "application.properties"
	// El parámetro de entrada de tipo OAuth2AuthorizedClientRepository almacena información sobre el cliente(como esta aplicación web) que acaba de ser autorizado
	// Ambos parámetros de entrada son inyectados automáticamente por Spring
	// Estos 2 parámetros de entrada son necesarios para crear el objeto de tipo ServletOAuth2AuthorizedClientExchangeFilterFunction que es necesario para que el cliente Web Cliente soporte OAuth2
	@Bean
	public WebClient webClient(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository) {
		ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 = new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository, oAuth2AuthorizedClientRepository);
		// Para que automáticamente detecte la configuración OAuth2 del cliente en función del actual objeto de tipo Authentication de Spring Security
		oauth2.setDefaultOAuth2AuthorizedClient(true);
		
		return WebClient.builder()
				// Esta configuración hace que el token de acceso JWT se incluya  automáticamente en cada petición http que se haga a través de este cliente Web Client
				// Nota: No usar este cliente Web Client con otras aplicaciones de terceros porque, como se incluye el token de acceso JWT en cada petición http, este token JWT también llega a estas aplicaciones de terceros y puede haber información comprometida o sensible dentro de dicho token JWT
				.apply(oauth2.oauth2Configuration())
				.build();
	}

}
