package br.edu.ifg.med_clinica_api.controller;

import br.edu.ifg.med_clinica_api.model.bo.PatientService;
import br.edu.ifg.med_clinica_api.model.entity.User;
import br.edu.ifg.med_clinica_api.model.bo.UserService;
import br.edu.ifg.med_clinica_api.model.dto.user.UserDTO;
import br.edu.ifg.med_clinica_api.infra.security.DataToken;
import br.edu.ifg.med_clinica_api.infra.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class UserController {

    private final TokenService tokenService;
    private final AuthenticationManager manager;
    private final PatientService patientService;
    private final UserService userService;

    public UserController(TokenService tokenService, AuthenticationManager manager, PatientService patientService, UserService userService) {
        this.tokenService = tokenService;
        this.manager = manager;
        this.patientService = patientService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody @Valid UserDTO data) {

        var authenticationToken = new UsernamePasswordAuthenticationToken(
                data.email(),
                data.password()
        );
        var authentication = manager.authenticate(authenticationToken);

        var tokenJWT = tokenService.generateToken((User) authentication.getPrincipal());
        return ResponseEntity.ok(new DataToken(tokenJWT));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser(
            Authentication authentication
    ) {
        var email = authentication.getName();

        return ResponseEntity.ok(
                userService.getAuthenticatedUser(email)
        );
    }
}
