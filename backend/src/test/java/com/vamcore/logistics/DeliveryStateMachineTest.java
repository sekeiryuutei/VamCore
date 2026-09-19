package com.vamcore.logistics;

import com.vamcore.logistics.domain.model.Delivery;
import com.vamcore.logistics.domain.valueobject.DeliveryCode;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.vamcore.logistics.domain.valueobject.DeliveryStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeliveryStateMachineTest {

    private Delivery newDelivery() {
        return Delivery.create(UUID.randomUUID(), DeliveryCode.of("DEL-001"), "Juan Pérez", "Cali, Colombia");
    }

    @Test
    void shouldFollowHappyPathToDelivered() {
        Delivery delivery = newDelivery();

        delivery.changeStatus(CONFIRMED);
        delivery.changeStatus(PREPARING);
        delivery.changeStatus(READY);
        delivery.assignTo(UUID.randomUUID(), UUID.randomUUID());
        assertThat(delivery.status()).isEqualTo(ASSIGNED);

        delivery.changeStatus(DISPATCHED);
        delivery.changeStatus(IN_TRANSIT);
        delivery.changeStatus(DELIVERED);

        assertThat(delivery.status()).isEqualTo(DELIVERED);
    }

    @Test
    void shouldAllowFailedThenReturnedFlow() {
        Delivery delivery = newDelivery();
        delivery.changeStatus(CONFIRMED);
        delivery.changeStatus(PREPARING);
        delivery.changeStatus(READY);
        delivery.assignTo(UUID.randomUUID(), UUID.randomUUID());
        delivery.changeStatus(DISPATCHED);
        delivery.changeStatus(IN_TRANSIT);

        delivery.changeStatus(FAILED);
        delivery.changeStatus(RETURNED);

        assertThat(delivery.status()).isEqualTo(RETURNED);
    }

    @Test
    void shouldAllowCancellationOnlyBeforePreparation() {
        Delivery delivery = newDelivery();
        delivery.changeStatus(CANCELLED);
        assertThat(delivery.status()).isEqualTo(CANCELLED);
    }

    @Test
    void shouldRejectSkippingStatesDirectlyToDispatched() {
        Delivery delivery = newDelivery();

        assertThatThrownBy(() -> delivery.changeStatus(DISPATCHED))
            .isInstanceOf(BusinessException.class)
            .satisfies(ex -> assertThat(((BusinessException) ex).code()).isEqualTo("INVALID_DELIVERY_TRANSITION"));
    }

    @Test
    void shouldNotAllowAnyTransitionOutOfDelivered() {
        Delivery delivery = newDelivery();
        delivery.changeStatus(CONFIRMED);
        delivery.changeStatus(PREPARING);
        delivery.changeStatus(READY);
        delivery.assignTo(UUID.randomUUID(), UUID.randomUUID());
        delivery.changeStatus(DISPATCHED);
        delivery.changeStatus(IN_TRANSIT);
        delivery.changeStatus(DELIVERED);

        assertThatThrownBy(() -> delivery.changeStatus(RETURNED)).isInstanceOf(BusinessException.class);
    }
}
