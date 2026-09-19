package com.vamcore.logistics.infrastructure.web;

import com.vamcore.logistics.application.command.AssignDeliveryCommand;
import com.vamcore.logistics.application.command.ChangeDeliveryStatusCommand;
import com.vamcore.logistics.application.command.CreateDeliveryCommand;
import com.vamcore.logistics.application.dto.*;
import com.vamcore.logistics.application.usecase.*;
import com.vamcore.logistics.domain.model.Delivery;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * API de VamTrack (ver sección 40 del doc de arquitectura: /api/v1/).
 */
@RestController
@RequestMapping("/api/v1/logistics/deliveries")
public class DeliveryController {

    private final CreateDeliveryUseCase createDeliveryUseCase;
    private final ListDeliveriesUseCase listDeliveriesUseCase;
    private final ChangeDeliveryStatusUseCase changeDeliveryStatusUseCase;
    private final AssignDeliveryUseCase assignDeliveryUseCase;
    private final GetDeliveryTrackingUseCase getDeliveryTrackingUseCase;

    public DeliveryController(CreateDeliveryUseCase createDeliveryUseCase, ListDeliveriesUseCase listDeliveriesUseCase,
                               ChangeDeliveryStatusUseCase changeDeliveryStatusUseCase, AssignDeliveryUseCase assignDeliveryUseCase,
                               GetDeliveryTrackingUseCase getDeliveryTrackingUseCase) {
        this.createDeliveryUseCase = createDeliveryUseCase;
        this.listDeliveriesUseCase = listDeliveriesUseCase;
        this.changeDeliveryStatusUseCase = changeDeliveryStatusUseCase;
        this.assignDeliveryUseCase = assignDeliveryUseCase;
        this.getDeliveryTrackingUseCase = getDeliveryTrackingUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('DELIVERY_MANAGE')")
    public DeliveryResponse create(@Valid @RequestBody CreateDeliveryRequest request) {
        Delivery delivery = createDeliveryUseCase.handle(
            new CreateDeliveryCommand(request.deliveryCode(), request.customerName(), request.destinationAddress())
        );
        return DeliveryResponse.from(delivery);
    }

    @GetMapping
    public List<DeliveryResponse> list() {
        return listDeliveriesUseCase.handle().stream().map(DeliveryResponse::from).toList();
    }

    @PostMapping("/{deliveryId}/status")
    @PreAuthorize("hasAuthority('DELIVERY_MANAGE')")
    public DeliveryResponse changeStatus(@PathVariable UUID deliveryId, @Valid @RequestBody ChangeDeliveryStatusRequest request) {
        Delivery delivery = changeDeliveryStatusUseCase.handle(
            new ChangeDeliveryStatusCommand(deliveryId, request.newStatus(), request.notes())
        );
        return DeliveryResponse.from(delivery);
    }

    @PostMapping("/{deliveryId}/assign")
    @PreAuthorize("hasAuthority('DELIVERY_MANAGE')")
    public DeliveryResponse assign(@PathVariable UUID deliveryId, @Valid @RequestBody AssignDeliveryRequest request) {
        Delivery delivery = assignDeliveryUseCase.handle(
            new AssignDeliveryCommand(deliveryId, request.driverId(), request.vehicleId())
        );
        return DeliveryResponse.from(delivery);
    }

    @GetMapping("/{deliveryId}/tracking")
    public List<TrackingEventResponse> tracking(@PathVariable UUID deliveryId) {
        return getDeliveryTrackingUseCase.handle(deliveryId).stream().map(TrackingEventResponse::from).toList();
    }
}
