package br.edu.ifg.med_clinica_api.controller;

import br.edu.ifg.med_clinica_api.domain.user.User;
import br.edu.ifg.med_clinica_api.domain.user.UserRepository;
import br.edu.ifg.med_clinica_api.domain.user.dto.UserDTO;
import br.edu.ifg.med_clinica_api.infra.security.DataToken;
import br.edu.ifg.med_clinica_api.infra.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth/login")
public class UserController {

    private final TokenService tokenService;
    private final AuthenticationManager manager;

    public UserController(TokenService tokenService, AuthenticationManager manager) {
        this.tokenService = tokenService;
        this.manager = manager;
    }

    @PostMapping
    public ResponseEntity<?> userLogin(@RequestBody @Valid UserDTO data) {

        var authenticationToken = new UsernamePasswordAuthenticationToken(
                data.email(),
                data.password()
        );
        var authentication = manager.authenticate(authenticationToken);

        var tokenJWT = tokenService.generateToken((User) authentication.getPrincipal());
        return ResponseEntity.ok(new DataToken(tokenJWT));
    }
}
