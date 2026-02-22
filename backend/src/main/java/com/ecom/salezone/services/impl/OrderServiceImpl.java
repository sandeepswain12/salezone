package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.CreateOrderRequest;
import com.ecom.salezone.dtos.OrderDto;
import com.ecom.salezone.dtos.OrderUpdateRequest;
import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.enities.*;
import com.ecom.salezone.exceptions.BadApiRequestException;
import com.ecom.salezone.exceptions.ResourceNotFoundException;
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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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

    // ================= GET ORDER =================
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

    // ================= CREATE ORDER =================
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

        List<CartItem> cartItems = cart.getItems();

        if (cartItems.size() <= 0) {
            log.error("LogKey: {} - Cart is empty | cartId={}", logkey, cartId);
            throw new BadApiRequestException("Invalid number of items in cart !!");
        }

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

    // ================= REMOVE ORDER =================
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

    // ================= GET ORDERS OF USER =================
    @Override
    public List<OrderDto> getOrdersOfUser(String userId, String logkey) {

        log.info("LogKey: {} - Entry into getOrdersOfUser method | userId={}", logkey, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - User not found | userId={}", logkey, userId);
                    return new ResourceNotFoundException("User not found !!");
                });

        List<Order> orders = orderRepository.findByUser(user);

        log.info("LogKey: {} - Orders fetched for user | userId={} count={}",
                logkey, userId, orders.size());

        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());
    }

    // ================= GET PAGINATED ORDERS =================
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

    // ================= UPDATE ORDER =================
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
        order.setDeliveredDate(request.getDeliveredDate());

        Order updatedOrder = orderRepository.save(order);

        log.info("LogKey: {} - Order updated successfully | orderId={}",
                logkey, orderId);

        return modelMapper.map(updatedOrder, OrderDto.class);
    }

    @Override
    public OrderDto updateOrder(String orderId, OrderDto request, String logkey) {
        return null;
    }
}