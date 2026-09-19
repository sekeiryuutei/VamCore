package com.vamcore.assets.application.command;

import java.util.UUID;

public record ChangeAssetStatusCommand(UUID assetId, String newStatus) {
}
