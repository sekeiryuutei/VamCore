package com.vamcore.identity.application.usecase;

import com.vamcore.identity.domain.model.User;
import com.vamcore.identity.domain.repository.UserRepository;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

/**
 * Cambia los roles de un usuario del mismo tenant (ver permiso USER_MANAGE,
 * reservado a ADMIN — sección 32 del documento de arquitectura).
 */
@Service
public class UpdateUserRolesUseCase {

    private final UserRepository userRepository;

    public UpdateUserRolesUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User handle(UUID userId, Set<String> newRoles) {
        TenantId tenantId = TenantContext.get();

        User user = userRepository.findById(userId)
            .filter(u -> u.tenantId().equals(tenantId.value()))
            .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "Usuario no encontrado en este tenant", HttpStatus.NOT_FOUND));

        user.replaceRoles(newRoles);
        return userRepository.save(user);
    }
}
