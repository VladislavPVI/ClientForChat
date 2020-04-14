package com.pvi.myClient.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class MyCont {
    @RequestMapping("/about")
    public Principal user(Principal user) {
        return user;
    }
}