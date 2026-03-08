package com.ecom.salezone.services;

import com.ecom.salezone.dtos.CreateOrderRequest;
import com.ecom.salezone.dtos.OrderDto;
import com.ecom.salezone.dtos.OrderUpdateRequest;
import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.enums.PaymentStatus;

import java.util.List;

public interface OrderService {
    OrderDto getOrder(String orderId, String logkey);

    //create order
    OrderDto createOrder(CreateOrderRequest orderDto,String logkey);

    //remove order
    void removeOrder(String orderId, String logkey);

    //get orders of user
    List<OrderDto> getOrdersOfUser(String userId,String logkey);

    //get orders
    PageableResponse<OrderDto> getOrders(int pageNumber, int pageSize, String sortBy, String sortDir, String logkey);

    OrderDto updateOrder(String orderId, OrderUpdateRequest request, String logkey);
    OrderDto updateOrder(String orderId, OrderDto request, String logkey);

    void updatePaymentStatus(String orderId, String paymentId, PaymentStatus status, String logKey);
    void updateRazorpayOrderId(String orderId, String razorpayOrderId, String logKey);

    //order methods(logic) related to order
}
