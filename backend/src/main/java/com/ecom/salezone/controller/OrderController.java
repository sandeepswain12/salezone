package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.*;
import com.ecom.salezone.util.LogKeyGenerator;
import com.ecom.salezone.services.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salezone/ecom/orders")
public class OrderController {

    private static final Logger log =
            LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    // ================= CREATE ORDER =================
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

    // ================= REMOVE ORDER =================
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

    // ================= GET ORDERS OF USER =================
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

    // ================= GET ALL ORDERS (PAGINATED) =================
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

    // ================= UPDATE ORDER =================
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