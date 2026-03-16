package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.OrderDto;
import com.ecom.salezone.dtos.UserDto;
import com.ecom.salezone.enums.PaymentStatus;
import com.ecom.salezone.exceptions.BadApiRequestException;
import com.ecom.salezone.services.OrderService;
import com.ecom.salezone.services.UserService;
import com.ecom.salezone.util.LogKeyGenerator;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * PaymentController handles payment processing operations
 * for the SaleZone E-commerce system.
 *
 * This controller integrates with the Razorpay payment gateway
 * to initiate and verify payments for orders.
 *
 * APIs Provided:
 * - Initiate Razorpay payment for an order
 * - Verify Razorpay payment signature
 * - Update order payment status
 *
 * Payment Flow:
 * 1. User places an order.
 * 2. Client calls initiate-payment API.
 * 3. Backend creates Razorpay order.
 * 4. Client completes payment using Razorpay Checkout.
 * 5. Client sends payment details to capture API.
 * 6. Backend verifies Razorpay signature.
 * 7. Order payment status is updated accordingly.
 *
 * Features:
 * - Secure Razorpay integration
 * - Payment signature verification
 * - Order ownership validation
 * - Duplicate payment prevention
 *
 * Security:
 * - Payment is verified using Razorpay signature validation.
 * - Only the order owner can initiate payment.
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */

@Tag(
        name = "Payment APIs",
        description = "APIs for processing and verifying payments using Razorpay"
)
@RestController
@RequestMapping("/salezone/ecom/payment")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Value("${razorpayKey}")
    private String key;

    @Value("${razorpaySecret}")
    private String secret;


    @Operation(
            summary = "Initiate payment",
            description = "Creates a Razorpay order for an existing order and returns Razorpay order details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Razorpay order created successfully"),
            @ApiResponse(responseCode = "200", description = "Razorpay order already exists"),
            @ApiResponse(responseCode = "400", description = "Unauthorized payment attempt"),
            @ApiResponse(responseCode = "500", description = "Error creating Razorpay order")
    })
    @PostMapping("/initiate-payment/{orderId}")
    public ResponseEntity<?> initiatePayment(@PathVariable String orderId, Principal principal) {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("{} Initiating payment for orderId={}", logKey, orderId);

        UserDto user = userService.getUserByEmail(principal.getName(), logKey);
        OrderDto order = orderService.getOrder(orderId, logKey);

        // Validate order ownership
        if (!order.getUser().getUserId().equals(user.getUserId())) {
            log.error("{} Unauthorized payment attempt for orderId={}", logKey, orderId);
            throw new BadApiRequestException("Unauthorized payment attempt");
        }

        try {

            // Prevent duplicate Razorpay order creation
            if (order.getRazorPayOrderId() != null) {

                log.info("{} Razorpay order already exists for orderId={}", logKey, orderId);

                return ResponseEntity.ok(Map.of(
                        "orderId", order.getOrderId(),
                        "razorpayOrderId", order.getRazorPayOrderId(),
                        "amount", order.getOrderAmount(),
                        "paymentStatus", order.getPaymentStatus()
                ));
            }

            RazorpayClient razorpayClient = new RazorpayClient(key, secret);

            JSONObject request = new JSONObject();
            request.put("amount", order.getOrderAmount() * 100);
            request.put("currency", "INR");
            request.put("receipt", order.getOrderId());

            Order razorpayOrder = razorpayClient.orders.create(request);

            String razorpayOrderId = razorpayOrder.get("id");

            log.info("{} Razorpay order created | orderId={} razorpayOrderId={}",
                    logKey, orderId, razorpayOrderId);

            // Save Razorpay order id
            orderService.updateRazorpayOrderId(orderId, razorpayOrderId, logKey);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of(
                            "orderId", orderId,
                            "razorpayOrderId", razorpayOrderId,
                            "amount", order.getOrderAmount(),
                            "paymentStatus", order.getPaymentStatus()
                    )
            );

        } catch (Exception e) {

            log.error("{} Error creating Razorpay order for orderId={}", logKey, orderId, e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error creating Razorpay order"));
        }
    }


    @Operation(
            summary = "Verify payment",
            description = "Verifies Razorpay payment signature and updates order payment status."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment verified successfully"),
            @ApiResponse(responseCode = "400", description = "Payment verification failed"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Error verifying payment")
    })
    @PostMapping("/capture/{orderId}")
    public ResponseEntity<?> verifyPayment(
            @RequestBody Map<String, Object> payload,
            @PathVariable String orderId) {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("{} Payment verification started for orderId={}", logKey, orderId);

        try {

            String razorpayOrderId = payload.get("razorpayOrderId").toString();
            String razorpayPaymentId = payload.get("razorpayPaymentId").toString();
            String razorpaySignature = payload.get("razorpayPaymentSignature").toString();

            // Fetch order
            OrderDto order = orderService.getOrder(orderId, logKey);

            // Validate order id match
            if (!order.getRazorPayOrderId().equals(razorpayOrderId)) {

                log.error("{} Razorpay order mismatch | orderId={} razorpayOrderId={}",
                        logKey, orderId, razorpayOrderId);

                throw new BadApiRequestException("Invalid payment order");
            }

            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpaySignature);

            boolean verified = Utils.verifyPaymentSignature(options, secret);

            if (verified) {

                log.info("{} Payment signature verified | orderId={} paymentId={}",
                        logKey, orderId, razorpayPaymentId);

                orderService.updatePaymentStatus(orderId, razorpayPaymentId, PaymentStatus.PAID, logKey);

                return ResponseEntity.ok(
                        Map.of(
                                "success", true,
                                "message", "Payment verified successfully"
                        )
                );

            } else {

                log.warn("{} Payment signature verification failed | orderId={}", logKey, orderId);

                orderService.updatePaymentStatus(orderId, razorpayPaymentId, PaymentStatus.FAILED, logKey);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "success", false,
                                "message", "Payment verification failed"
                        ));
            }

        } catch (Exception e) {

            log.error("{} Error verifying payment for orderId={}", logKey, orderId, e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error verifying payment"
                    ));
        }
    }
}