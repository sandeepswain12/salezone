package com.ecom.salezone.services;

import com.ecom.salezone.dtos.CreateOrderRequest;
import com.ecom.salezone.dtos.OrderDto;
import com.ecom.salezone.dtos.OrderUpdateRequest;
import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.enums.PaymentStatus;

import java.util.List;

/**
 * OrderService defines business operations related to
 * order management in the SaleZone E-commerce system.
 *
 * Responsibilities:
 * - Creating orders
 * - Updating orders
 * - Deleting orders
 * - Fetching orders
 * - Managing order payment status
 * - Integrating with payment gateways (Razorpay)
 *
 * Order Lifecycle:
 * 1. User places an order.
 * 2. Order is created in the system.
 * 3. Payment is initiated through payment gateway.
 * 4. Payment status is verified and updated.
 * 5. Order status may be updated accordingly.
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */
public interface OrderService {

    /**
     * Fetches details of a specific order.
     *
     * @param orderId ID of the order
     * @param logkey unique request identifier used for tracing logs
     *
     * @return OrderDto containing order details
     */
    OrderDto getOrder(String orderId, String logkey);

    /**
     * Creates a new order in the system.
     *
     * @param orderDto request containing order creation details
     * @param logkey unique request identifier used for tracing logs
     *
     * @return OrderDto containing created order details
     */
    OrderDto createOrder(CreateOrderRequest orderDto, String logkey);

    /**
     * Removes an order from the system.
     *
     * @param orderId ID of the order to remove
     * @param logkey unique request identifier used for tracing logs
     */
    void removeOrder(String orderId, String logkey);

    /**
     * Fetches all orders belonging to a specific user.
     *
     * @param userId ID of the user
     * @param logkey unique request identifier used for tracing logs
     *
     * @return list of orders for the user
     */
    List<OrderDto> getOrdersOfUser(String userId, String logkey);

    /**
     * Fetches all orders with pagination and sorting support.
     *
     * @param pageNumber page index
     * @param pageSize number of records per page
     * @param sortBy field used for sorting
     * @param sortDir sorting direction (asc or desc)
     * @param logkey unique request identifier used for tracing logs
     *
     * @return pageable response containing orders
     */
    PageableResponse<OrderDto> getOrders(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            String logkey
    );

    /**
     * Updates order information using update request DTO.
     *
     * @param orderId ID of the order
     * @param request request containing fields to update
     * @param logkey unique request identifier used for tracing logs
     *
     * @return updated OrderDto
     */
    OrderDto updateOrder(String orderId, OrderUpdateRequest request, String logkey);

    /**
     * Updates an order using full OrderDto.
     *
     * @param orderId ID of the order
     * @param request updated order data
     * @param logkey unique request identifier used for tracing logs
     *
     * @return updated OrderDto
     */
    OrderDto updateOrder(String orderId, OrderDto request, String logkey);

    /**
     * Updates payment status of an order.
     *
     * Used after verifying payment through payment gateway.
     *
     * @param orderId ID of the order
     * @param paymentId payment gateway transaction ID
     * @param status payment status (PAID / FAILED)
     * @param logKey unique request identifier used for tracing logs
     */
    void updatePaymentStatus(
            String orderId,
            String paymentId,
            PaymentStatus status,
            String logKey
    );

    /**
     * Stores Razorpay order ID for payment processing.
     *
     * @param orderId ID of the order
     * @param razorpayOrderId Razorpay generated order ID
     * @param logKey unique request identifier used for tracing logs
     */
    void updateRazorpayOrderId(
            String orderId,
            String razorpayOrderId,
            String logKey
    );

}