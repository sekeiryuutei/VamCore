package com.vamcore.identity.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RegisterUserRequest(
    @NotNull UUID tenantId,
    @NotBlank @Email String email,
    @NotBlank String password,
    String fullName
) {
}
