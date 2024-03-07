package com.carrot.controller;

import com.carrot.limit.RedisLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/limit")
public class LimitController {

    @GetMapping("/test1")
    @RedisLimiter(key = "test1", count = 1)
    public String test1() {
        return "test1 successfully";
    }

    @GetMapping("/test2")
    @RedisLimiter(key = "test2", count = 2)
    public String test2() {
        return "test2 successfully";
    }

    @GetMapping("/test3")
    public String test3() {
        return "test3 successfully";
    }
}

