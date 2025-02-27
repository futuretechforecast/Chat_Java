package com.chat.Controller;

import com.chat.Model.UserModel;
import com.chat.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/user")
@CrossOrigin("*")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/addnew")
	public ResponseEntity<UserModel> addNewUser(@RequestBody UserModel model) {
		try {
			UserModel savedUser = userService.addNewUser(model);
			return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/login")
	public ResponseEntity<UserModel> login(@RequestBody UserModel model) {
		try {

			UserModel user = userService.login(model);
			return ResponseEntity.ok(user);
		} catch (RuntimeException e) {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@GetMapping("/getUserDetails")
	public ResponseEntity<UserModel> getDetails(@RequestParam String currentUserId) {
		try {
			UserModel user = userService.getDetails(currentUserId);

			return ResponseEntity.ok(user);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@GetMapping("/getAllUser")
	public ResponseEntity<List<UserModel>> getAllUsers(@RequestParam String currentUserId) {
	    try {
	        List<UserModel> users = userService.getAllUsers(currentUserId);
	        if (users.isEmpty()) {
	            return ResponseEntity.noContent().build();
	        }
	        return ResponseEntity.ok(users);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	    }
	}

}
