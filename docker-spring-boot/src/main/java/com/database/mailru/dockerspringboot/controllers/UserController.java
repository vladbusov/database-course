package com.database.mailru.dockerspringboot.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping(value = "/user/{nickname}/create", produces = "application/json")
    public String createUser(@PathVariable("nickname") String nickname) {
        return nickname;
    }

}
