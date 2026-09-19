package com.vamcore.identity.application.usecase;

import com.vamcore.identity.application.command.LoginCommand;
import com.vamcore.identity.domain.model.User;
import com.vamcore.identity.domain.repository.PermissionRepository;
import com.vamcore.identity.domain.repository.UserRepository;
import com.vamcore.identity.infrastructure.security.JwtTokenProvider;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PermissionRepository permissionRepository;

    public LoginUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder,
                         JwtTokenProvider jwtTokenProvider, PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.permissionRepository = permissionRepository;
    }

    public String handle(LoginCommand command) {
        User user = userRepository.findByEmail(command.email())
            .orElseThrow(() -> new BusinessException("INVALID_CREDENTIALS", "Credenciales inválidas", HttpStatus.UNAUTHORIZED));

        if (!user.isActive()) {
            throw new BusinessException("USER_LOCKED", "El usuario está bloqueado o deshabilitado", HttpStatus.FORBIDDEN);
        }

        if (!passwordEncoder.matches(command.rawPassword(), user.passwordHash())) {
            throw new BusinessException("INVALID_CREDENTIALS", "Credenciales inválidas", HttpStatus.UNAUTHORIZED);
        }

        Set<String> permissions = permissionRepository.findPermissionCodesByRoles(user.roles());

        return jwtTokenProvider.generateToken(user, permissions);
    }
}
