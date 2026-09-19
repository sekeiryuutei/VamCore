package com.vamcore.assets.application.command;

import java.util.UUID;

public record AssignAssetCommand(UUID assetId, UUID assigneeId) {
}
