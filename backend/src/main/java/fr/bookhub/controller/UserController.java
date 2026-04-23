package fr.bookhub.controller;

import fr.bookhub.bo.User;
import fr.bookhub.dto.UserResponseDTO;
import fr.bookhub.service.AuthService;
import fr.bookhub.service.UserDetailsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthService authService;
    private final UserDetailsServiceImpl userDetailsService;

    public UserController(AuthService authService, UserDetailsServiceImpl userDetailsService) {
        this.authService = authService;
        this.userDetailsService = userDetailsService;
    }

    // Recuperer les info de l'USER
    @GetMapping("/info")
    public ResponseEntity<UserResponseDTO> getInfoUser(
            @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(userDetailsService.mapToDTO(user));
    }


}
