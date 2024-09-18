package com.mlorenzo.clients.sociallogin.SocialLoginWebClient.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {
	
	// La anotación @AuthenticationPrincipal nos permite mapear los datos del actual usuario autenticado al objeto de tipo OAuth2User que se le pasa como argumento de entrada a este método handler
	@GetMapping("/home")
	public String displayHomePage(Model model, @AuthenticationPrincipal OAuth2User principal) {
		if(principal != null) {
			// Obtenemos el nombre del actual usuario autenticado
			String name = principal.getAttribute("name");
			model.addAttribute("name", name);
		}
		
		return "home";
	}

}
