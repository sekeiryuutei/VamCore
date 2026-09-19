package com.vamcore.inventory.application.usecase;

import com.vamcore.inventory.domain.model.Product;
import com.vamcore.inventory.domain.repository.ProductRepository;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListProductsUseCase {

    private final ProductRepository productRepository;

    public ListProductsUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> handle() {
        TenantId tenantId = TenantContext.get();
        return productRepository.findAllByTenant(tenantId.value());
    }
}
