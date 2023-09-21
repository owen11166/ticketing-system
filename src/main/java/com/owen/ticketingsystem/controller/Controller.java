package com.owen.ticketingsystem.controller;

import com.owen.ticketingsystem.entity.Match;
import com.owen.ticketingsystem.entity.Team;
import com.owen.ticketingsystem.entity.User;
import com.owen.ticketingsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Controller
public class Controller {
    @Autowired
    UserService userService;
    @GetMapping("/game")
    public String game() {
        return "game";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        userService.save(user);
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


}
