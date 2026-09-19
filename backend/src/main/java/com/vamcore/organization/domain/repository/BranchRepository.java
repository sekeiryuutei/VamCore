package com.vamcore.organization.domain.repository;

import com.vamcore.organization.domain.model.Branch;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchRepository {

    Branch save(Branch branch);

    Optional<Branch> findById(UUID id);

    List<Branch> findAllByTenantId(UUID tenantId);
}
