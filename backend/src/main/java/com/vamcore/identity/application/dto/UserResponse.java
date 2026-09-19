package com.vamcore.identity.application.dto;

import com.vamcore.identity.domain.model.User;

import java.util.Set;
import java.util.UUID;

public record UserResponse(UUID id, UUID tenantId, String email, String fullName, String status, Set<String> roles) {

    public static UserResponse from(User user) {
        return new UserResponse(user.id(), user.tenantId(), user.email(), user.fullName(), user.status().name(), user.roles());
    }
}
