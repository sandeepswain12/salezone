package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.*;
import com.ecom.salezone.repository.*;
import com.ecom.salezone.services.AnalyticsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final Logger log =
            LoggerFactory.getLogger(AnalyticsServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    // Revenue
    @Override
    public List<RevenueDto> getRevenue(String logKey) {

        log.info("LogKey: {} - Entry into getRevenue method", logKey);

        List<Object[]> data = orderRepository.getRevenueData();

        List<RevenueDto> result = data.stream()
                .map(obj -> new RevenueDto(
                        obj[0].toString(),
                        ((Number) obj[1]).doubleValue()
                ))
                .toList();

        log.info("LogKey: {} - Revenue data processed successfully | count={}",
                logKey, result.size());

        return result;
    }

    // Orders
    @Override
    public List<OrderStatsDto> getOrders(String logKey) {

        log.info("LogKey: {} - Entry into getOrders method", logKey);

        List<Object[]> data = orderRepository.getOrdersData();

        List<OrderStatsDto> result = data.stream()
                .map(obj -> new OrderStatsDto(
                        obj[0].toString(),
                        ((Number) obj[1]).longValue()
                ))
                .toList();

        log.info("LogKey: {} - Orders data processed successfully | count={}",
                logKey, result.size());

        return result;
    }

    // 💰 Summary
    @Override
    public SummaryDto getSummary(String logKey) {

        log.info("LogKey: {} - Entry into getSummary method", logKey);

        Double revenue = orderRepository.getTotalRevenue();
        Long orders = orderRepository.count();
        Long users = userRepository.count();
        Long products = productRepository.count();

        SummaryDto summary =
                new SummaryDto(revenue, orders, users, products);

        log.info("LogKey: {} - Summary created successfully | payload={}",
                logKey, summary);

        return summary;
    }
}