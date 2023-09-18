package com.owen.ticketingsystem.controller;

import com.owen.ticketingsystem.entity.Match;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Controller
public class Controller {

    @GetMapping("/game")
    public String game(){
        return "game";
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }
    @GetMapping("/register")
    public String register(){
        return "register";
    }
    @GetMapping("/")
    public String showMatches(Model model) throws IOException {
        List<Match> matches = null;
        try {
            matches = readMatchesFromFile("data/matches.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("matches", matches);
        return "index";
    }

    public List<Match> readMatchesFromFile(String fileName) throws IOException {
        List<Match> matches = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); // skip header line
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
}
