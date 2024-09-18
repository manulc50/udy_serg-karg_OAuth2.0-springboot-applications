package com.mlorenzo.clients.sociallogin.SocialLoginWebClient.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexPageController {

	@GetMapping // Si no se especifica ninguna ruta en esta anotación, por defecto se usa la ruta raíz "/"
	public String displayIndexPage() {
		return "index";
	}
}
