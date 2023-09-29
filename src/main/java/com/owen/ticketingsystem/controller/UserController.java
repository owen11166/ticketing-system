package com.owen.ticketingsystem.controller;

import com.owen.ticketingsystem.entity.User;
import com.owen.ticketingsystem.service.UserService;
import com.owen.ticketingsystem.validation.EmailNotFoundException;
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

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/forgotPassword")
    public String forgotPassword(@RequestParam("email") String email,Model model){
        try {
            userService.resetPassword(email);
        } catch (EmailNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        } catch (MessagingException e) {
            model.addAttribute("error", "There was an error sending the email");
            return "login";
        }

        return "redirect:/login";
    }

    @GetMapping("/memberCenter")
    String memberCenter(){

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


}
