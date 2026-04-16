package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.CreateOrderRequest;
import com.ecom.salezone.dtos.OrderDto;
import com.ecom.salezone.dtos.OrderUpdateRequest;
import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.enities.*;
import com.ecom.salezone.enums.OrderStatus;
import com.ecom.salezone.enums.PaymentMethod;
import com.ecom.salezone.enums.PaymentStatus;
import com.ecom.salezone.exceptions.BadApiRequestException;
import com.ecom.salezone.exceptions.ResourceNotFoundException;
import com.ecom.salezone.repository.AddressRepository;
import com.ecom.salezone.util.Helper;
import com.ecom.salezone.repository.CartRepository;
import com.ecom.salezone.repository.OrderRepository;
import com.ecom.salezone.repository.UserRepository;
import com.ecom.salezone.services.OrderService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Implementation of OrderService for the SaleZone E-commerce system.
 *
 * Handles order management operations including:
 * - Creating orders from user carts
 * - Fetching orders by ID
 * - Fetching user order history
 * - Updating order information
 * - Updating payment status
 * - Deleting orders
 *
 * Integrates caching to improve performance for order retrieval.
 *
 * @author Sandeep Kumar Swain
 * @version 1.0
 * @since 15-03-2026
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log =
            LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    /* Get Order */
    @Cacheable(
            value = "orders",
            key = "#orderId",
            condition = "@cacheFlags.orderCacheEnabled()"
    )
    @Override
    public OrderDto getOrder(String orderId, String logkey) {

        log.info("LogKey: {} - Entry into getOrder method | orderId={}", logkey, orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Order not found | orderId={}", logkey, orderId);
                    return new ResourceNotFoundException("Order not found !!");
                });

        log.info("LogKey: {} - Order fetched successfully | orderId={}", logkey, orderId);

        return modelMapper.map(order, OrderDto.class);
    }

    /* Create Order */
    @CacheEvict(
            value = {"orders","orders_page","user_orders"},
            condition = "@cacheFlags.orderCacheEnabled()",
            allEntries = true
    )
    @Override
    public OrderDto createOrder(CreateOrderRequest orderDto, String logkey) {

        log.info("LogKey: {} - Entry into createOrder method | payload={}", logkey, orderDto);

        String userId = orderDto.getUserId();
        String cartId = orderDto.getCartId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - User not found | userId={}", logkey, userId);
                    return new ResourceNotFoundException("User not found with given id !!");
                });

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Cart not found | cartId={}", logkey, cartId);
                    return new ResourceNotFoundException("Cart with given id not found on server !!");
                });

        String billingName    = orderDto.getBillingName();
        String billingPhone   = orderDto.getBillingPhone();
        String billingAddress = orderDto.getBillingAddress();

        if (orderDto.getAddressId() != null && !orderDto.getAddressId().isBlank()) {

            Address address = addressRepository.findById(orderDto.getAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address not found !!"));

            if (!address.getUser().getUserId().equals(userId)) {
                throw new BadApiRequestException("Address does not belong to this user");
            }

            billingName    = address.getName();
            billingPhone   = address.getMobile();
            billingAddress = address.getFullAddress()
                    + ", " + address.getCity()
                    + ", " + address.getState()
                    + " - " + address.getPincode();

        } else {
            // If no addressId, manual fields become mandatory
            if (billingName == null || billingName.isBlank())
                throw new BadApiRequestException("Billing name is required !!");
            if (billingPhone == null || billingPhone.isBlank())
                throw new BadApiRequestException("Billing phone is required !!");
            if (billingAddress == null || billingAddress.isBlank())
                throw new BadApiRequestException("Billing address is required !!");
        }

        List<CartItem> cartItems = cart.getItems();

        if (cartItems.size() <= 0) {
            log.error("LogKey: {} - Cart is empty | cartId={}", logkey, cartId);
            throw new BadApiRequestException("Invalid number of items in cart !!");
        }

        LocalDate estimatedDelivery = LocalDate.now().plusDays(
                orderDto.getPaymentMethod() == PaymentMethod.COD ? 7 : 5
        );

        Order order = Order.builder()
                .billingName(billingName)
                .billingPhone(billingPhone)
                .billingAddress(billingAddress)
                .deliveredDate(null)
                .estimatedDeliveryDate(estimatedDelivery)
                .paymentStatus(orderDto.getPaymentStatus())
                .orderStatus(orderDto.getOrderStatus())
                .orderId(UUID.randomUUID().toString())
                .paymentMethod(orderDto.getPaymentMethod())
                .user(user)
                .build();

        AtomicReference<Integer> orderAmount = new AtomicReference<>(0);

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {

            OrderItem orderItem = OrderItem.builder()
                    .quantity(cartItem.getQuantity())
                    .product(cartItem.getProduct())
                    .totalPrice(cartItem.getQuantity()
                            * cartItem.getProduct().getDiscountedPrice())
                    .order(order)
                    .build();

            orderAmount.set(orderAmount.get() + orderItem.getTotalPrice());

            return orderItem;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        order.setOrderAmount(orderAmount.get());

        log.info("LogKey: {} - Order items created | totalItems={} totalAmount={}",
                logkey, orderItems.size(), orderAmount.get());

        cart.getItems().clear();
        cartRepository.save(cart);

        Order savedOrder = orderRepository.save(order);

        log.info("LogKey: {} - Order created successfully | orderId={}",
                logkey, savedOrder.getOrderId());

        return modelMapper.map(savedOrder, OrderDto.class);
    }

    /* Remove Order */
    @CacheEvict(
            value = {"orders","orders_page","user_orders"},
            condition = "@cacheFlags.orderCacheEnabled()",
            allEntries = true
    )
    @Override
    public void removeOrder(String orderId, String logkey) {

        log.warn("LogKey: {} - Entry into removeOrder method | orderId={}", logkey, orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Order not found for deletion | orderId={}", logkey, orderId);
                    return new ResourceNotFoundException("order is not found !!");
                });

        orderRepository.delete(order);

        log.info("LogKey: {} - Order removed successfully | orderId={}", logkey, orderId);
    }

    /* Get Orders Of User */
    @Cacheable(
            value = "user_orders",
            key = "#userId",
            condition = "@cacheFlags.orderCacheEnabled()"
    )
    @Override
    public List<OrderDto> getOrdersOfUser(String userId, String logkey) {

        log.info("LogKey: {} - Entry into getOrdersOfUser method | userId={}", logkey, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - User not found | userId={}", logkey, userId);
                    return new ResourceNotFoundException("User not found !!");
                });

        List<Order> orders = orderRepository.findByUserOrderByOrderedDateDesc(user);

        log.info("LogKey: {} - Orders fetched for user | userId={} count={}",
                logkey, userId, orders.size());

        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());
    }

    /* Get Paginated Orders */
    @Cacheable(
            value = "orders_page",
            key = "'page_' + #pageNumber + '_size_' + #pageSize + '_sort_' + #sortBy + '_' + #sortDir",
            condition = "@cacheFlags.orderCacheEnabled()"
    )
    @Override
    public PageableResponse<OrderDto> getOrders(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            String logkey) {

        log.info("LogKey: {} - Entry into getOrders method | page={} size={} sortBy={} sortDir={}",
                logkey, pageNumber, pageSize, sortBy, sortDir);

        Sort sort = (sortDir.equalsIgnoreCase("desc"))
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Order> page = orderRepository.findAll(pageable);

        log.info("LogKey: {} - Orders fetched from DB | count={}",
                logkey, page.getNumberOfElements());

        return Helper.getPageableResponse(page, OrderDto.class, logkey);
    }

    /* Update Order */
    @CacheEvict(
            value = {"orders","orders_page","user_orders"},
            condition = "@cacheFlags.orderCacheEnabled()",
            allEntries = true
    )
    @Override
    public OrderDto updateOrder(String orderId, OrderUpdateRequest request, String logkey) {

        log.info("LogKey: {} - Entry into updateOrder method | orderId={} payload={}",
                logkey, orderId, request);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Invalid order update attempt | orderId={}",
                            logkey, orderId);
                    return new BadApiRequestException("Invalid update data");
                });

        order.setBillingName(request.getBillingName());
        order.setBillingPhone(request.getBillingPhone());
        order.setBillingAddress(request.getBillingAddress());
        order.setPaymentStatus(request.getPaymentStatus());
        order.setOrderStatus(request.getOrderStatus());
        // Auto-set delivered date when status is DELIVERED
        if (request.getOrderStatus() == OrderStatus.DELIVERED
                && order.getDeliveredDate() == null) {
            order.setDeliveredDate(LocalDateTime.now());
            log.info("LogKey: {} - Order marked delivered | orderId={}", logkey, orderId);
        }

        Order updatedOrder = orderRepository.save(order);

        log.info("LogKey: {} - Order updated successfully | orderId={}",
                logkey, orderId);

        return modelMapper.map(updatedOrder, OrderDto.class);
    }

    @Override
    public OrderDto updateOrder(String orderId, OrderDto request, String logkey) {

        return null;
    }

    /* Update Payment Status */
    @CacheEvict(
            value = {"orders","orders_page","user_orders"},
            condition = "@cacheFlags.orderCacheEnabled()",
            allEntries = true
    )
    @Override
    public void updatePaymentStatus(String orderId, String paymentId, PaymentStatus status, String logKey) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setPaymentStatus(status);
        order.setPaymentId(paymentId);

        orderRepository.save(order);

        log.info("LogKey: {} - Payment updated | orderId={} paymentId={}", logKey, orderId, paymentId);
    }

    /* Update Razorpay Order Id */
    @Override
    public void updateRazorpayOrderId(String orderId, String razorpayOrderId, String logKey) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setRazorpayOrderId(razorpayOrderId);

        orderRepository.save(order);

        log.info("LogKey: {} - Razorpay order id saved | orderId={} razorpayOrderId={}",
                logKey, orderId, razorpayOrderId);
    }
}