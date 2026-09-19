package com.vamcore.assets.infrastructure.web;

import com.vamcore.assets.application.command.AssignAssetCommand;
import com.vamcore.assets.application.command.ChangeAssetStatusCommand;
import com.vamcore.assets.application.command.CreateAssetCommand;
import com.vamcore.assets.application.dto.*;
import com.vamcore.assets.application.usecase.*;
import com.vamcore.assets.domain.model.Asset;
import com.vamcore.assets.domain.model.Maintenance;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * API de VamAsset (ver sección 40 del doc de arquitectura: /api/v1/).
 */
@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {

    private final CreateAssetUseCase createAssetUseCase;
    private final ListAssetsUseCase listAssetsUseCase;
    private final ChangeAssetStatusUseCase changeAssetStatusUseCase;
    private final AssignAssetUseCase assignAssetUseCase;
    private final GetAssetHistoryUseCase getAssetHistoryUseCase;
    private final GetAssetDepreciationUseCase getAssetDepreciationUseCase;
    private final ScheduleMaintenanceUseCase scheduleMaintenanceUseCase;
    private final CompleteMaintenanceUseCase completeMaintenanceUseCase;
    private final ListMaintenanceUseCase listMaintenanceUseCase;

    public AssetController(CreateAssetUseCase createAssetUseCase, ListAssetsUseCase listAssetsUseCase,
                            ChangeAssetStatusUseCase changeAssetStatusUseCase, AssignAssetUseCase assignAssetUseCase,
                            GetAssetHistoryUseCase getAssetHistoryUseCase, GetAssetDepreciationUseCase getAssetDepreciationUseCase,
                            ScheduleMaintenanceUseCase scheduleMaintenanceUseCase, CompleteMaintenanceUseCase completeMaintenanceUseCase,
                            ListMaintenanceUseCase listMaintenanceUseCase) {
        this.createAssetUseCase = createAssetUseCase;
        this.listAssetsUseCase = listAssetsUseCase;
        this.changeAssetStatusUseCase = changeAssetStatusUseCase;
        this.assignAssetUseCase = assignAssetUseCase;
        this.getAssetHistoryUseCase = getAssetHistoryUseCase;
        this.getAssetDepreciationUseCase = getAssetDepreciationUseCase;
        this.scheduleMaintenanceUseCase = scheduleMaintenanceUseCase;
        this.completeMaintenanceUseCase = completeMaintenanceUseCase;
        this.listMaintenanceUseCase = listMaintenanceUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ASSET_MANAGE')")
    public AssetResponse create(@Valid @RequestBody CreateAssetRequest request) {
        Asset asset = createAssetUseCase.handle(new CreateAssetCommand(
            request.assetCode(), request.name(), request.category(), request.serialNumber(),
            request.acquisitionCost(), request.currency(), request.usefulLifeMonths()
        ));
        return AssetResponse.from(asset);
    }

    @GetMapping
    public List<AssetResponse> list() {
        return listAssetsUseCase.handle().stream().map(AssetResponse::from).toList();
    }

    @PostMapping("/{assetId}/status")
    @PreAuthorize("hasAuthority('ASSET_MANAGE')")
    public AssetResponse changeStatus(@PathVariable UUID assetId, @Valid @RequestBody ChangeAssetStatusRequest request) {
        Asset asset = changeAssetStatusUseCase.handle(new ChangeAssetStatusCommand(assetId, request.newStatus()));
        return AssetResponse.from(asset);
    }

    @PostMapping("/{assetId}/assign")
    @PreAuthorize("hasAuthority('ASSET_MANAGE')")
    public AssetResponse assign(@PathVariable UUID assetId, @Valid @RequestBody AssignAssetRequest request) {
        Asset asset = assignAssetUseCase.handle(new AssignAssetCommand(assetId, request.assigneeId()));
        return AssetResponse.from(asset);
    }

    @GetMapping("/{assetId}/history")
    public List<AssetAssignmentResponse> history(@PathVariable UUID assetId) {
        return getAssetHistoryUseCase.handle(assetId).stream().map(AssetAssignmentResponse::from).toList();
    }

    @GetMapping("/{assetId}/depreciation")
    public DepreciationResponse depreciation(@PathVariable UUID assetId) {
        return getAssetDepreciationUseCase.handle(assetId);
    }

    @PostMapping("/{assetId}/maintenance/schedule")
    @PreAuthorize("hasAuthority('ASSET_MANAGE')")
    public MaintenanceResponse scheduleMaintenance(@PathVariable UUID assetId, @RequestBody(required = false) ScheduleMaintenanceRequest request) {
        Maintenance maintenance = scheduleMaintenanceUseCase.handle(assetId, request != null ? request.notes() : null);
        return MaintenanceResponse.from(maintenance);
    }

    @PostMapping("/{assetId}/maintenance/complete")
    @PreAuthorize("hasAuthority('ASSET_MANAGE')")
    public MaintenanceResponse completeMaintenance(@PathVariable UUID assetId) {
        return MaintenanceResponse.from(completeMaintenanceUseCase.handle(assetId));
    }

    @GetMapping("/{assetId}/maintenance")
    public List<MaintenanceResponse> maintenanceHistory(@PathVariable UUID assetId) {
        return listMaintenanceUseCase.handle(assetId).stream().map(MaintenanceResponse::from).toList();
    }
}
