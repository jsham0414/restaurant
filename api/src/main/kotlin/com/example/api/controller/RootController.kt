package com.example.api.com.example.api.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class RootController {
    @RequestMapping
    fun homeRedirect(): String {
        return "redirect:/swagger-ui.html"
    }
}