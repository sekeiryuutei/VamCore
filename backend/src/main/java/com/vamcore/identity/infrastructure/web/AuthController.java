package com.vamcore.identity.infrastructure.web;

import com.vamcore.identity.application.command.LoginCommand;
import com.vamcore.identity.application.command.RegisterUserCommand;
import com.vamcore.identity.application.dto.*;
import com.vamcore.identity.application.usecase.LoginUseCase;
import com.vamcore.identity.application.usecase.RegisterUserUseCase;
import com.vamcore.identity.domain.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Rutas públicas de autenticación (ver SecurityConfig - permitAll en /api/v1/auth/**).
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase, LoginUseCase loginUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterUserRequest request) {
        User user = registerUserUseCase.handle(
            new RegisterUserCommand(request.tenantId(), request.email(), request.password(), request.fullName())
        );
        return UserResponse.from(user);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        String token = loginUseCase.handle(new LoginCommand(request.email(), request.password()));
        return LoginResponse.of(token);
    }
}
