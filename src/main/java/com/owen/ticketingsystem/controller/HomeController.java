package com.owen.ticketingsystem.controller;

import com.owen.ticketingsystem.entity.*;
import com.owen.ticketingsystem.repository.CreditFormRepository;
import com.owen.ticketingsystem.repository.PaymentRepository;
import com.owen.ticketingsystem.repository.ProductRepository;
import com.owen.ticketingsystem.repository.UserRepository;
import com.owen.ticketingsystem.service.CartService;
import com.owen.ticketingsystem.service.ProductService;
import com.owen.ticketingsystem.service.UserService;
import com.owen.ticketingsystem.validation.WebUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CreditFormRepository creditFormRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ProductRepository productRepository;
    UserService userService;


    @GetMapping("/payment-fail")
    public String fail(){
        return  "paymentFailure";
    }

    @PostMapping("/remove")
    public String removeItemFromCart(@RequestParam("itemId") Long itemId,Principal principal ){

        String username=principal.getName();
        User user=userRepository.findByUserName(username);
        Cart cart=cartService.getCartByUser(user);
        cartService.removeItemFromCart(cart,itemId);

        return "redirect:/cart";
    }

    @GetMapping("/payment-success")
    public ModelAndView handlePayPalSuccess(@RequestParam String paymentId) {
        // 假設我們從PayPal取得了支付資料
        Payment payment = new Payment();
        payment.setTransactionId(paymentId);
        payment.setAmount(1000.0);  // 請根據實際情況設定
        payment.setPayerEmail("payer@example.com");  // 請根據實際情況設定
        payment.setTimestamp(LocalDateTime.now());

        // 儲存支付資料
        System.out.println("Attempting to save payment data...");
        paymentRepository.save(payment);
        System.out.println("Payment data saved successfully.");
        ModelAndView modelAndView = new ModelAndView("paymentSuccess");
        modelAndView.addObject("transactionId", paymentId);
        return modelAndView;
    }

    @PostMapping("/payment-success")
    public ResponseEntity<?> handlePayPalSuccess(
            @RequestParam String paymentId,
            @RequestParam String token,
            @RequestParam String PayerID) {

        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setToken(token);
        payment.setPayerId(PayerID);

        // You can set other fields of payment object if necessary.
        // For example: payment.setAmount(...);

        paymentRepository.save(payment);

        return ResponseEntity.ok("Payment details saved successfully");
    }
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
    @GetMapping("/payment-failure")
    public String handlePayPalFailure() {
        return "paymentFailure";
    }

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
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,@RequestParam int quantity, @RequestParam int currentPage,Principal principal ){
        String username = principal.getName();

        User user = userRepository.findByUserName(username);
        cartService.addItemToCart(user,productId,quantity);
    return  "redirect:/products?page=" + currentPage;
    }

    @PostMapping("/uploadProduct")
    public String uploadProduct(@ModelAttribute ProductDTO productDTO) throws IOException {
        // 處理上傳的圖片
        byte[] imageBytes = productDTO.getimage().getBytes();
        String imageUrl = saveImageToStorage(imageBytes); // 實現這個方法來保存圖片

        // 保存商品信息到資料庫
        Products product = new Products();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setImageUrl(imageUrl);

        productService.save(product);

        return "redirect:/form";
    }

    public String saveImageToStorage(byte[] imageBytes) throws IOException {
        String imageName = UUID.randomUUID().toString() + ".jpg"; // 生成唯一的文件名
        Path imagePath = Paths.get("uploads/" + imageName);
        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, imageBytes);
        return imagePath.toString();
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

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("webUser", new WebUser());
        return "register";
    }

    @GetMapping("/products")
    public String listProducts(@RequestParam(defaultValue = "0") int page,Model model) {
        Page<Products> productPage = productRepository.findAll(PageRequest.of(page, 5));
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("currentPage", page);


        return "productList";
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
    @PostMapping("/updateQuantity")
    public String updateQuantity(@RequestParam Long productId, @RequestParam int quantity, Principal principal) {
        // 根據 principal 獲得當前用戶
        String username = principal.getName();
        User user = userRepository.findByUserName(username);

        // 使用 CartService 更新購物車中的商品數量
        cartService.updateItemQuantity(user, productId, quantity);

        // 重定向回購物車頁面
        return "redirect:/cart";
    }


}
