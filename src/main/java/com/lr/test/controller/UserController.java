package com.lr.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Administrator on 2017/1/24.
 */
@Controller
@RequestMapping("user")
public class UserController {
    @RequestMapping("login")
    public String login(){
        return "login";
    }
}
