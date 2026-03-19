package com.XCLONE.KhataBackend.Controller;

import com.XCLONE.KhataBackend.DTO.dashboard.DashboardResponseDTO;
import com.XCLONE.KhataBackend.Service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    private UUID getUserId() {
        return (UUID) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    @GetMapping
    public DashboardResponseDTO getDashboard() {

        return dashboardService.getDashboard(getUserId());

    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {

        return dashboardService.getStats(getUserId());

    }
}
