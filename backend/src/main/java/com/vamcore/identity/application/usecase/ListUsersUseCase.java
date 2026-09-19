package com.vamcore.identity.application.usecase;

import com.vamcore.identity.domain.model.User;
import com.vamcore.identity.domain.repository.UserRepository;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListUsersUseCase {

    private final UserRepository userRepository;

    public ListUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> handle() {
        TenantId tenantId = TenantContext.get();
        return userRepository.findAllByTenantId(tenantId.value());
    }
}
