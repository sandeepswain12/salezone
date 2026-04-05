package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.CreateOrderRequest;
import com.ecom.salezone.dtos.OrderDto;
import com.ecom.salezone.dtos.OrderUpdateRequest;
import com.ecom.salezone.enities.Address;
import com.ecom.salezone.enities.Cart;
import com.ecom.salezone.enities.CartItem;
import com.ecom.salezone.enities.Order;
import com.ecom.salezone.enities.Product;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.enums.OrderStatus;
import com.ecom.salezone.enums.PaymentMethod;
import com.ecom.salezone.enums.PaymentStatus;
import com.ecom.salezone.exceptions.BadApiRequestException;
import com.ecom.salezone.repository.AddressRepository;
import com.ecom.salezone.repository.CartRepository;
import com.ecom.salezone.repository.OrderRepository;
import com.ecom.salezone.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_shouldCreateOrderAndClearCart() {
        User user = createUser("user-1");
        Product product = createProduct("product-1", 200);
        CartItem cartItem = CartItem.builder().product(product).quantity(2).totalPrice(400).build();
        Cart cart = new Cart();
        cart.setCartId("cart-1");
        cart.setItems(new ArrayList<>(List.of(cartItem)));
        Address address = Address.builder().id("addr-1").user(user).build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .userId("user-1")
                .cartId("cart-1")
                .addressId("addr-1")
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.NOT_PAID)
                .orderStatus(OrderStatus.PENDING)
                .billingName("John")
                .billingPhone("9999999999")
                .build();

        Order savedOrder = Order.builder().orderId("order-1").orderAmount(400).user(user).address(address).build();
        OrderDto response = OrderDto.builder().orderId("order-1").orderAmount(400).build();

        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(cartRepository.findById("cart-1")).thenReturn(Optional.of(cart));
        when(addressRepository.findById("addr-1")).thenReturn(Optional.of(address));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(modelMapper.map(savedOrder, OrderDto.class)).thenReturn(response);

        OrderDto result = orderService.createOrder(request, "log-1");

        assertEquals("order-1", result.getOrderId());
        assertEquals(0, cart.getItems().size());
        verify(cartRepository).save(cart);
    }

    @Test
    void updateOrder_shouldThrowWhenAddressDoesNotBelongToOrderUser() {
        User orderUser = createUser("user-1");
        User otherUser = createUser("user-2");
        Order order = Order.builder().orderId("order-1").user(orderUser).build();
        Address address = Address.builder().id("addr-2").user(otherUser).build();
        OrderUpdateRequest request = OrderUpdateRequest.builder().addressId("addr-2").build();

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));
        when(addressRepository.findById("addr-2")).thenReturn(Optional.of(address));

        BadApiRequestException exception = assertThrows(
                BadApiRequestException.class,
                () -> orderService.updateOrder("order-1", request, "log-1")
        );

        assertEquals("Address does not belong to user", exception.getMessage());
    }

    private User createUser(String userId) {
        User user = new User();
        user.setUserId(userId);
        user.setEmail(userId + "@salezone.com");
        return user;
    }

    private Product createProduct(String productId, int discountedPrice) {
        Product product = new Product();
        product.setProductId(productId);
        product.setDiscountedPrice(discountedPrice);
        return product;
    }
}
