package com.vamcore.reporting.infrastructure.web;

import com.vamcore.reporting.application.dto.PlatformSummaryResponse;
import com.vamcore.reporting.application.dto.RecentActivityResponse;
import com.vamcore.reporting.application.usecase.GetPlatformSummaryUseCase;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API de reportería agregada (VamCore core transversal, ver sección 26 del
 * documento de arquitectura - CQRS selectivo).
 */
@RestController
@RequestMapping("/api/v1/reporting")
public class ReportingController {

    private final GetPlatformSummaryUseCase getPlatformSummaryUseCase;

    public ReportingController(GetPlatformSummaryUseCase getPlatformSummaryUseCase) {
        this.getPlatformSummaryUseCase = getPlatformSummaryUseCase;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('REPORTING_VIEW')")
    public PlatformSummaryResponse summary() {
        return PlatformSummaryResponse.from(getPlatformSummaryUseCase.handle());
    }

    @GetMapping("/activity")
    @PreAuthorize("hasAuthority('REPORTING_VIEW')")
    public List<RecentActivityResponse> activity(@RequestParam(defaultValue = "20") int limit) {
        return getPlatformSummaryUseCase.recentActivity(limit).stream().map(RecentActivityResponse::from).toList();
    }
}
