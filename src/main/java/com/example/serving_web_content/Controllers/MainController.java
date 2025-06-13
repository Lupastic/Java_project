package com.example.serving_web_content.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home( Model model) {
        model.addAttribute("title", " главная страница");
        return "home";
    }
    @GetMapping("/about")
    public String about( Model model) {
        model.addAttribute("title", "о чем то");
        return "about";
    }

    @GetMapping("/Support")
    public String Support( Model model) {
        model.addAttribute("title", "о чем то");
        return "Support";
    }
    @GetMapping("/profile")
    public String Profile( Model model) {
        model.addAttribute("title", "о чем то то");
        return "profile";
    }
}
