package com.fil.controller;

import com.fil.dto.UserRegistrationData;
import com.fil.exceptions.AlreadyExistsException;
import com.fil.exceptions.NotFoundException;
import com.fil.model.Session;
import com.fil.model.User;
import com.fil.service.SessionService;
import com.fil.service.UserService;
import com.fil.service.WalletService;
import com.fil.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;
    @Autowired
    private SessionService sessionService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationData data) throws AlreadyExistsException {
        User user = userService.register(data.toUser());
        walletService.create(user);
        Session session = sessionService.createSession(user);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User saved successfully");
        response.put("data", user);

        return ResponseEntity
                .ok()
                .headers(ResponseUtil.createAuthHeaders(session))
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@AuthenticationPrincipal User user, HttpServletResponse res) throws NotFoundException {
        Session session = sessionService.createSession(user);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User logged in");
        response.put("data", user);

        return ResponseEntity
                .ok()
                .headers(ResponseUtil.createAuthHeaders(session))
                .body(response);

    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user) {
//        sessionService.removeSession(user);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();

    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@AuthenticationPrincipal User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", user);

        return ResponseEntity.ok(response);

    }

}