package com.owen.ticketingsystem.controller;

import com.owen.ticketingsystem.entity.Order;
import com.owen.ticketingsystem.entity.User;
import com.owen.ticketingsystem.repository.OrderRepository;
import com.owen.ticketingsystem.repository.UserRepository;
import com.owen.ticketingsystem.service.UserService;
import com.owen.ticketingsystem.validation.WebUser;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private  OrderRepository orderRepository;


    @GetMapping("/memberCenter")
    String memberCenter(Model model, Principal principal) {
        String name=principal.getName();

        User user=userService.findByUserName(name);
        List<Order> order=orderRepository.findByUser(user);
        model.addAttribute("user",user);
        model.addAttribute("orders",order);

        return "memberCenter";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("webUser", new WebUser());
        return "register";
    }

    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("webUser") WebUser webuser, BindingResult bindingResult, HttpSession session, Model model) {
        String userName = webuser.getUserName();
        if (bindingResult.hasErrors()) {
            return "register";
        }
        User existing = userService.findByUserName(userName);
        if (existing != null) {
            model.addAttribute("webUser", new WebUser());
            model.addAttribute("registrationError", "使用者名稱已經存在");
            return "register";
        }
        userService.save(webuser);

        session.setAttribute("user", userName);

        return "redirect:/";  // 可根據實際情況進行重定向
    }

    @GetMapping("/forgotPassword")
    public String forgot() {

        return "forgotPassword";
    }
    @GetMapping("/password")
    public String forgotPassword() {

        return "password";
    }

    @PostMapping("/resetPasswordRequest")
    public String processResetPasswordRequest(@RequestParam String email, Model model) {
        try {
            userService.processForgotPassword(email);
            model.addAttribute("emailSent", true);
            return "forgotPassword"; // 替換成你的Thymeleaf模板名稱
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "forgotPassword"; // 替換成你的Thymeleaf模板名稱
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/submitNewPassword")
    public String submitNewPassword(@RequestParam String newPassword,
                                    @RequestParam String confirmPassword,
                                    @RequestParam String token,
                                    Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("passwordError", "兩次輸入的密碼不匹配");
            return "password";
        }

        try {
            userService.resetPassword(token,newPassword);
            model.addAttribute("passwordResetSuccess", true);
            return "password";
        } catch (Exception e )  {
            model.addAttribute("passwordError", e.getMessage());
            return "password";
        }
    }
}