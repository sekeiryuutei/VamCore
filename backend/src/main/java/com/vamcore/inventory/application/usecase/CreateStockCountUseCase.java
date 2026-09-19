package com.vamcore.inventory.application.usecase;

import com.vamcore.inventory.application.command.CreateStockCountCommand;
import com.vamcore.inventory.domain.model.Product;
import com.vamcore.inventory.domain.model.Stock;
import com.vamcore.inventory.domain.model.StockCount;
import com.vamcore.inventory.domain.repository.ProductRepository;
import com.vamcore.inventory.domain.repository.StockCountRepository;
import com.vamcore.inventory.domain.repository.StockRepository;
import com.vamcore.inventory.domain.valueobject.Quantity;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registra un conteo físico (ver sección 56 - MVP de Inventario). Captura
 * la cantidad del sistema EN ESE MOMENTO junto a la cantidad contada, para
 * poder calcular la varianza sin importar si el stock cambia después
 * mientras el conteo sigue en DRAFT.
 */
@Service
public class CreateStockCountUseCase {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final StockCountRepository stockCountRepository;

    public CreateStockCountUseCase(ProductRepository productRepository, StockRepository stockRepository,
                                    StockCountRepository stockCountRepository) {
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
        this.stockCountRepository = stockCountRepository;
    }

    @Transactional
    public StockCount handle(CreateStockCountCommand command) {
        TenantId tenantId = TenantContext.get();

        Product product = productRepository.findById(tenantId.value(), command.productId())
            .orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND", "Producto no encontrado", HttpStatus.NOT_FOUND));

        Quantity systemQuantity = stockRepository.findByProductAndWarehouse(tenantId.value(), command.productId(), command.warehouseId())
            .map(Stock::quantity)
            .orElse(Quantity.zero(product.unitOfMeasure()));

        Quantity counted = Quantity.of(command.countedQuantity(), product.unitOfMeasure());

        StockCount stockCount = StockCount.create(tenantId.value(), command.productId(), command.warehouseId(), systemQuantity, counted);
        return stockCountRepository.save(stockCount);
    }
}
