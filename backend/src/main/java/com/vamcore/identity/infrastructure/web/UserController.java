package com.vamcore.identity.infrastructure.web;

import com.vamcore.identity.application.dto.UpdateUserRolesRequest;
import com.vamcore.identity.application.dto.UserResponse;
import com.vamcore.identity.application.usecase.ListUsersUseCase;
import com.vamcore.identity.application.usecase.UpdateUserRolesUseCase;
import com.vamcore.identity.domain.model.User;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Gestión básica de usuarios del tenant (ver sección 32 del documento de
 * arquitectura). Reservado a ADMIN vía el permiso USER_MANAGE.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final ListUsersUseCase listUsersUseCase;
    private final UpdateUserRolesUseCase updateUserRolesUseCase;

    public UserController(ListUsersUseCase listUsersUseCase, UpdateUserRolesUseCase updateUserRolesUseCase) {
        this.listUsersUseCase = listUsersUseCase;
        this.updateUserRolesUseCase = updateUserRolesUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public List<UserResponse> list() {
        return listUsersUseCase.handle().stream().map(UserResponse::from).toList();
    }

    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public UserResponse updateRoles(@PathVariable UUID userId, @Valid @RequestBody UpdateUserRolesRequest request) {
        User user = updateUserRolesUseCase.handle(userId, request.roles());
        return UserResponse.from(user);
    }
}
