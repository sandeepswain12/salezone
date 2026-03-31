package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.AddToWishlistRequest;
import com.ecom.salezone.dtos.ProductDto;
import com.ecom.salezone.dtos.WishlistDto;
import com.ecom.salezone.enities.Product;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.enities.Wishlist;
import com.ecom.salezone.exceptions.BadApiRequestException;
import com.ecom.salezone.exceptions.ResourceNotFoundException;
import com.ecom.salezone.repository.ProductRepository;
import com.ecom.salezone.repository.UserRepository;
import com.ecom.salezone.repository.WishlistRepository;
import com.ecom.salezone.services.WishlistService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private static final Logger log = LoggerFactory.getLogger(WishlistServiceImpl.class);

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public WishlistDto addToWishlist(String userId, AddToWishlistRequest request, String logkey) {

        log.info("LogKey: {} - Add to wishlist | userId={} payload={}",
                logkey, userId, request);

        String productId = request.getProductId();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found | productId={}", productId);
                    return new ResourceNotFoundException("Product not found !!");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found | userId={}", userId);
                    return new ResourceNotFoundException("User not found !!");
                });

        // Prevent duplicate (IMPORTANT)
        if (wishlistRepository.findByUserAndProduct(user, product).isPresent()) {
            throw new BadApiRequestException("Product already in wishlist !!");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);

        wishlistRepository.save(wishlist);

        return getWishlist(userId, logkey);
    }

    @Override
    public WishlistDto getWishlist(String userId, String logkey) {

        log.info("LogKey: {} - Get wishlist | userId={}", logkey, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found !!"));

        List<Wishlist> wishlistItems = wishlistRepository.findByUser(user);

        List<ProductDto> products = wishlistItems.stream()
                .map(item -> mapper.map(item.getProduct(), ProductDto.class))
                .toList();

        return WishlistDto.builder()
                .userId(userId)
                .products(products)
                .build();
    }

    @Override
    public void removeFromWishlist(String userId, String productId, String logkey) {

        log.warn("LogKey: {} - Remove from wishlist | userId={} productId={}",
                logkey, userId, productId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found !!"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found !!"));

        wishlistRepository.deleteByUserAndProduct(user, product);
    }
}
