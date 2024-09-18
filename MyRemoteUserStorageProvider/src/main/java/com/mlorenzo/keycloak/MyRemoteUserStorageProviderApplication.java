package com.mlorenzo.keycloak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Nota: Este proyecto es un proveedor personalizado para el servidor Keycloak que obtiene datos de usuarios autenticados desde una base de datos remota(otra que no es propia del servidor Keycloak)
//       En nuestro caso, la base de datos remota es una base de datos H2 que se encuentra en el proyecto "legacyusersservice"
// 		 Existen 2 opciones: Una es que este proyecto se comunique directamente con la base de datos remota y la otra es que se comunique con un Web Service(proyecto "legacyusersservice") mediante peticiones http
// 		 para que éste se encargue de obtener los datos de la base de datos remota
// 		 En nuestro caso, usamos la segunda opción

@SpringBootApplication
public class MyRemoteUserStorageProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyRemoteUserStorageProviderApplication.class, args);
	}

}
