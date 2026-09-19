package com.vamcore.identity.application.usecase;

import com.vamcore.identity.application.command.RegisterUserCommand;
import com.vamcore.identity.domain.model.User;
import com.vamcore.identity.domain.repository.UserRepository;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User handle(RegisterUserCommand command) {
        if (userRepository.existsByEmailAndTenantId(command.email(), command.tenantId())) {
            throw new BusinessException(
                "USER_ALREADY_EXISTS",
                "Ya existe un usuario con ese email en este tenant",
                HttpStatus.CONFLICT
            );
        }

        boolean isFirstUserOfTenant = !userRepository.existsAnyByTenantId(command.tenantId());

        String hash = passwordEncoder.encode(command.rawPassword());
        User user = User.create(command.tenantId(), command.email(), hash, command.fullName());

        // Bootstrap: el primer usuario de una empresa necesita poder administrarla
        // (ver permisos ASSET_DISPOSE/TENANT_MANAGE/USER_MANAGE, reservados a ADMIN
        // en V7__permissions_foundation.sql). Los siguientes usuarios quedan como
        // USER; un ADMIN existente los puede promover más adelante.
        if (isFirstUserOfTenant) {
            user.assignRole("ADMIN");
        }

        return userRepository.save(user);
    }
}
