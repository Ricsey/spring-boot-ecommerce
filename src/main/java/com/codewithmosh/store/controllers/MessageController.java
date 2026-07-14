package com.codewithmosh.store.controllers;

import com.codewithmosh.store.entities.Message;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class MessageController {
    @RequestMapping("/message")
    public Message sayHello() {
        return new Message("Hello World!");
    }
}
