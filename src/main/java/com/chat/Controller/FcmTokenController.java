package com.chat.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chat.Model.FcmTokenModel;
import com.chat.Service.FcmTokenService;

@RestController
@RequestMapping("/api/chat/token")
@CrossOrigin("*")
public class FcmTokenController {

	@Autowired
	private FcmTokenService fcmTokenService;

	@PostMapping("/save")
	public String saveToken(@RequestBody FcmTokenModel model) {
		try {
			fcmTokenService.saveToken(model);
			return "FCM token saved successfully.";
		} catch (Exception e) {
			return "Error saving FCM token: " + e.getMessage();
		}
	}

}
