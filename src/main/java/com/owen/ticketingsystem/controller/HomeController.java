package com.owen.ticketingsystem.controller;

import com.owen.ticketingsystem.entity.*;
import com.owen.ticketingsystem.repository.CreditFormRepository;
import com.owen.ticketingsystem.repository.PaymentRepository;
import com.owen.ticketingsystem.repository.ProductRepository;
import com.owen.ticketingsystem.repository.UserRepository;
import com.owen.ticketingsystem.service.CartService;
import com.owen.ticketingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private CartService cartService;
    @Autowired
    private CreditFormRepository creditFormRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserService userService;

    @GetMapping("/checkout")
    public String checkOut(Model model,Principal principal){

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


        return "checkout";
    }



    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("creditForm", new CreditForm());
        return "form";
    }


    @PostMapping("/submit")
    public String submitForm(@ModelAttribute CreditForm creditForm) {
        if (creditForm.isSaveToDatabase()) {
            creditFormRepository.save(creditForm);
        }
        return "redirect:/";
    }

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/game")
    public String game(Model model) throws  IOException {
        List<Match> matches = null;
        try {
            matches = readMatchesFromFile("src/main/resources/matches.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("matches", matches);
        return "game";
    }


    @GetMapping("/products")
    public String listProducts(@RequestParam(defaultValue = "0") int page,Model model) {
        Page<Products> productPage = productRepository.findAll(PageRequest.of(page, 5));
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("currentPage", page);


        return "productList";
    }



    @GetMapping("/")
    public String showMatches(Model model) throws IOException {
        List<Match> matches = null;
        try {
            matches = readMatchesFromFile("src/main/resources/matches.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("matches", matches);
        List<Team> teams = null;
        try {
            teams = readTeamsFromFile("src/main/resources/standings.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("teams", teams);

        return "index";
    }


    public List<Match> readMatchesFromFile(String fileName) throws IOException {
        List<Match> matches = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                Match match = new Match();
                match.setDate(fields[0]);
                match.setTime(fields[1]);
                match.setTeams(fields[2]);
                match.setLocation(fields[3]);
                matches.add(match);
            }
        }
        return matches;
    }

    public List<Team> readTeamsFromFile(String fileName) throws IOException {
        List<Team> teams = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); // skip header line
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                Team team = new Team();
                team.setRank(Integer.parseInt(fields[0]));
                team.setName(fields[1]);
                team.setPlayed(Integer.parseInt(fields[2]));
                team.setRecord(fields[3]);
                team.setWinRate(Double.parseDouble(fields[4]));
                team.setGamesBehind(Double.parseDouble(fields[5]));
                team.setStreak(fields[6]);
                teams.add(team);
            }
        }
        return teams;
    }


}
