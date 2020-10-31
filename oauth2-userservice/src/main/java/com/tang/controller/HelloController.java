package com.tang.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 可以匿名访问的资源
 */
@RestController
public class HelloController {


    @GetMapping("hello")
    public String hello() {
        return "Hello";
    }

}