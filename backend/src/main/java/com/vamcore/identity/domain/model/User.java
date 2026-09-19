package com.vamcore.identity.domain.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Aggregate root que representa un usuario de la plataforma
 * (ver sección 9 del documento de arquitectura - Bounded Context Identity & Access).
 *
 * Un User siempre pertenece a un Tenant (ver ADR-005 Multi-tenancy).
 */
public class User {

    private final UUID id;
    private final UUID tenantId;
    private String email;
    private String passwordHash;
    private String fullName;
    private UserStatus status;
    private final Set<String> roles;
    private final Instant createdAt;

    private User(UUID id, UUID tenantId, String email, String passwordHash, String fullName,
                 UserStatus status, Set<String> roles, Instant createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.status = status;
        this.roles = roles;
        this.createdAt = createdAt;
    }

    public static User create(UUID tenantId, String email, String passwordHash, String fullName) {
        Objects.requireNonNull(tenantId, "tenantId es obligatorio");
        Objects.requireNonNull(email, "email es obligatorio");
        Objects.requireNonNull(passwordHash, "passwordHash es obligatorio");
        Set<String> defaultRoles = new HashSet<>();
        defaultRoles.add("USER");
        return new User(UUID.randomUUID(), tenantId, email, passwordHash, fullName,
            UserStatus.ACTIVE, defaultRoles, Instant.now());
    }

    public static User reconstitute(UUID id, UUID tenantId, String email, String passwordHash, String fullName,
                                     UserStatus status, Set<String> roles, Instant createdAt) {
        return new User(id, tenantId, email, passwordHash, fullName, status, new HashSet<>(roles), createdAt);
    }

    public void assignRole(String role) {
        this.roles.add(role);
    }

    public void revokeRole(String role) {
        this.roles.remove(role);
    }

    /** Reemplaza el conjunto completo de roles (ver UpdateUserRolesUseCase, solo ADMIN). */
    public void replaceRoles(Set<String> newRoles) {
        this.roles.clear();
        this.roles.addAll(newRoles);
    }

    public void lock() {
        this.status = UserStatus.LOCKED;
    }

    public void unlock() {
        this.status = UserStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    public UUID id() {
        return id;
    }

    public UUID tenantId() {
        return tenantId;
    }

    public String email() {
        return email;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public String fullName() {
        return fullName;
    }

    public UserStatus status() {
        return status;
    }

    public Set<String> roles() {
        return roles;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public enum UserStatus {
        ACTIVE, LOCKED, DISABLED
    }
}
