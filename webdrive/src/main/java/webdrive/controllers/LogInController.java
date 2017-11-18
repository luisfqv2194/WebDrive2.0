package webdrive.controllers; // Para leer los controladores el paquete debe ser un subpaquete del paquete principal

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogInController {

	@RequestMapping("/login") // Todos los métodos
	public String sayHi() {
		return "hi";
	}
}
