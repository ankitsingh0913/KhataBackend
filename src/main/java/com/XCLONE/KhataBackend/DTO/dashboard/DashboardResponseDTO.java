package com.XCLONE.KhataBackend.DTO.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardResponseDTO {
    private Map<String, Object> stats;

    private List<Map<String, Object>> salesChart;

    private List<Map<String, Object>> topCustomers;

    private List<Map<String, Object>> recentBills;
}
