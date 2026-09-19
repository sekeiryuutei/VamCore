package com.vamcore.reporting.application.usecase;

import com.vamcore.assets.domain.model.Asset;
import com.vamcore.assets.domain.repository.AssetRepository;
import com.vamcore.audit.domain.model.AuditLog;
import com.vamcore.audit.domain.repository.AuditLogRepository;
import com.vamcore.inventory.domain.repository.ProductRepository;
import com.vamcore.inventory.domain.repository.WarehouseRepository;
import com.vamcore.logistics.domain.model.Delivery;
import com.vamcore.logistics.domain.repository.DeliveryRepository;
import com.vamcore.reporting.domain.model.PlatformSummary;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Compone un resumen de solo lectura a través de VamStock, VamAsset y
 * VamTrack (ver sección 26 - CQRS selectivo: "dashboards" es exactamente
 * el caso de uso que el documento de arquitectura señala como candidato).
 *
 * Depende de los PUERTOS de dominio de los otros módulos (no de su
 * infraestructura JPA), lo cual es una composición de solo lectura
 * razonable para esta etapa — el día que el volumen lo justifique, esto
 * se reemplaza por una proyección/tabla de lectura materializada sin
 * tocar los módulos de escritura.
 */
@Service
public class GetPlatformSummaryUseCase {

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final AssetRepository assetRepository;
    private final DeliveryRepository deliveryRepository;
    private final AuditLogRepository auditLogRepository;

    public GetPlatformSummaryUseCase(ProductRepository productRepository, WarehouseRepository warehouseRepository,
                                      AssetRepository assetRepository, DeliveryRepository deliveryRepository,
                                      AuditLogRepository auditLogRepository) {
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.assetRepository = assetRepository;
        this.deliveryRepository = deliveryRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public PlatformSummary handle() {
        TenantId tenantId = TenantContext.get();

        List<Asset> assets = assetRepository.findAllByTenant(tenantId.value());
        List<Delivery> deliveries = deliveryRepository.findAllByTenant(tenantId.value());

        return new PlatformSummary(
            productRepository.findAllByTenant(tenantId.value()).size(),
            warehouseRepository.findAllByTenant(tenantId.value()).size(),
            assets.size(),
            assets.stream().collect(Collectors.groupingBy(a -> a.status().name(), Collectors.counting())),
            deliveries.size(),
            deliveries.stream().collect(Collectors.groupingBy(d -> d.status().name(), Collectors.counting()))
        );
    }

    @Transactional(readOnly = true)
    public List<AuditLog> recentActivity(int limit) {
        TenantId tenantId = TenantContext.get();
        return auditLogRepository.findRecentByTenant(tenantId.value(), limit);
    }
}
