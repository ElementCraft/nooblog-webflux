package com.noobug.nooblog.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @RequestMapping("/{path}")
    public String route(@PathVariable String path) {
        return "admin/" + path;
    }
}
