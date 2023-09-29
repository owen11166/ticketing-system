package com.owen.ticketingsystem.controller;

import com.owen.ticketingsystem.entity.Cart;
import com.owen.ticketingsystem.entity.User;
import com.owen.ticketingsystem.repository.UserRepository;
import com.owen.ticketingsystem.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class CartController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartService cartService;

    @GetMapping("/cart")
    public String viewCart(Model model, Principal principal) {

        String username = principal.getName();

        User user = userRepository.findByUserName(username);

        Cart cart = cartService.getCartByUser(user);

        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        int num=(int)cart.getItems().stream().mapToDouble(item->item.getQuantity()).sum();


        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        model.addAttribute("num", num);

        return "cart";
    }
    @PostMapping("/updateQuantity")
    public String updateQuantity(@RequestParam Long productId, @RequestParam int quantity, Principal principal) {

        String username = principal.getName();
        User user = userRepository.findByUserName(username);


        cartService.updateItemQuantity(user, productId, quantity);


        return "redirect:/cart";
    }
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,@RequestParam int quantity, @RequestParam int currentPage,Principal principal ){
        String username = principal.getName();

        User user = userRepository.findByUserName(username);
        cartService.addItemToCart(user,productId,quantity);
        return  "redirect:/products?page=" + currentPage;
    }
    @PostMapping("/remove")
    public String removeItemFromCart(@RequestParam("itemId") Long itemId,Principal principal ){

        String username=principal.getName();
        User user=userRepository.findByUserName(username);
        Cart cart=cartService.getCartByUser(user);
        cartService.removeItemFromCart(cart,itemId);

        return "redirect:/cart";
    }


}
