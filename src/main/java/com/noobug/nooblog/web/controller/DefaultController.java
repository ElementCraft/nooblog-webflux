package com.noobug.nooblog.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {

    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    @RequestMapping("/adminLogin")
    public String adminLogin() {
        return "admin_login";
    }

}