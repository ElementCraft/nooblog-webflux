package com.noobug.nooblog.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/{path}")
    public String route(@PathVariable String path) {
        return "user/" + path;
    }
}
