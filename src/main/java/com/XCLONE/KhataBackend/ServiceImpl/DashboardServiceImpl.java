package com.XCLONE.KhataBackend.ServiceImpl;

import com.XCLONE.KhataBackend.DTO.dashboard.DashboardResponseDTO;
import com.XCLONE.KhataBackend.Repository.BillRepository;
import com.XCLONE.KhataBackend.Repository.CustomerRepository;
import com.XCLONE.KhataBackend.Repository.ProductRepository;
import com.XCLONE.KhataBackend.Service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final BillRepository billRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Override
    public DashboardResponseDTO getDashboard(UUID userId) {
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);

        Map<String, Object> stats = getStats(userId);

        List<Map<String, Object>> recentBills =
                billRepository.findRecentBills(userId);

        List<Map<String, Object>> topCustomers =
                customerRepository.findTopCustomers(userId);

        List<Map<String, Object>> salesChart =
                billRepository.getSalesLast7Days(userId, sevenDaysAgo);

        return DashboardResponseDTO.builder()
                .stats(stats)
                .recentBills(recentBills)
                .topCustomers(topCustomers)
                .salesChart(salesChart)
                .build();
    }

    @Override
    public Map<String, Object> getStats(UUID userId) {

        Map<String, Object> stats = new HashMap<>();

        Instant startOfToday = LocalDate.now()
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC);

        Instant startOfMonth = LocalDate.now()
                .withDayOfMonth(1)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC);

        BigDecimal todaySales =
                billRepository.getSalesAfter(userId, startOfToday);

        BigDecimal monthlySales =
                billRepository.getSalesAfter(userId, startOfMonth);

        int todayBillCount =
                billRepository.countAfter(userId, startOfToday);

        int monthlyBillCount =
                billRepository.countAfter(userId, startOfMonth);

        int customerCount =
                customerRepository.countByUserId(userId);

        int productCount =
                productRepository.countByUserId(userId);

        int lowStockCount =
                productRepository.countLowStock(userId);

        stats.put("todaySales", todaySales);
        stats.put("todayBillCount", todayBillCount);
        stats.put("monthlySales", monthlySales);
        stats.put("monthlyBillCount", monthlyBillCount);
        stats.put("customerCount", customerCount);
        stats.put("productCount", productCount);
        stats.put("lowStockCount", lowStockCount);
        stats.put("totalPending", 0); // can connect to payments later

        return stats;
    }
}
