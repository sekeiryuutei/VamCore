package com.vamcore.identity.infrastructure.persistence;

import com.vamcore.identity.domain.model.User;
import com.vamcore.identity.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository jpaRepository;

    public UserRepositoryAdapter(SpringDataUserRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = new UserJpaEntity(
            user.id(), user.tenantId(), user.email(), user.passwordHash(),
            user.fullName(), user.status().name(), user.roles(), user.createdAt()
        );
        jpaRepository.save(entity);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmailAndTenantId(String email, UUID tenantId) {
        return jpaRepository.findByEmailAndTenantId(email, tenantId).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public boolean existsByEmailAndTenantId(String email, UUID tenantId) {
        return jpaRepository.existsByEmailAndTenantId(email, tenantId);
    }

    @Override
    public boolean existsAnyByTenantId(UUID tenantId) {
        return jpaRepository.existsByTenantId(tenantId);
    }

    @Override
    public List<User> findAllByTenantId(UUID tenantId) {
        return jpaRepository.findAllByTenantId(tenantId).stream().map(this::toDomain).toList();
    }

    private User toDomain(UserJpaEntity entity) {
        return User.reconstitute(
            entity.getId(), entity.getTenantId(), entity.getEmail(), entity.getPasswordHash(),
            entity.getFullName(), User.UserStatus.valueOf(entity.getStatus()), entity.getRoles(), entity.getCreatedAt()
        );
    }
}
