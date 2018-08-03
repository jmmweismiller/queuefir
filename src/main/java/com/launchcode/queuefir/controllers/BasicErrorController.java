package com.launchcode.queuefir.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BasicErrorController implements ErrorController {

    @RequestMapping("/error")
    public String errorPage() {
return "errorPage";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}

