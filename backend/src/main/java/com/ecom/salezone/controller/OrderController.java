package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.*;
import com.ecom.salezone.util.LogKeyGenerator;
import com.ecom.salezone.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * OrderController handles order management operations
 * in the SaleZone E-commerce system.
 *
 * This controller provides APIs for:
 * - Creating new orders
 * - Updating existing orders
 * - Removing orders
 * - Fetching orders of a specific user
 * - Fetching all orders with pagination
 *
 * Features:
 * - User specific order retrieval
 * - Paginated order listing
 * - Order lifecycle management
 *
 * Security:
 * - Orders are user specific
 * - Some endpoints may require admin privileges
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */
@Tag(
        name = "Order APIs",
        description = "APIs for managing customer orders in the SaleZone system"
)
@RestController
@RequestMapping("/salezone/ecom/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Operation(
            summary = "Create order",
            description = "Creates a new order using the provided order request payload."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid order request"),
            @ApiResponse(responseCode = "404", description = "User or cart not found")
    })
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Create order request received | payload={}",
                logkey, request);

        OrderDto order =
                orderService.createOrder(request, logkey);

        log.info("LogKey: {} - Order created successfully | orderId={} payload={}",
                logkey, order.getOrderId(), order);

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Remove order",
            description = "Deletes an order using the provided order ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order removed successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponseMessage> removeOrder(
            @PathVariable String orderId) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.warn("LogKey: {} - Remove order request received | orderId={}",
                logkey, orderId);

        orderService.removeOrder(orderId, logkey);

        ApiResponseMessage responseMessage =
                ApiResponseMessage.builder()
                        .status(HttpStatus.OK)
                        .message("Order is removed !!")
                        .success(true)
                        .build();

        log.info("LogKey: {} - Order removed successfully | orderId={}",
                logkey, orderId);

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @Operation(
            summary = "Get orders of user",
            description = "Fetches all orders belonging to a specific user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders fetched successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<OrderDto>> getOrdersOfUser(
            @PathVariable String userId) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Get orders of user request received | userId={}",
                logkey, userId);

        List<OrderDto> ordersOfUser =
                orderService.getOrdersOfUser(userId, logkey);

        log.info("LogKey: {} - Orders fetched successfully for user | userId={} count={}",
                logkey, userId, ordersOfUser.size());

        return new ResponseEntity<>(ordersOfUser, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all orders",
            description = "Fetches all orders with pagination and sorting support."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders fetched successfully")
    })
    @GetMapping
    public ResponseEntity<PageableResponse<OrderDto>> getOrders(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "orderedDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Get orders request received | page={} size={} sortBy={} sortDir={}",
                logkey, pageNumber, pageSize, sortBy, sortDir);

        PageableResponse<OrderDto> orders =
                orderService.getOrders(pageNumber, pageSize, sortBy, sortDir, logkey);

        log.info("LogKey: {} - Orders fetched successfully | totalElements={}",
                logkey, orders.getContent().size());

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @Operation(
            summary = "Update order",
            description = "Updates an existing order using the order ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDto> updateOrder(
            @PathVariable String orderId,
            @RequestBody OrderUpdateRequest request) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Update order request received | orderId={} payload={}",
                logkey, orderId, request);

        OrderDto dto =
                orderService.updateOrder(orderId, request, logkey);

        log.info("LogKey: {} - Order updated successfully | orderId={} payload={}",
                logkey, orderId, dto);

        return ResponseEntity.ok(dto);
    }
}