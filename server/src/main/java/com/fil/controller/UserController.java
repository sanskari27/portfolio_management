package com.fil.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fil.dto.UserRegistrationData;
import com.fil.exceptions.AlreadyExistsException;
import com.fil.exceptions.InvalidFieldException;
import com.fil.exceptions.PermissionDeniedException;
import com.fil.model.User;
import com.fil.model.Wallet;
import com.fil.model.enums.UserRole;
import com.fil.service.UserService;
import com.fil.service.WalletService;

@RestController
@RequestMapping("/api/user")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private WalletService walletService;

	@PostMapping("/add-balance")
	public ResponseEntity<?> addBalance(@RequestBody ModelMap modal, HttpServletRequest req) {
		Object amtObj = modal.get("amount");
		if (amtObj == null || !(amtObj instanceof Double)) {
			throw new InvalidFieldException();
		}
		User user = (User) req.getAttribute("user");

		double amount = (double) amtObj;

		walletService.addBalance(user, amount);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", true);
		return ResponseEntity.status(HttpStatus.OK).body(map);

	}

	@GetMapping("/details")
	public ResponseEntity<?> details(HttpServletRequest req) {
		User user = (User) req.getAttribute("user");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", true);
		map.put("user", user);
		return ResponseEntity.status(HttpStatus.OK).body(map);

	}

	@GetMapping("/wallet-balance")
	public ResponseEntity<?> walletBalance(@RequestBody ModelMap modal, HttpServletRequest req) {
		User user = (User) req.getAttribute("user");

		Wallet wallet = walletService.findByUser(user);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", true);
		map.put("balance", wallet.getBalance());
		return ResponseEntity.status(HttpStatus.OK).body(map);

	}

	@PostMapping("/add-user")
	public ResponseEntity<?> addUser(@Valid @RequestBody UserRegistrationData data, HttpServletRequest req)
			throws AlreadyExistsException, PermissionDeniedException {
		User reqUser = (User) req.getAttribute("user");
		if (reqUser.getRole() != UserRole.PORTAL_MANAGER) {
			throw new PermissionDeniedException();
		}

		User user = data.toUser();
		userService.register(user);
		walletService.create(user);

		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("message", "User saved succesfully");
		response.put("data", user);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);

	}

}