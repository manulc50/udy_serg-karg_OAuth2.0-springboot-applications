package com.mlorenzo.photoapp.oauthServer;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class AuthorizationServerConfiguration {

	// Método para registrar clientes(En este caso, los datos de los clientes se almacenan en memoria)
	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		RegisteredClient registeredClient = RegisteredClient
				.withId(UUID.randomUUID().toString())
				.clientId("client1")
				// Hay 2 tipos de clientes; públicos y confidenciales. En este caso, este cliente es confidencial porque estamos especificando una clave para su registro y es necesario indicarla durante la autenticación cuando se solicite el token JWT
				// Los clientes públicos, como por ejemplo una aplicación de tipo "Single Page JavaScript application", no utilizan claves secretas
				.clientSecret("{noop}myClientSecretValue")
				// Indicamos el método a usar para enviar las credenciales de este cliente(En este caso, indicamos que las credenciales se envían usando Autenticación Básica) en la petición http Post para solicitar el token JWT de acceso
				// Si se usa la opción "CLIENT_SECRET_POST", en lugar de usar Autenticación Básica para el envío de las credenciales del cliente, dichas credenciales van en los campos del formulario de la petición http Post
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				// Nota: No funciona si se pone "localhost" en vez de la ip "127.0.0.1"
				.redirectUri("http://127.0.0.1:8080/login/oauth2/code/users-client-oidc")
				.redirectUri("http://127.0.0.1:8080/authorized")
				.scope(OidcScopes.OPENID) // Scope predefinido
				.scope("read") // Scope personalizado
				// Habilitamos página de consentimiento
				//.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
				.build();
		
		return new InMemoryRegisteredClientRepository(registeredClient);
	}
	
	@Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        return http.formLogin(Customizer.withDefaults()).build();
    }
	
	@Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder()
                //.issuer("http://auth-server:8000")
        		.issuer("http://localhost:8000")
                .build();
    }
	
	// El código que hay a continuación crea beans de Spring para generar tokens JWT de acceso usando el algoritmo RSA para la firma y validación de los tokens
	// La clave privada RSA se usa para la firma del token JWT y la clave pública RSA se usa para la validación del token JWT
	
	@Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);

        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    private static RSAKey generateRsa() throws NoSuchAlgorithmException {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    private static KeyPair generateRsaKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);

        return keyPairGenerator.generateKeyPair();
    }
}
