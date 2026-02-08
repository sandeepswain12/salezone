package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.*;
import com.ecom.salezone.helper.LogKeyGenerator;
import com.ecom.salezone.services.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salezone/ecom/orders")
public class OrderController {

    // Controller-level logger
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    /**
     * Create new order
     * URL: POST /salezone/ecom/orders
     */
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Create Order | payload={}",logkey, request);

        OrderDto order = orderService.createOrder(request, logkey);

        log.info("{} API RESPONSE: Order Created | response={}",logkey, order);

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    /**
     * Remove order by orderId
     * URL: DELETE /salezone/ecom/orders/{orderId}
     */
//    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponseMessage> removeOrder(
            @PathVariable String orderId) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.warn("{} API CALL: Remove Order | orderId={}",logkey, orderId);

        orderService.removeOrder(orderId, logkey);

        ApiResponseMessage responseMessage = ApiResponseMessage.builder()
                .status(HttpStatus.OK)
                .message("order is removed !!")
                .success(true)
                .build();

        log.info("{} API RESPONSE: Order Removed | orderId={}",logkey, orderId);

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * Get all orders of a specific user
     * URL: GET /salezone/ecom/orders/users/{userId}
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<OrderDto>> getOrdersOfUser(
            @PathVariable String userId) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Get Orders Of User | userId={}",logkey, userId);

        List<OrderDto> ordersOfUser = orderService.getOrdersOfUser(userId, logkey);

        log.info("{} API RESPONSE: Orders Fetched For User | userId={} count={}",
                logkey, userId, ordersOfUser.size());

        return new ResponseEntity<>(ordersOfUser, HttpStatus.OK);
    }

    /**
     * Get all orders with pagination and sorting
     * URL: GET /salezone/ecom/orders
     */
    @GetMapping
    public ResponseEntity<PageableResponse<OrderDto>> getOrders(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "orderedDate", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Get Orders (Paginated) | page={} size={} sortBy={} sortDir={}",
                logkey, pageNumber, pageSize, sortBy, sortDir);

        PageableResponse<OrderDto> orders =
                orderService.getOrders(pageNumber, pageSize, sortBy, sortDir, logkey);

        log.info("{} API RESPONSE: Orders Fetched | count={}",
                logkey, orders.getContent().size());

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    /**
     * Update order details
     * URL: PUT /salezone/ecom/orders/{orderId}
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDto> updateOrder(
            @PathVariable("orderId") String orderId,
            @RequestBody OrderUpdateRequest request) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Update Order | orderId={} payload={}",logkey, orderId, request);

        OrderDto dto = orderService.updateOrder(orderId, request, logkey);

        log.info("{} API RESPONSE: Order Updated | orderId={}",logkey, orderId);

        return ResponseEntity.ok(dto);
    }
}
