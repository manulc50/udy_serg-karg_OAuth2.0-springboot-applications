package com.mlorenzo.keycloak;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.UserCredentialStore;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.storage.user.UserLookupProvider;

// Se necesita al menos las implementaciones de las interfaces "UserStorageProvider", "UserLookupProvider" y "CredentialInputValidator" para permitir que el servidor Keycloak localice
// los detalles de los usuarios desde un almacenamiento(base de datos) remoto y luego poder validar las credenciales de los usuarios 

public class RemoteUserStorageProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator {
	private KeycloakSession session;
	private ComponentModel model;
	private UsersApiService usersApiService;
	
	public RemoteUserStorageProvider(KeycloakSession session, ComponentModel model, UsersApiService usersApiService) {
		this.session = session;
		this.model = model;
		this.usersApiService = usersApiService;
	}

	@Override
	public void close() {
		
	}

	// Nota: Al menos debe implementarse alguno de los 3 siguientes métodos
	
	@Override
	public UserModel getUserById(String id, RealmModel realm) {
		return null;
	}
	
	// Nota: En nuestro caso, el username es el email del usuario ya que en el proceso de login en el servidor Keycloak se introduce el email del usuario en el campo del username

	@Override
	public UserModel getUserByUsername(String username, RealmModel realm) {
		User user = usersApiService.getUserDetails(username);
		
		if(user != null)
			return createUserModel(username, realm);
		
		return null;
	}

	@Override
	public UserModel getUserByEmail(String email, RealmModel realm) {
		return null;
	}

	@Override
	public boolean supportsCredentialType(String credentialType) {
		return PasswordCredentialModel.TYPE.equals(credentialType);
	}

	@Override
	public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
		if(!supportsCredentialType(credentialType))
			return false;
		
		return !getCredentialStore().getStoredCredentialsByType(realm, user, credentialType).isEmpty();
	}

	@Override
	public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
		VerifyPasswordResponse response = usersApiService.verifyUserPassword(user.getUsername(), credentialInput.getChallengeResponse());
		
		if(response == null)
			return false;
		
		return response.getResult();
	}
	
	private UserModel createUserModel(String username, RealmModel realm) {
		return new AbstractUserAdapter(session, realm, model) {
			
			@Override
			public String getUsername() {
				return username;
			}
		};
	}
	
	private UserCredentialStore getCredentialStore() {
		return session.userCredentialManager();
	}

}
