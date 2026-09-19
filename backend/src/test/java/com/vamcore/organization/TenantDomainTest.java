package com.vamcore.organization;

import com.vamcore.organization.domain.model.Tenant;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TenantDomainTest {

    @Test
    void shouldCreateActiveTenant() {
        Tenant tenant = Tenant.create("Empresa ABC", "900123456-7");

        assertThat(tenant.name()).isEqualTo("Empresa ABC");
        assertThat(tenant.status()).isEqualTo(Tenant.TenantStatus.ACTIVE);
    }

    @Test
    void shouldSuspendAndReactivateTenant() {
        Tenant tenant = Tenant.create("Empresa ABC", "900123456-7");

        tenant.suspend();
        assertThat(tenant.status()).isEqualTo(Tenant.TenantStatus.SUSPENDED);

        tenant.reactivate();
        assertThat(tenant.status()).isEqualTo(Tenant.TenantStatus.ACTIVE);
    }
}
