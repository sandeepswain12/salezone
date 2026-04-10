package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.*;
import com.ecom.salezone.services.AnalyticsService;
import com.ecom.salezone.util.LogKeyGenerator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Analytics APIs",
        description = "APIs for dashboard analytics in SaleZone system"
)
@RestController
@RequestMapping("/salezone/ecom/admin/analytics")
public class AnalyticsController {

    private static final Logger log =
            LoggerFactory.getLogger(AnalyticsController.class);

    @Autowired
    private AnalyticsService analyticsService;

    @Operation(
            summary = "Get revenue analytics",
            description = "Fetch revenue grouped by date for dashboard graphs"
    )
    @GetMapping("/revenue")
    public ResponseEntity<List<RevenueDto>> getRevenue() {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Get revenue analytics request received", logKey);

        List<RevenueDto> revenue =
                analyticsService.getRevenue(logKey);

        log.info("LogKey: {} - Revenue analytics fetched successfully | count={}",
                logKey, revenue.size());

        return ResponseEntity.ok(revenue);
    }

    @Operation(
            summary = "Get orders analytics",
            description = "Fetch orders count grouped by date"
    )
    @GetMapping("/orders")
    public ResponseEntity<List<OrderStatsDto>> getOrders() {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Get orders analytics request received", logKey);

        List<OrderStatsDto> orders =
                analyticsService.getOrders(logKey);

        log.info("LogKey: {} - Orders analytics fetched successfully | count={}",
                logKey, orders.size());

        return ResponseEntity.ok(orders);
    }

    @Operation(
            summary = "Get dashboard summary",
            description = "Fetch total revenue, users, orders, products"
    )
    @GetMapping("/summary")
    public ResponseEntity<SummaryDto> getSummary() {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Get summary analytics request received", logKey);

        SummaryDto summary =
                analyticsService.getSummary(logKey);

        log.info("LogKey: {} - Summary fetched successfully | payload={}",
                logKey, summary);

        return ResponseEntity.ok(summary);
    }
}