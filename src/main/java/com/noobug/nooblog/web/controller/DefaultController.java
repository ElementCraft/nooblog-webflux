package com.noobug.nooblog.web.controller;

import com.noobug.nooblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {

    @Autowired
    private UserService userService;

    @RequestMapping("/index")
    public String index(Model model) {

        model.addAttribute("session_user", "");
        return "index";
    }

    @RequestMapping("/adminLogin")
    public String adminLogin() {
        return "admin_login";
    }

}