package com.vamcore.organization.application.command;

/**
 * Representa la intención de registrar una nueva empresa en la plataforma.
 */
public record CreateTenantCommand(String name, String taxId) {
}
