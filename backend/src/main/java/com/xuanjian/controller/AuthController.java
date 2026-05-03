package com.xuanjian.controller;

import com.xuanjian.common.Result;
import com.xuanjian.entity.AppUser;
import com.xuanjian.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "用户认证")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        AppUser user = userService.register(body.get("username"), body.get("password"), body.get("email"));
        if (user == null) return Result.error("用户名已存在");
        return Result.ok(Map.of("id", user.getId(), "username", user.getUsername(), "role", user.getRole()));
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        AppUser user = userService.login(body.get("username"), body.get("password"));
        if (user == null) return Result.error("用户名或密码错误");
        return Result.ok(Map.of("id", user.getId(), "username", user.getUsername(), "role", user.getRole()));
    }
}