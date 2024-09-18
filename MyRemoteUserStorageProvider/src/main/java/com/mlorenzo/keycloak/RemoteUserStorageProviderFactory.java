package com.mlorenzo.keycloak;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class RemoteUserStorageProviderFactory implements UserStorageProviderFactory<RemoteUserStorageProvider> {

	private static final String PROVIDER_NAME = "my-remote-user-storage-provider";
	
	@Override
	public RemoteUserStorageProvider create(KeycloakSession session, ComponentModel model) {
		// "host.docker.internal" si ejecutamos el servidor Keycloak desde un contenedor de Docker
		return new RemoteUserStorageProvider(session, model, buildHttpClient("http://host.docker.internal:8099"));
	}

	@Override
	public String getId() {
		return PROVIDER_NAME;
	}
	
	// Método para construir un cliente RESTEasy Http Client para realizar las peticiones http
	private UsersApiService buildHttpClient(String uri) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		
		ResteasyWebTarget target = client.target(uri);
		
		return target.proxyBuilder(UsersApiService.class)
				.classloader(UsersApiService.class.getClassLoader())
				.build();
	}

}
