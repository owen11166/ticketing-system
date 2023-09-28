package com.owen.ticketingsystem.service;

import com.owen.ticketingsystem.entity.Cart;
import com.owen.ticketingsystem.entity.CartItem;
import com.owen.ticketingsystem.entity.Products;
import com.owen.ticketingsystem.entity.User;
import com.owen.ticketingsystem.repository.CartItemRepository;
import com.owen.ticketingsystem.repository.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartItemRepository cartItemRepository;

    public void removeItemFromCart(Cart cart, Long itemId) {
        Optional<CartItem> itemToRemove = cart.getItems().stream().filter(item -> item.getId().equals(itemId)).findFirst();
        if(itemToRemove.isPresent()){
            cart.getItems().remove(itemToRemove.get());
            cartItemRepository.delete(itemToRemove.get());
            cartRepository.save(cart);
        }
    }

    ;

    public Cart getCartByUser(User user) {
        Cart cart = cartRepository.findByUser(user).orElse(null);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }
        return cart;
    }


    public void addItemToCart(User user, Long productId, int quantity) {
        Products product = productService.findProductById(productId);
        Cart cart = cartRepository.findByUser(user).orElse(new Cart(user));

        // 檢查是否已存在相同的商品項目
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            // 如果存在，更新數量
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // 如果不存在，新建購物車項目
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setCart(cart);
            cart.getItems().add(cartItem);
        }

        cartRepository.save(cart);
    }

    public void updateItemQuantity(User user, Long productId, int quantity) {
        // 獲取用戶的購物車
        Cart cart = getCartByUser(user);

        // 在購物車中找到對應的商品並更新其數量
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(quantity);
                // 持久化更新
                cartItemRepository.save(item);
                break;
            }
        }
    }


}
