package com.noobug.nooblog.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/blog")
public class BlogController {

    @RequestMapping("/u/{account}")
    public String index(@PathVariable String account) {
        return "blog/user";
    }

    @RequestMapping("/t/{id}")
    public String article(@PathVariable String id) {
        return "blog/user";
    }
}
