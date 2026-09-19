package com.vamcore.identity.application.command;

import java.util.UUID;

public record RegisterUserCommand(UUID tenantId, String email, String rawPassword, String fullName) {
}
