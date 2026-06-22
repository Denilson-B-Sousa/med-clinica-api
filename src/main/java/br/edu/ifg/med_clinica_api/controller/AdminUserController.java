package br.edu.ifg.med_clinica_api.controller;

import br.edu.ifg.med_clinica_api.domain.bo.UserService;
import br.edu.ifg.med_clinica_api.domain.dto.user.UserStatusDTO;
import br.edu.ifg.med_clinica_api.domain.dto.user.UserStatusUpdateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<UserStatusDTO> updateStatus(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateDTO data
    ) {
        return ResponseEntity.ok(userService.updateStatus(userId, data));
    }
}
