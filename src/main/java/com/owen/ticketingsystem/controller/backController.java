package com.owen.ticketingsystem.controller;

import com.owen.ticketingsystem.entity.ProductDTO;
import com.owen.ticketingsystem.entity.Products;
import com.owen.ticketingsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
public class backController {
    @Autowired
    private ProductService productService;
    @PostMapping("/uploadProduct")
    public String uploadProduct(@ModelAttribute ProductDTO productDTO) throws IOException {

        byte[] imageBytes = productDTO.getimage().getBytes();
        String imageUrl = saveImageToStorage(imageBytes);


        Products product = new Products();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setImageUrl(imageUrl);

        productService.save(product);

        return "redirect:/form";
    }

    public String saveImageToStorage(byte[] imageBytes) throws IOException {
        String imageName = UUID.randomUUID().toString() + ".jpg";
        Path imagePath = Paths.get("uploads/" + imageName);
        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, imageBytes);
        return imagePath.toString();
    }
}
