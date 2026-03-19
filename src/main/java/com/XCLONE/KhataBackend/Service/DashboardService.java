package com.XCLONE.KhataBackend.Service;

import com.XCLONE.KhataBackend.DTO.dashboard.DashboardResponseDTO;

import java.util.Map;
import java.util.UUID;

public interface DashboardService {

    DashboardResponseDTO getDashboard(UUID userId);
    Map<String, Object> getStats(UUID userId);
}
