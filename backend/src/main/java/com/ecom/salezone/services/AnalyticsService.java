package com.ecom.salezone.services;

import com.ecom.salezone.dtos.*;

import java.util.List;

public interface AnalyticsService {

    List<RevenueDto> getRevenue(String logKey);

    List<OrderStatsDto> getOrders(String logKey);

    SummaryDto getSummary(String logKey);
}