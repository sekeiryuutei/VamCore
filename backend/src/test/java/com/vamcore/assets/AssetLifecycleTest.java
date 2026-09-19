package com.vamcore.assets;

import com.vamcore.assets.domain.model.Asset;
import com.vamcore.assets.domain.valueobject.AssetCode;
import com.vamcore.assets.domain.valueobject.AssetStatus;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.vamcore.assets.domain.valueobject.AssetStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssetLifecycleTest {

    private Asset newAsset() {
        return Asset.acquire(UUID.randomUUID(), AssetCode.of("LAPTOP-001"), "Laptop Lenovo", "Cómputo", "SN123", null, null);
    }

    @Test
    void shouldStartInAcquiredStatus() {
        assertThat(newAsset().status()).isEqualTo(ACQUIRED);
    }

    @Test
    void shouldFollowHappyPathLifecycle() {
        Asset asset = newAsset();

        asset.changeStatus(IN_STORAGE);
        assertThat(asset.status()).isEqualTo(IN_STORAGE);

        asset.assignTo(UUID.randomUUID());
        assertThat(asset.status()).isEqualTo(ASSIGNED);
        assertThat(asset.currentAssigneeId()).isNotNull();

        asset.changeStatus(IN_USE);
        asset.changeStatus(IN_MAINTENANCE);
        asset.changeStatus(IN_USE);
        asset.changeStatus(RETIRED);
        asset.changeStatus(DISPOSED);

        assertThat(asset.status()).isEqualTo(DISPOSED);
    }

    @Test
    void shouldRejectSkippingStatesDirectlyFromAcquiredToAssigned() {
        Asset asset = newAsset();

        assertThatThrownBy(() -> asset.assignTo(UUID.randomUUID()))
            .isInstanceOf(BusinessException.class)
            .satisfies(ex -> assertThat(((BusinessException) ex).code()).isEqualTo("INVALID_ASSET_TRANSITION"));
    }

    @Test
    void shouldNeverAllowReactivatingADisposedAsset() {
        // Ver sección 16 del documento de arquitectura: DISPOSED -> IN_USE
        // debe estar prohibido salvo una operación explícita de reversión.
        Asset asset = newAsset();
        asset.changeStatus(IN_STORAGE);
        asset.changeStatus(RETIRED);
        asset.changeStatus(DISPOSED);

        assertThatThrownBy(() -> asset.changeStatus(IN_USE)).isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> asset.changeStatus(IN_STORAGE)).isInstanceOf(BusinessException.class);
    }
}
