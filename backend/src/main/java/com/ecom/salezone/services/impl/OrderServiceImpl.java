package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.CreateOrderRequest;
import com.ecom.salezone.dtos.OrderDto;
import com.ecom.salezone.dtos.OrderUpdateRequest;
import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.enities.*;
import com.ecom.salezone.exceptions.BadApiRequestException;
import com.ecom.salezone.exceptions.ResourceNotFoundException;
import com.ecom.salezone.util.Helper;
import com.ecom.salezone.util.LogKeyGenerator;
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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    // Logger for order service operations
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CartRepository cartRepository;

    /**
     * Fetch order by orderId
     */
    @Override
    public OrderDto getOrder(String orderId, String logkey) {

        log.info("{} Fetching order details | orderId={}", logkey, orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("{} Order not found | orderId={}", logkey, orderId);
                    return new ResourceNotFoundException("Order not found !!");
                });

        return modelMapper.map(order, OrderDto.class);
    }

    /**
     * Create order from cart
     */
    @Override
    public OrderDto createOrder(CreateOrderRequest orderDto, String logkey) {

        log.info("{} Create order request received | request={}", logkey, orderDto);

        String userId = orderDto.getUserId();
        String cartId = orderDto.getCartId();

        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("{} User not found | userId={}", logkey, userId);
                    return new ResourceNotFoundException("User not found with given id !!");
                });

        // Fetch cart
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> {
                    log.error("{} Cart not found | cartId={}", logkey, cartId);
                    return new ResourceNotFoundException("Cart with given id not found on server !!");
                });

        List<CartItem> cartItems = cart.getItems();

        // Validate cart items
        if (cartItems.size() <= 0) {
            log.error("{} Cart is empty | cartId={}", logkey, cartId);
            throw new BadApiRequestException("Invalid number of items in cart !!");
        }

        // Create order entity
        Order order = Order.builder()
                .billingName(orderDto.getBillingName())
                .billingPhone(orderDto.getBillingPhone())
                .billingAddress(orderDto.getBillingAddress())
                .deliveredDate(null)
                .paymentStatus(orderDto.getPaymentStatus())
                .orderStatus(orderDto.getOrderStatus())
                .orderId(UUID.randomUUID().toString())
                .user(user)
                .build();

        // Calculate order amount and convert CartItem -> OrderItem
        AtomicReference<Integer> orderAmount = new AtomicReference<>(0);

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {

            OrderItem orderItem = OrderItem.builder()
                    .quantity(cartItem.getQuantity())
                    .product(cartItem.getProduct())
                    .totalPrice(cartItem.getQuantity()
                            * cartItem.getProduct().getDiscountedPrice())
                    .order(order)
                    .build();

            // Accumulate order amount
            orderAmount.set(orderAmount.get() + orderItem.getTotalPrice());

            return orderItem;
        }).collect(Collectors.toList());

        // Attach items and total amount to order
        order.setOrderItems(orderItems);
        order.setOrderAmount(orderAmount.get());

        log.info("{} Order items created | totalItems={}, totalAmount={}",
                logkey, orderItems.size(), orderAmount.get());

        // Clear cart after order creation
        cart.getItems().clear();
        cartRepository.save(cart);

        // Save order
        Order savedOrder = orderRepository.save(order);

        log.info("{} Order created successfully | orderId={}", logkey, savedOrder.getOrderId());

        return modelMapper.map(savedOrder, OrderDto.class);
    }

    /**
     * Remove order by orderId
     */
    @Override
    public void removeOrder(String orderId, String logkey) {

        log.info("{} Remove order request | orderId={}", logkey, orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("{} Order not found for deletion | orderId={}", logkey, orderId);
                    return new ResourceNotFoundException("order is not found !!");
                });

        orderRepository.delete(order);

        log.info("{} Order removed successfully | orderId={}", logkey, orderId);
    }

    /**
     * Get all orders of a user
     */
    @Override
    public List<OrderDto> getOrdersOfUser(String userId, String logkey) {

        log.info("{} Fetching orders of user | userId={}", logkey, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("{} User not found | userId={}", logkey, userId);
                    return new ResourceNotFoundException("User not found !!");
                });

        List<Order> orders = orderRepository.findByUser(user);

        log.info("{} Total orders found | userId={}, count={}", logkey, userId, orders.size());

        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Get paginated orders
     */
    @Override
    public PageableResponse<OrderDto> getOrders(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            String logkey) {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("{} Fetching paginated orders | page={}, size={}, sortBy={}, sortDir={}",
                logKey, pageNumber, pageSize, sortBy, sortDir);

        Sort sort = (sortDir.equalsIgnoreCase("desc"))
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Order> page = orderRepository.findAll(pageable);

        return Helper.getPageableResponse(page, OrderDto.class, logKey);
    }

    /**
     * Update order details
     */
    @Override
    public OrderDto updateOrder(String orderId, OrderUpdateRequest request, String logkey) {

        log.info("{} Update order request | orderId={}, request={}", logkey, orderId, request);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("{} Invalid order update attempt | orderId={}", logkey, orderId);
                    return new BadApiRequestException("Invalid update data");
                });

        order.setBillingName(request.getBillingName());
        order.setBillingPhone(request.getBillingPhone());
        order.setBillingAddress(request.getBillingAddress());
        order.setPaymentStatus(request.getPaymentStatus());
        order.setOrderStatus(request.getOrderStatus());
        order.setDeliveredDate(request.getDeliveredDate());

        Order updatedOrder = orderRepository.save(order);

        log.info("{} Order updated successfully | orderId={}", logkey, orderId);

        return modelMapper.map(updatedOrder, OrderDto.class);
    }

    @Override
    public OrderDto updateOrder(String orderId, OrderDto request, String logkey) {
        return null;
    }
}
