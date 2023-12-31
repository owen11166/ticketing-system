package com.owen.ticketingsystem.service;

import com.owen.ticketingsystem.entity.Cart;
import com.owen.ticketingsystem.entity.CartItem;
import com.owen.ticketingsystem.entity.Products;
import com.owen.ticketingsystem.entity.User;
import com.owen.ticketingsystem.repository.CartItemRepository;
import com.owen.ticketingsystem.repository.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
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
            cartRepository.save(cart);  // 先保存購物車的更改
            cartItemRepository.delete(itemToRemove.get());  // 然後從資料庫中刪除項目
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


        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {

            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {

            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setCart(cart);
            cart.getItems().add(cartItem);
        }

        cartRepository.save(cart);
    }

    public void updateItemQuantity(User user, Long productId, int quantity) {

        Cart cart = getCartByUser(user);

        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(quantity);

                cartItemRepository.save(item);
                break;
            }
        }
    }
    public void clearCart(User user) {
        System.out.println("開始清空購物車");
        Cart cart = getCartByUser(user);
        cartItemRepository.deleteAllByCart(cart);
        cartRepository.delete(cart);
        System.out.println("購物車清空完成");
    }

}
